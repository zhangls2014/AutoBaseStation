package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * 基站信息数据结构体
 *
 * @author zhangls
 */
@Entity(tableName = "wcdma")
data class CellInfoWcdmaModel(
        /**
         * 数据产生的时间
         */
        @PrimaryKey
        var createTime: Long,
        /**
         * 是否上传该数据
         */
        var uploaded: Boolean,
        var wcdmaMnc: String,
        var wcdmaCid: String,
        var wcdmaPsc: String,
        var wcdmaLac: String,
        var wcdmaRssi: String,
        var wcdmaAsulevel: String,
        var wcdmaUarfcn: String)