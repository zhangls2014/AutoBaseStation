package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.CellInfoCdmaModel

/**
 * 测量的基站信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface CdmaDao {


    /**
     * 添加测量数据
     *
     * @param cellInfoModel 测量数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCellInfo(cellInfoModel: CellInfoCdmaModel)

    /**
     * 获取所有未上传的数据
     *
     * @return 未上传数据列表
     */
    @Query("SELECT * FROM cdma WHERE uploaded = 0 ORDER BY createTime ASC")
    fun getNotUploaded(): List<CellInfoCdmaModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM cdma ORDER BY createTime DESC")
    fun getAllCdma(): List<CellInfoCdmaModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM cdma ORDER BY createTime DESC")
    fun getAllCdmaData(): LiveData<List<CellInfoCdmaModel>>

    /**
     * 更新指定数据
     */
    @Update
    fun updateCdma(models: List<CellInfoCdmaModel>)

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM cdma WHERE createTime < :time")
    fun getDateCdma(time: Long): List<CellInfoCdmaModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deleteCdma(models: List<CellInfoCdmaModel>)
}