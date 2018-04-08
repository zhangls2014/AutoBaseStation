package com.zhangls.auto

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.zhangls.auto.dao.*
import com.zhangls.auto.model.*


/**
 * @author zhangls
 */
@Database(entities = [
    CellInfoGsmModel::class,
    CellInfoLteModel::class,
    CellInfoCdmaModel::class,
    CellInfoWcdmaModel::class,
    ConfigModel::class,
    LocationModel::class,
    PictureModel::class], version = 1)
abstract class AbstractDatabase : RoomDatabase() {

    /**
     * 获取测量数据
     *
     * @return GsmDao 可操作对象
     */
    abstract fun gsmDao(): GsmDao

    /**
     * 获取测量数据
     *
     * @return CdmaDao 可操作对象
     */
    abstract fun cdmaDao(): CdmaDao

    /**
     * 获取测量数据
     *
     * @return LteDao 可操作对象
     */
    abstract fun lteDao(): LteDao

    /**
     * 获取测量数据
     *
     * @return WcdmaDao 可操作对象
     */
    abstract fun wcdmaDao(): WcdmaDao

    /**
     * 获取配置信息
     *
     * @return ConfigDao 可操作对象
     */
    abstract fun configDao(): ConfigDao

    /**
     * 获取定位信息
     *
     * @return LocationDao 可操作对象
     */
    abstract fun locationDao(): LocationDao

    /**
     * 获取图片信息
     *
     * @return PictureDao 可操作对象
     */
    abstract fun pictureDao(): PictureDao


    companion object {

        private var mAppDatabase: AbstractDatabase? = null

        @Synchronized
        fun get(context: Context): AbstractDatabase {
            if (mAppDatabase == null) {
                mAppDatabase = Room
                        .databaseBuilder(context.applicationContext, AbstractDatabase::class.java, "BaseStation.db")
                        .build()
            }
            return mAppDatabase!!
        }
    }
}
