package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 定位数据结构体
 *
 * @author zhangls
 */
@Entity(tableName = "location")
data class LocationModel(
        /**
         * 数据产生的时间
         */
        @PrimaryKey
        var createTime: Long,
        /**
         * 是否上传该数据
         */
        var uploaded: Boolean,
        /**
         * 纬度
         */
        var latitude: Double,
        /**
         * 经度
         */
        var longitude: Double,
        /**
         * 定位经度
         */
        var accuracy: Float,
        /**
         * 海拔
         */
        var altitude: Double
)