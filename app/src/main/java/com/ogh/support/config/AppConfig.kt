package com.ogh.support.config

import com.blankj.utilcode.util.PathUtils
import java.io.File

object AppConfig {
    class FilePath {
        companion object {
            val FILE_FOLDER = PathUtils.getExternalAppDownloadPath() + File.separator //   /storage/emulated/0/Android/data/package/files/Download/
        }
    }
}