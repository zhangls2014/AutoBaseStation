package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 基站信息数据结构体
 *
 * @author zhangls
 */
@Entity(tableName = "cdma")
data class CellInfoCdmaModel(
        /**
         * 数据产生的时间
         */
        @PrimaryKey
        var createTime: Long,
        /**
         * 是否上传该数据
         */
        var uploaded: Boolean,
        var cdmaMnc: String,
        var cdmaBasestationId: String,
        var cdmaNetworkId: String,
        var cdmaSystemId: String,
        var cdmaRssi: String,
        var cdmaEcio: String,
        var cdmaEvdoDbm: String,
        var cdmaEvdoEcio: String,
        var cdmaAsulevel: String,
        var cdmaLatitude: String,
        var cdmaLongitude: String)