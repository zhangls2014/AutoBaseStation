package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * @author zhangls
 */
@Entity(tableName = "picture")
data class PictureModel(
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
         * 存储路径
         */
        var path: String)