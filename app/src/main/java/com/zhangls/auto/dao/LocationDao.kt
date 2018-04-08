package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.LocationModel

/**
 * 设备定位信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface LocationDao {

    /**
     * 添加定位数据
     *
     * @param locationModel 定位数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCellInfo(locationModel: LocationModel)

    /**
     * 获取所有未上传的数据
     *
     * @return 未上传数据列表
     */
    @Query("SELECT * FROM location WHERE uploaded = 0 ORDER BY createTime ASC")
    fun getNotUploaded(): List<LocationModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM location ORDER BY createTime DESC")
    fun getAllLocation(): List<LocationModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM location ORDER BY createTime DESC")
    fun getAllLocationData(): LiveData<List<LocationModel>>

    /**
     * 更新指定数据
     */
    @Update
    fun updateLocation(models: List<LocationModel>)

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM location WHERE createTime < :time")
    fun getDateLocation(time: Long): List<LocationModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deleteLocation(models: List<LocationModel>)
}