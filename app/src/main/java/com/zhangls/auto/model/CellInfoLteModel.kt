package com.zhangls.auto.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 基站信息数据结构体
 *
 * @author zhangls
 */
@Entity(tableName = "lte")
data class CellInfoLteModel(
        /**
         * 数据产生的时间
         */
        @PrimaryKey
        var createTime: Long,
        /**
         * 是否上传该数据
         */
        var uploaded: Boolean,
        var lteMnc: String,
        var lteCi: String,
        var ltePci: String,
        var lteTac: String,
        var lteAsulevel: String,
        var lteRsrp: String,
        var lteRsrq: String,
        var lteEarfcn: String)