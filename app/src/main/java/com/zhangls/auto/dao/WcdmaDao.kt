package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.CellInfoWcdmaModel

/**
 * 测量的基站信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface WcdmaDao {


    /**
     * 添加测量数据
     *
     * @param cellInfoModel 测量数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCellInfo(cellInfoModel: CellInfoWcdmaModel)

    /**
     * 获取所有未上传的数据
     *
     * @return 未上传数据列表
     */
    @Query("SELECT * FROM wcdma ORDER BY createTime ASC")
    fun getNotUploaded(): List<CellInfoWcdmaModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM wcdma ORDER BY createTime DESC")
    fun getAllWcdma(): List<CellInfoWcdmaModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM wcdma ORDER BY createTime DESC")
    fun getAllWcdmaData(): LiveData<List<CellInfoWcdmaModel>>

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM wcdma WHERE createTime < :time")
    fun getDateWcdma(time: Long): List<CellInfoWcdmaModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deleteWcdma(models: List<CellInfoWcdmaModel>)

    /**
     * 更新指定数据
     */
    @Update
    fun updateWcdma(models: List<CellInfoWcdmaModel>)
}