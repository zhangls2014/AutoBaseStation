package com.zhangls.auto.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.zhangls.auto.AbstractDatabase
import com.zhangls.auto.R
import com.zhangls.auto.model.ConfigModel
import com.zhangls.auto.service.MeasureService
import com.zhangls.auto.service.UpdateService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {

    private val myHandler = Handler()
    private val mLoadingRunnable = Runnable {
        initLauncher()
    }
    /**
     * 数据库操作对象
     */
    private lateinit var database: AbstractDatabase
    /**
     * 应用配置
     */
    private lateinit var configModel: ConfigModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        window.decorView.post { myHandler.post(mLoadingRunnable) }
    }

    override fun onDestroy() {
        super.onDestroy()
        MeasureService.stopService(this)
        UpdateService.stopService(this)
        database.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.menu_album) {
            AlbumActivity.activityStart(this)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * 启动时缓加载的数据，操作
     */
    private fun initLauncher() {
        // 启动时检查权限
        getPermission(false)
        // 异步加载初始化数据库资源
        Observable.create<ConfigModel> {
            database = AbstractDatabase.get(this)
            val configModel = database.configDao().getConfig()
            this.configModel = configModel
            it.onNext(configModel)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // 初始化配置的默认值
                    initConfig(it)

                    // 初始化测量按钮
                    if (measureSwitch.isChecked)
                        tvContentSwitch.text = getString(R.string.contentSwitchOn)
                    else
                        tvContentSwitch.text = getString(R.string.contentSwitchOff)
                    measureSwitch.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            tvContentSwitch.text = getString(R.string.contentSwitchOn)
                            getPermission(true)
                        } else {
                            tvContentSwitch.text = getString(R.string.contentSwitchOff)
                            MeasureService.stopService(this)
                        }
                    }

                    // 点击设置监听
                    llMeasureCycle.setOnClickListener({ measureClick(configModel.measureCycle) })
                    llUploadCycle.setOnClickListener({ uploadClick(configModel.uploadCycle) })
                    llSaveTime.setOnClickListener({ saveTimeClick(configModel.saveTime) })

                    // 配置监听，配置变化时更新内存中的配置信息
                    database.configDao().getConfigLiveData().observe(this, Observer {
                        if (it == null)
                            return@Observer
                        else {
                            configModel = it
                            initConfig(it)
                        }
                    })

                    // 启动数据更新服务
                    UpdateService.startService(this)
                })

        // 初始化布局
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = sectionsPagerAdapter
        container.offscreenPageLimit = 5
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }


    /**
     * 初始化配置信息
     */
    private fun initConfig(configModel: ConfigModel) {
        // 初始化配置的默认值
        val measureCycle = resources.getStringArray(R.array.measureCycle)
        if (configModel.measureCycle < measureCycle.size && configModel.measureCycle > 0)
            tvMeasureCycle.text = measureCycle[configModel.measureCycle]
        else
            tvMeasureCycle.text = measureCycle[0]

        val uploadCycle = resources.getStringArray(R.array.uploadCycle)
        if (configModel.uploadCycle < uploadCycle.size && configModel.uploadCycle > 0)
            tvUploadCycle.text = uploadCycle[configModel.uploadCycle]
        else
            tvUploadCycle.text = uploadCycle[0]

        val saveTime = resources.getStringArray(R.array.saveTime)
        if (configModel.saveTime < saveTime.size && configModel.saveTime > 0)
            tvSaveTime.text = saveTime[configModel.saveTime]
        else
            tvSaveTime.text = saveTime[0]
    }


    /**
     * 显示测量间隔对话框
     */
    private fun measureClick(index: Int) {
        val checkedItem = intArrayOf(0)
        AlertDialog.Builder(this)
                .setTitle(R.string.titleMeasureCycle)
                .setSingleChoiceItems(
                        resources.getStringArray(R.array.measureCycle),
                        index,
                        { _, which -> checkedItem[0] = which })
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    if (index == checkedItem[0])
                        return@setPositiveButton
                    else
                        doAsync {
                            database.configDao().insertConfig(
                                    ConfigModel(
                                            configModel.id,
                                            checkedItem[0],
                                            configModel.uploadCycle,
                                            configModel.saveTime))
                        }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .create()
                .show()
    }

    /**
     * 显示上传间隔对话框
     */
    private fun uploadClick(index: Int) {
        val checkedItem = intArrayOf(0)
        AlertDialog.Builder(this)
                .setTitle(R.string.titleUploadCycle)
                .setSingleChoiceItems(
                        resources.getStringArray(R.array.uploadCycle),
                        index,
                        { _, which -> checkedItem[0] = which })
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    if (index == checkedItem[0])
                        return@setPositiveButton
                    else
                        doAsync {
                            database.configDao().insertConfig(
                                    ConfigModel(
                                            configModel.id,
                                            configModel.measureCycle,
                                            checkedItem[0],
                                            configModel.saveTime))
                        }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .create()
                .show()
    }

    /**
     * 显示数据保存时间对话框
     */
    private fun saveTimeClick(index: Int) {
        val checkedItem = intArrayOf(0)
        AlertDialog.Builder(this)
                .setTitle(R.string.titleDataSaveTime)
                .setSingleChoiceItems(
                        resources.getStringArray(R.array.saveTime),
                        index,
                        { _, which -> checkedItem[0] = which })
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    if (index == checkedItem[0])
                        return@setPositiveButton
                    else
                        doAsync {
                            database.configDao().insertConfig(
                                    ConfigModel(
                                            configModel.id,
                                            configModel.measureCycle,
                                            configModel.uploadCycle,
                                            checkedItem[0]))
                        }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .create()
                .show()
    }

    /**
     * 应用启动时，先检查权限
     *
     * @param isMeasure 是否是点击测量开关时调用
     */
    private fun getPermission(isMeasure: Boolean) {
        AndPermission.with(this)
                .permission(
                        Permission.CAMERA,
                        Permission.READ_PHONE_STATE,
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted({
                    if (isMeasure) MeasureService.startService(this)
                })
                .onDenied({ permissions ->
                    // 没有权限，无法测量
                    measureSwitch.isChecked = false

                    if (AndPermission.hasAlwaysDeniedPermission(this, permissions)) {
                        // 权限申请被拒绝时，检查，若勾选了始终拒绝权限授予，则弹出提示框
                        val settingService = AndPermission.permissionSetting(this)
                        val fragment = CommonDialogFragment.newInstance(getString(R.string.permission_apply_introduce))
                        fragment.setPositiveListener { settingService.execute() }
                        fragment.setNegativeListener { settingService.cancel() }
                        fragment.show(supportFragmentManager, null)
                    }
                })
                .rationale({ _, _, executor ->
                    // 弹出权限申请说明提示框
                    val fragment = CommonDialogFragment.newInstance(getString(R.string.permission_apply_introduce))
                    fragment.setPositiveListener { executor.execute() }
                    fragment.setNegativeListener { executor.cancel() }
                    fragment.show(supportFragmentManager, null)
                })
                .start()
    }


    /**
     * ViewPager adapter
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return ItemFragment.newInstance(
                    when (position) {
                        0 -> ItemFragment.ACTION_GSM
                        1 -> ItemFragment.ACTION_LTE
                        2 -> ItemFragment.ACTION_CDMA
                        3 -> ItemFragment.ACTION_WCDMA
                        else -> ItemFragment.ACTION_LOCATION
                    })
        }

        override fun getCount(): Int {
            return 5
        }
    }
}
