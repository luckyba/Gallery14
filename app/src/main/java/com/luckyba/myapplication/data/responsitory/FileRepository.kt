package com.luckyba.myapplication.data.responsitory

import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.provider.MediaReader
import com.luckyba.myapplication.util.StringUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*

class FileRepository(val context: Context):RepositoryInterface {
    private var mMedia: MediaReader = MediaReader(context)
    private val external = MediaStore.Files.getContentUri("external")
    private val destSource = "/storage/emulated/0/"

    //this is for test
    override fun getAllMedia(): ArrayList<AlbumFile> {
        TODO("Not yet implemented")
    }

    override fun getMedias(): ArrayList<AlbumFile> {
        TODO("Not yet implemented")
    }

    override fun getAlbums(): ArrayList<AlbumFolder> {
        return mMedia.allMedia
    }

    override fun getHiddenAlbums(): ArrayList<AlbumFolder> {
        TODO("Not yet implemented")
    }

    override fun deleteListFile(listPath: MutableSet<String>): Boolean {
        try {
            for(path in listPath) {
                deleteFileOrDirectory(path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true

    }

    private fun deleteFileOrDirectory(srcDir: String) {
        try {
            val deleteFile = File(srcDir)
            if (deleteFile.isDirectory) {
                val files = deleteFile.list()
                val filesLength = deleteFile.list().size
                for (i in 0 until filesLength) {
                    val src1 = File(deleteFile, files[i]).path
                    deleteFileOrDirectory(src1)
                }
                deleteFile.delete()

            } else {
                deleteFile.delete()
            }
            context.contentResolver.delete(
                external, MediaStore.MediaColumns.DATA + "=?", arrayOf(
                    deleteFile.path
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deleteAlbum(listPath: MutableSet<String>): Boolean {
        TODO("Not yet implemented")
    }

    override fun moveFileToAlbum(listPath: MutableSet<String>, outPath: String): Boolean {
        return try {
            for(i in 0 until listPath.size) {
                copyFileOrDirectory(listPath.elementAt(i), outPath)
                deleteFileOrDirectory(listPath.elementAt(i))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun copyFile(listPath: MutableSet<String>, outPath: String): Boolean {
        return try {
            for(i in 0 until listPath.size) {
                copyFileOrDirectory(listPath.elementAt(i), outPath)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override fun createAlbum(albumName: String, listPath: MutableSet<String>): Boolean {
        var newAlbum: String = albumName;
        if (albumName.isEmpty()) {
            newAlbum = "New Album"
        }
        val file = File(external.toString())
        return if (file.createNewFile()) {
            copyFile(listPath, file.absolutePath)
            true
        } else {
            false
        }
    }

    override fun reName(path: String, oldName: String, newName: String): Boolean {
        if (oldName == newName) return true

        return try {
            val fileFrom = File(path)
            val fileTo = File(StringUtils.getAlbumPathRenamed(path, newName))
            fileFrom.renameTo(fileTo)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

    private fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun scanFile(context: Context, path: Array<String?>?) {
        MediaScannerConnection.scanFile(context.applicationContext, path, null, null)
    }
}