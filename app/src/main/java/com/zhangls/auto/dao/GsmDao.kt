package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.zhangls.auto.model.CellInfoGsmModel

/**
 * 测量的基站信息 Dao 类
 *
 * @author zhangls
 */
@Dao
interface GsmDao {

    /**
     * 添加测量数据
     *
     * @param cellInfoModel 测量数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCellInfo(cellInfoModel: CellInfoGsmModel)

    /**
     * 查询某条数据
     *
     * @param createTime 数据产生时间
     * @return CellInfoWcdmaModel
     */
    @Query("SELECT * FROM gsm WHERE createTime = :createTime LIMIT 1")
    fun queryByCreateTime(createTime: String): CellInfoGsmModel

    /**
     * 获取所有未上传的数据
     *
     * @return 未上传数据列表
     */
    @Query("SELECT * FROM gsm WHERE uploaded = 0 ORDER BY createTime ASC")
    fun getNotUploaded(): List<CellInfoGsmModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM gsm ORDER BY createTime DESC")
    fun getAllGsm(): List<CellInfoGsmModel>

    /**
     * 获取所有数据
     *
     * @return 数据列表
     */
    @Query("SELECT * FROM gsm ORDER BY createTime DESC")
    fun getAllGsmData(): LiveData<List<CellInfoGsmModel>>

    /**
     * 获取小于指定时间段的数据
     */
    @Query("SELECT * FROM gsm WHERE createTime < :time")
    fun getDateGsm(time: Long): List<CellInfoGsmModel>

    /**
     * 删除指定数据
     */
    @Delete
    fun deleteGsm(models: List<CellInfoGsmModel>)

    /**
     * 更新指定数据
     */
    @Update
    fun updateGsm(models: List<CellInfoGsmModel>)
}