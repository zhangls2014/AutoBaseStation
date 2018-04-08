package com.zhangls.auto.service

import android.Manifest
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.telephony.*
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.zhangls.auto.AbstractDatabase
import com.zhangls.auto.R
import com.zhangls.auto.model.*
import com.zhangls.auto.view.CameraActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync

/**
 * 测量数据服务，在后台运行，不与界面发生数据交换
 *
 * @author zhangls
 */
class MeasureService : LifecycleService() {

    private var isMeasuring = false
    private lateinit var mTelephonyManager: TelephonyManager
    private lateinit var database: AbstractDatabase
    /**
     * 应用配置
     */
    private lateinit var configModel: ConfigModel
    private val mHandler = Handler()
    private lateinit var mRunnable: Runnable
    private lateinit var client: AMapLocationClient
    private val locationListener = AMapLocationListener {
        if (it != null) {
            val locationModel = LocationModel(
                    System.currentTimeMillis(),
                    false,
                    it.latitude,
                    it.longitude,
                    it.accuracy,
                    it.altitude)
            doAsync { database.locationDao().insertCellInfo(locationModel) }
        }

        CameraActivity.activityStart(applicationContext)
    }


    companion object {
        // 定义常量
        const val ACTION_START = "com.zhangls.auto.action.measure_start"
        const val ACTION_STOP = "com.zhangls.auto.action.measure_stop"
        const val INTERVAL_UNIT = 1000L

        /**
         * 启动服务，开始数据收集
         */
        fun startService(context: Context) {
            val intent = Intent(context, MeasureService::class.java)
            intent.action = ACTION_START
            context.startService(intent)
        }

        /**
         * 停止服务，结束数据收集
         */
        fun stopService(context: Context) {
            val intent = Intent(context, MeasureService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mTelephonyManager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // 初始化高德定位组件
        val options = AMapLocationClientOption()
        // 高精度模式
        options.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        // 30 秒定位一次，暂时固定设置
        options.interval = 30_000
        // 是否是一次性定位
        options.isOnceLocation = false
        // 主动刷新设备 wifi 模块
        options.isWifiScan = true
        // 不需要返回位置信息
        options.isNeedAddress = false
        client = AMapLocationClient(applicationContext)
        client.setLocationOption(options)
        client.setLocationListener(locationListener)

        // 异步加载初始化数据库资源
        Observable.create<ConfigModel> {
            database = AbstractDatabase.get(this)
            var configModel = database.configDao().getConfig()
            if (configModel == null) {
                configModel = ConfigModel(1, 0, 0, 0)
                database.configDao().insertConfig(configModel)
            }
            this.configModel = configModel
            it.onNext(configModel)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // 配置监听，配置变化时更新内存中的配置信息
                    database.configDao().getConfigLiveData()?.observe(this, Observer {
                        if (it == null)
                            return@Observer
                        else {
                            configModel = it
                        }
                    })
                })

        mRunnable = Runnable {
            startMeasure()
            if (isMeasuring) mHandler.postDelayed(mRunnable, initConfig(configModel) * INTERVAL_UNIT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!isMeasuring) mHandler.post(mRunnable)
            }
            ACTION_STOP -> {
                stopMeasure()
                stopSelf()
            }
            else -> {
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 停止数据测量
     */
    private fun stopMeasure() {
        isMeasuring = false
        client.stopLocation()
        mHandler.removeCallbacks(mRunnable)
    }


    /**
     * 开始数据测量
     */
    private fun startMeasure() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return

        isMeasuring = true

        // 开始定位
        if (!client.isStarted) client.startLocation()

        val cellInfos = mTelephonyManager.allCellInfo
        for (cellInfo in cellInfos) {
            if (!cellInfo.isRegistered) continue

            when (cellInfo) {
                is CellInfoGsm -> {
                    val cellIdentity = cellInfo.cellIdentity
                    val cellSignalStrengthGsm = cellInfo.cellSignalStrength

                    val gsmModel = CellInfoGsmModel(
                            System.currentTimeMillis(),
                            false,
                            cellIdentity.mnc.toString(),
                            cellIdentity.cid.toString(),
                            cellIdentity.lac.toString(),
                            cellSignalStrengthGsm.dbm.toString(),
                            cellSignalStrengthGsm.asuLevel.toString(),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) cellIdentity.arfcn.toString() else "",
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) cellIdentity.bsic.toString() else "")
                    database.gsmDao().insertCellInfo(gsmModel)
                }
                is CellInfoCdma -> {
                    val cellIdentity = cellInfo.cellIdentity
                    val cellSignalStrength = cellInfo.cellSignalStrength

                    val cdmaModel = CellInfoCdmaModel(
                            System.currentTimeMillis(),
                            false,
                            "cdma",
                            cellIdentity.basestationId.toString(),
                            cellIdentity.networkId.toString(),
                            cellIdentity.systemId.toString(),
                            cellSignalStrength.cdmaDbm.toString(),
                            cellSignalStrength.cdmaEcio.toString(),
                            cellSignalStrength.evdoDbm.toString(),
                            cellSignalStrength.evdoEcio.toString(),
                            cellSignalStrength.asuLevel.toString(),
                            cellIdentity.latitude.toString(),
                            cellIdentity.longitude.toString())
                    doAsync { database.cdmaDao().insertCellInfo(cdmaModel) }
                }
                is CellInfoLte -> {
                    val cellIdentity = cellInfo.cellIdentity
                    val cellSignalStrength = cellInfo.cellSignalStrength

                    val lteModel = CellInfoLteModel(
                            System.currentTimeMillis(),
                            false,
                            cellIdentity.mnc.toString(),
                            cellIdentity.ci.toString(),
                            cellIdentity.pci.toString(),
                            cellIdentity.tac.toString(),
                            cellSignalStrength.asuLevel.toString(),
                            cellSignalStrength.dbm.toString(),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) cellSignalStrength.rsrp.toString() else "",
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) cellIdentity.earfcn.toString() else "")
                    doAsync { database.lteDao().insertCellInfo(lteModel) }
                }
                is CellInfoWcdma -> {
                    val cellIdentity = cellInfo.cellIdentity
                    val cellSignalStrength = cellInfo.cellSignalStrength

                    val wcdmaModel = CellInfoWcdmaModel(
                            System.currentTimeMillis(),
                            false,
                            cellIdentity.mnc.toString(),
                            cellIdentity.cid.toString(),
                            cellIdentity.psc.toString(),
                            cellIdentity.lac.toString(),
                            cellSignalStrength.dbm.toString(),
                            cellSignalStrength.asuLevel.toString(),
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) cellIdentity.uarfcn.toString() else "")
                    doAsync { database.wcdmaDao().insertCellInfo(wcdmaModel) }
                }
            }
        }
    }


    /**
     * 初始化配置信息
     */
    private fun initConfig(configModel: ConfigModel): Int {
        // 初始化配置的默认值
        val measureSecond = resources.getIntArray(R.array.measureSecond)
        return if (configModel.measureCycle < measureSecond.size && configModel.measureCycle > 0) {
            measureSecond[configModel.measureCycle]
        } else {
            measureSecond[0]
        }
    }
}