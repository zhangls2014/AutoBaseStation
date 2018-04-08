package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * 基站信息数据结构体
 *
 * @author zhangls
 */
@Entity(tableName = "gsm")
data class CellInfoGsmModel(
        /**
         * 数据产生的时间
         */
        @PrimaryKey
        var createTime: Long,
        /**
         * 是否上传该数据
         */
        var uploaded: Boolean,
        var gsmMnc: String,
        var gsmCid: String,
        var gsmLac: String,
        var gsmRssi: String,
        var gsmAsulevel: String,
        var gsmArfcn: String,
        var gsmBasic: String)