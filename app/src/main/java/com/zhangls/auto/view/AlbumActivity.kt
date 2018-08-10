package com.zhangls.auto.view

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.view.View
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.zhangls.auto.AbstractDatabase
import com.zhangls.auto.FileUtil
import com.zhangls.auto.OnItemClickListener
import com.zhangls.auto.R
import com.zhangls.auto.model.PictureModel
import com.zhangls.auto.type.PictureViewBinder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_album.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import org.jetbrains.anko.doAsync
import java.io.File


class AlbumActivity : AppCompatActivity() {

    private val items = Items()
    private val adapter = MultiTypeAdapter(items)
    private var database: AbstractDatabase? = null


    companion object {

        fun activityStart(activity: Activity) {
            val intent = Intent(activity, AlbumActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        // 设置顶部导航栏
        toolbar.title = getString(R.string.titleActivityAlbum)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.layoutManager = GridLayoutManager(this, 2)

        adapter.register(PictureModel::class.java, PictureViewBinder(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (items[position] is PictureModel) {
                    val model = items[position] as PictureModel
                    doAsync {
                        // 删除数据库信息
                        database!!.pictureDao().deletePicture(arrayListOf(model))
                        // 删除文件
                        val file = File(model.path)
                        FileUtil.deleteFiles(file)
                    }
                }
            }
        }))
        recycler.adapter = adapter

        getPermissions()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item!!.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    /**
     * 获取存储空间写入权限
     */
    private fun getPermissions() {
        AndPermission.with(this)
                .permission(
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted { refreshData() }
                .onDenied { permissions ->
                    if (AndPermission.hasAlwaysDeniedPermission(this, permissions)) {
                        // 权限申请被拒绝时，检查，若勾选了始终拒绝权限授予，则弹出提示框
                        val settingService = AndPermission.permissionSetting(this)
                        val fragment = CommonDialogFragment.newInstance(getString(R.string.permission_camera_introduce))
                        fragment.setPositiveListener { settingService.execute() }
                        fragment.setNegativeListener { settingService.cancel() }
                        fragment.show(supportFragmentManager, null)
                    }
                }
                .rationale { _, _, executor ->
                    // 弹出权限申请说明提示框
                    val fragment = CommonDialogFragment.newInstance(getString(R.string.permission_camera_introduce))
                    fragment.setPositiveListener { executor.execute() }
                    fragment.setNegativeListener { executor.cancel() }
                    fragment.show(supportFragmentManager, null)
                }
                .start()
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        Observable.create<Any> { it ->
            if (database == null) database = AbstractDatabase.get(this)

            items.addAll(database!!.pictureDao().getAllPicture())

            // 观察数据，每次数据变化时刷新列表数据
            database!!.pictureDao().getAllPictureData().observe(this, Observer {
                items.clear()
                adapter.notifyDataSetChanged()
                if (it != null) {
                    items.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapter.notifyDataSetChanged() }
    }
}
