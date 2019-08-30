package com.trulden.friends.util

import java.io.File
import java.io.FileOutputStream

fun copyDataFromOneToAnother(fromPath: String, toPath: String) {
    val inStream = File(fromPath).inputStream()
    val outStream = FileOutputStream(toPath)

    inStream.use { input ->
        outStream.use { output ->
            input.copyTo(output)
        }
    }
}