package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.PictureModel

/**
 * 拍照信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface PictureDao {

    /**
     * 添加拍照数据
     *
     * @param pictureModel 拍照数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicture(pictureModel: PictureModel)

    /**
     * 获取所有未上传的数据
     *
     * @return 未上传数据列表
     */
    @Query("SELECT * FROM picture WHERE uploaded = 0 ORDER BY createTime ASC")
    fun getNotUploaded(): List<PictureModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM picture ORDER BY createTime DESC")
    fun getAllPicture(): List<PictureModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM picture ORDER BY createTime DESC")
    fun getAllPictureData(): LiveData<List<PictureModel>>

    /**
     * 更新指定数据
     */
    @Update
    fun updatePicture(models: List<PictureModel>)

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM picture WHERE createTime < :time")
    fun getDatePicture(time: Long): List<PictureModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deletePicture(models: List<PictureModel>)
}