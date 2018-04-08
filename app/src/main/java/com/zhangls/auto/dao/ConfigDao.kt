package com.zhangls.auto.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.zhangls.auto.model.ConfigModel

/**
 * 应用配置 Dao 类
 *
 * @author zhangls
 */
@Dao
interface ConfigDao {

    /**
     * 添加配置
     *
     * @param configDao 配置参数
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfig(configDao: ConfigModel)

    /**
     * 查询配置
     */
    @Query("SELECT * FROM config WHERE id = 1 LIMIT 1")
    fun getConfig(): ConfigModel?

    /**
     * 查询配置
     */
    @Query("SELECT * FROM config WHERE id = 1 LIMIT 1")
    fun getConfigLiveData(): LiveData<ConfigModel>?
}