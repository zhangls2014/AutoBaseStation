package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.CellInfoLteModel

/**
 * 测量的基站信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface LteDao {


    /**
     * 添加测量数据
     *
     * @param cellInfoModel 测量数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCellInfo(cellInfoModel: CellInfoLteModel)

    /**
     * 获取未上传的数据
     */
    @Query("SELECT * FROM lte ORDER BY createTime ASC")
    fun getNotUploaded(): List<CellInfoLteModel>

    /**
     * 获取数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM lte ORDER BY createTime DESC")
    fun getAllLte(): List<CellInfoLteModel>

    /**
     * 获取数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM lte ORDER BY createTime DESC")
    fun getAllLteData(): LiveData<List<CellInfoLteModel>>

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM lte WHERE createTime < :time")
    fun getDateLte(time: Long): List<CellInfoLteModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deleteLte(models: List<CellInfoLteModel>)

    /**
     * 更新指定数据
     */
    @Update
    fun updateLte(models: List<CellInfoLteModel>)
}