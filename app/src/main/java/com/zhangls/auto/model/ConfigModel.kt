package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 应用配置数据
 *
 * @author zhangls
 */
@Entity(tableName = "config")
data class ConfigModel(
        @PrimaryKey
        var id: Int,
        /**
         * 数据测量周期，序号
         */
        var measureCycle: Int,
        /**
         * 数据上传周期，序号
         */
        var uploadCycle: Int,
        /**
         * 数据本地保存时间，序号
         */
        var saveTime: Int
)