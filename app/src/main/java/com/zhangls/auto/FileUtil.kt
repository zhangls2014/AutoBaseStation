package com.zhangls.auto

import java.io.File


/**
 * 文件管理工具类
 *
 * @author zhangls
 */
class FileUtil {

    companion object {

        /**
         * 删除该文件夹以及该文件夹下所有的文件，或者删除该文件
         *
         * @param file File
         */
        fun deleteFiles(file: File) {
            if (!file.exists()) {
                return
            }
            if (file.isDirectory) {
                val childFile = file.listFiles()
                if (childFile == null || childFile.isEmpty()) {
                    file.delete()
                    return
                }
                for (f in childFile) {
                    if (f.isFile) {
                        f.delete()
                    } else if (f.isDirectory) {
                        deleteFiles(f)
                    }
                }
                file.delete()
            } else {
                file.delete()
            }
        }
    }

}