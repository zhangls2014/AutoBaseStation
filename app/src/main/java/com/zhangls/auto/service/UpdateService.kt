package com.zhangls.auto.service

import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.zhangls.auto.AbstractDatabase
import com.zhangls.auto.FileUtil
import com.zhangls.auto.R
import com.zhangls.auto.model.ConfigModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import java.io.File


/**
 * 数据更新服务，用于自动上传数据，删除超时数据
 *
 * @author zhangls
 */
class UpdateService : LifecycleService() {

    private lateinit var database: AbstractDatabase
    /**
     * 应用配置
     */
    private lateinit var configModel: ConfigModel
    private lateinit var mHandler: Handler


    companion object {
        // 定义常量
        const val ACTION_START = "com.zhangls.auto.action.update_start"
        const val ACTION_STOP = "com.zhangls.auto.action.update_stop"
        const val INTERVAL_UNIT = 1000L

        private const val MESSAGE_DELETE_DATA = 1
        private const val MESSAGE_UPLOAD_DATA = 2

        /**
         * 启动服务，开始数据更新
         */
        fun startService(context: Context) {
            val intent = Intent(context, UpdateService::class.java)
            intent.action = ACTION_START
            context.startService(intent)
        }

        /**
         * 停止服务，结束数据更新
         */
        fun stopService(context: Context) {
            val intent = Intent(context, UpdateService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        mHandler = Handler {
            when (it.what) {
                MESSAGE_UPLOAD_DATA -> {
                    uploadData()
                    mHandler.sendEmptyMessageDelayed(MESSAGE_UPLOAD_DATA,
                            initUploadConfig(configModel) * INTERVAL_UNIT)
                }
                MESSAGE_DELETE_DATA -> {
                    deleteData()
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DELETE_DATA,
                            initSaveConfig(configModel) * INTERVAL_UNIT)
                }
                else -> {
                    false
                }
            }
        }

        // 异步加载初始化数据库资源
        Observable.create<ConfigModel> {
            database = AbstractDatabase.get(this)
            val configModel = database.configDao().getConfig()
            this.configModel = configModel
            it.onNext(configModel)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    // 配置监听，配置变化时更新内存中的配置信息
                    database.configDao().getConfigLiveData().observe(this, Observer {
                        if (it == null)
                            return@Observer
                        else {
                            configModel = it
                        }
                    })

                    // 第一次启动时，开始数据轮询
                    mHandler.sendEmptyMessageDelayed(MESSAGE_UPLOAD_DATA,
                            (initUploadConfig(configModel) * INTERVAL_UNIT))
                    mHandler.sendEmptyMessage(MESSAGE_DELETE_DATA)
                }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            UpdateService.ACTION_START -> {
            }
            UpdateService.ACTION_STOP -> {
                stopUpdate()
            }
            else -> {
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 停止数据测量
     */
    private fun stopUpdate() {
        mHandler.removeMessages(MESSAGE_UPLOAD_DATA)
        mHandler.removeMessages(MESSAGE_DELETE_DATA)
        stopSelf()
    }

    /**
     * 初始化配置信息
     *
     * @return 更新周期
     */
    private fun initUploadConfig(configModel: ConfigModel): Int {
        // 初始化配置的默认值
        val uploadSecond = resources.getIntArray(R.array.uploadSecond)
        return if (configModel.uploadCycle < uploadSecond.size && configModel.uploadCycle > 0) {
            uploadSecond[configModel.uploadCycle]
        } else {
            uploadSecond[0]
        }
    }

    /**
     * 初始化配置信息
     *
     * @return 数据保存时间
     */
    private fun initSaveConfig(configModel: ConfigModel): Int {
        // 初始化配置的默认值
        val saveSecond = resources.getIntArray(R.array.saveSecond)
        return if (configModel.saveTime < saveSecond.size && configModel.saveTime > 0) {
            saveSecond[configModel.saveTime]
        } else {
            saveSecond[0]
        }
    }

    /**
     * 上传数据
     */
    private fun uploadData() {
        doAsync {
            val notUploadedCdma = database.cdmaDao().getNotUploaded()
            if (!notUploadedCdma.isEmpty()) {
                for (model in notUploadedCdma) {
                    model.uploaded = true
                }
                database.cdmaDao().updateCdma(notUploadedCdma)
            }

            val notUploadedWcdma = database.wcdmaDao().getNotUploaded()
            if (!notUploadedWcdma.isEmpty()) {
                for (model in notUploadedWcdma) {
                    model.uploaded = true
                }
                database.wcdmaDao().updateWcdma(notUploadedWcdma)
            }

            val notUploadedLte = database.lteDao().getNotUploaded()
            if (!notUploadedLte.isEmpty()) {
                for (model in notUploadedLte) {
                    model.uploaded = true
                }
                database.lteDao().updateLte(notUploadedLte)
            }

            val notUploadedGsm = database.gsmDao().getNotUploaded()
            if (!notUploadedGsm.isEmpty()) {
                for (model in notUploadedGsm) {
                    model.uploaded = true
                }
                database.gsmDao().updateGsm(notUploadedGsm)
            }

            val locationModel = database.locationDao().getNotUploaded()
            if (!locationModel.isEmpty()) {
                for (model in locationModel) {
                    model.uploaded = true
                }
                database.locationDao().updateLocation(locationModel)
            }

            val pictureModel = database.pictureDao().getNotUploaded()
            if (!pictureModel.isEmpty()) {
                for (model in pictureModel) {
                    model.uploaded = true
                }
                database.pictureDao().updatePicture(pictureModel)
            }
        }
    }

    /**
     * 删除数据
     */
    private fun deleteData() {
        doAsync {
            val time = System.currentTimeMillis() - initSaveConfig(configModel) * INTERVAL_UNIT
            // 删除 cdma 表中的数据
            val cdmaModel = database.cdmaDao().getDateCdma(time)
            if (cdmaModel.isNotEmpty()) {
                database.cdmaDao().deleteCdma(cdmaModel)
            }
            // 删除 wcdma 表中的数据
            val wcdmaModel = database.wcdmaDao().getDateWcdma(time)
            if (wcdmaModel.isNotEmpty()) {
                database.wcdmaDao().deleteWcdma(wcdmaModel)
            }
            // 删除 gsm 表中的数据
            val gsmModel = database.gsmDao().getDateGsm(time)
            if (gsmModel.isNotEmpty()) database.gsmDao().deleteGsm(gsmModel)
            // 删除 lte 表中的数据
            val lteModel = database.lteDao().getDateLte(time)
            if (lteModel.isNotEmpty()) {
                database.lteDao().deleteLte(lteModel)
            }
            // 删除 location 表中的数据
            val locationModel = database.locationDao().getDateLocation(time)
            if (locationModel.isNotEmpty()) database.locationDao().deleteLocation(locationModel)

            // 删除 picture 表中的数据
            val pictureModel = database.pictureDao().getDatePicture(time)
            if (locationModel.isNotEmpty()) {
                database.pictureDao().deletePicture(pictureModel)
                // 删除本地图片
                for (model in pictureModel) {
                    FileUtil.deleteFiles(File(model.path))
                }
            }

        }
    }
}