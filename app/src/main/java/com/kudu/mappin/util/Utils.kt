package com.kudu.mappin.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class Utils {
    fun getArchiveProjectName(`in`: InputStream?): String {
        var projectName = ""
        try {
            val zin = ZipInputStream(`in`)
            var entry: ZipEntry
            while (zin.nextEntry.also { entry = it } != null) {
                val entryName: String = entry.name.lowercase()
                if (entryName.endsWith(".qgs") || entryName.endsWith(".qgz")) {
                    projectName = entry.name
                    break
                }
            }
            zin.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return projectName
    }

    private fun documentFileToFolder(
        directory: DocumentFile,
        folder: String,
        resolver: ContentResolver,
    ): Boolean {
        val files = directory.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                val directoryPath = folder + file.name + "/"
                File(directoryPath).mkdir()
                val success = documentFileToFolder(file, directoryPath, resolver)
                if (!success) {
                    return false
                }
            } else {
                val filePath = folder + file.name
                try {
                    val input: InputStream? = resolver.openInputStream(file.uri)
                    inputStreamToFile(input, filePath,
                        file.length())
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }

    private fun fileToDocumentFile(
        file: File, directory: DocumentFile?,
        resolver: ContentResolver,
    ): Boolean {
        val files: Array<File> = if (file.isDirectory) file.listFiles()!! else arrayOf(file)
        for (f in files) {
            val filePath: String = f.path
            val fileName: String = f.name
            if (f.isDirectory) {
                // Use pre-existing directory if present
                var newDirectory = directory!!.findFile(fileName)
                if (newDirectory == null) {
                    newDirectory = directory.createDirectory(fileName)
                }
                val success = fileToDocumentFile(f, newDirectory, resolver)
                if (!success) {
                    return false
                }
            } else {
                var extension = ""
                var mimeType: String? = ""
                if (fileName.lastIndexOf(".") > -1) {
                    extension = fileName.substring(fileName.lastIndexOf(".") + 1)
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        extension)
                }
                // Use pre-existing file if present
                var documentFile = directory!!.findFile(fileName)
                if (documentFile == null) {
                    documentFile = directory.createFile(mimeType!!, fileName)
                }
                try {
                    val input: InputStream = FileInputStream(f)
                    val output: OutputStream? = resolver.openOutputStream(documentFile!!.uri)
                    inputStreamToOutputStream(input, output,
                        f.length())
                    output?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }

    private fun zipToFolder(`in`: InputStream?, folder: String): Boolean {
        try {
            val zin = ZipInputStream(`in`)
            var entry: ZipEntry
            while (zin.nextEntry.also { entry = it } != null) {
                val f = File(folder + entry.name)
                if (!f.canonicalPath.startsWith(folder)) {
                    // ZIP path traversal protection
                    throw SecurityException(
                        "ZIP path traversal attack detected, aborting.")
                }
                if (entry.isDirectory) {
                    f.mkdirs()
                    continue
                } else {
                    // some ZIP files don't include directory items, we
                    // therefore have to make sure parent directories are always
                    // created
                    File(f.parent!!.toString()).mkdirs()
                }
                val out: OutputStream = FileOutputStream(File(folder + entry.name))
                var size = 0
                val buffer = ByteArray(1024)
                while (zin.read(buffer, 0, buffer.size).also { size = it } != -1) {
                    out.write(buffer, 0, size)
                }
                out.close()
            }
            zin.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun folderToZip(folder: String, archivePath: String?): Boolean {
        try {
            val out = FileOutputStream(archivePath)
            val zip = ZipOutputStream(out)
            val success = addFolderToZip(zip, folder, folder)
            zip.flush()
            zip.close()
            return success
        } catch (e: Exception) {
            Log.e("QField",
                "inputStreamToOutputStream exception: " + e.message)
        }
        return false
    }

    private fun addFolderToZip(
        zip: ZipOutputStream, folder: String,
        rootFolder: String,
    ): Boolean {
        val dir = File(folder)
        val files: Array<File> = dir.listFiles()!!
        var pathPrefix = ""
        if (folder.length > rootFolder.length) {
            pathPrefix = folder.substring(rootFolder.length + 1)
            if (pathPrefix.substring(pathPrefix.length - 1) != "/") {
                pathPrefix = "$pathPrefix/"
            }
        }
        for (file in files) {
            val filePath: String = file.path
            val fileName: String = file.name
            if (file.isDirectory) {
                val success = addFolderToZip(zip, file.path, rootFolder)
                if (!success) {
                    return false
                }
            } else {
                try {
                    val zipFile = ZipEntry(pathPrefix + fileName)
                    zip.putNextEntry(zipFile)
                    val input: InputStream = FileInputStream(file)
                    inputStreamToOutputStream(input, zip, file.length())
                    zip.closeEntry()
                } catch (e: Exception) {
                    Log.e("QField", "inputStreamToOutputStream exception: " +
                            e.message)
                    return false
                }
            }
        }
        return true
    }

    private fun inputStreamToOutputStream(
        `in`: InputStream,
        out: OutputStream?,
        totalBytes: Long,
    ): Boolean {
        try {
            var size = 0
            var bufferSize = 1024
            var bufferRead: Long = 0
            val buffer = ByteArray(bufferSize)
            if (totalBytes > 0 && bufferRead + bufferSize > totalBytes) {
                bufferSize = (totalBytes - bufferRead).toInt()
            }
            while (bufferSize > 0 &&
                `in`.read(buffer, 0, bufferSize).also { size = it } != -1
            ) {
                out?.write(buffer, 0, size)
                bufferRead += bufferSize.toLong()
                if (totalBytes > 0 && bufferRead + bufferSize > totalBytes) {
                    bufferSize = (totalBytes - bufferRead).toInt()
                }
            }
        } catch (e: Exception) {
            Log.e("QField",
                "inputStreamToOutputStream exception: " + e.message)
            return false
        }
        return true
    }

    private fun inputStreamToFile(
        `in`: InputStream?, file: String?,
        totalBytes: Long,
    ): Boolean {
        try {
            val out: OutputStream = FileOutputStream(File(file.toString()))
            var size = 0
            var bufferSize = 1024
            var bufferRead: Long = 0
            val buffer = ByteArray(bufferSize)
            if (totalBytes > 0 && bufferRead + bufferSize > totalBytes) {
                bufferSize = (totalBytes - bufferRead).toInt()
            }
            while (bufferSize > 0 &&
                `in`?.read(buffer, 0, bufferSize).also { size = it!! } != -1
            ) {
                out.write(buffer, 0, size)
                bufferRead += bufferSize.toLong()
                if (totalBytes > 0 && bufferRead + bufferSize > totalBytes) {
                    bufferSize = (totalBytes - bufferRead).toInt()
                }
            }
            out.close()
        } catch (e: Exception) {
            Log.e("QField", "inputStreamToFile exception: " + e.message)
            return false
        }
        return true
    }

    private fun deleteDirectory(file: File, recursive: Boolean): Boolean {
        if (!file.isDirectory) {
            return false
        }
        val files: Array<File> = file.listFiles()!!
        for (f in files) {
            var success = true
            if (f.isDirectory) {
                if (recursive) {
                    success = deleteDirectory(f, true)
                }
            } else {
                success = f.delete()
            }
            if (!success) {
                return false
            }
        }
        return file.delete()
    }

    fun getExtensionFromMimeType(type: String?): String {
        when (type) {
            null -> {
                return ""
            }
            "application/pdf" -> {
                return "pdf"
            }
            "application/vnd.sqlite3", "application/x-sqlite3" -> {
                return "Db"
            }
            "application/geopackage+sqlite3" -> {
                return "gpkg"
            }
            "application/vnd.geo+json", "application/geo+json" -> {
                return "geojson"
            }
            "application/gpx+xml" -> {
                return "gpx"
            }
            "application/vnd.google-earth.kml+xml" -> {
                return "kml"
            }
            "application/vnd.google-earth.kmz" -> {
                return "kmz"
            }
            "application/zip" -> {
                return "zip"
            }
            "image/tiff" -> {
                return "tif"
            }
            "image/x-jp2" -> {
                return "jp2"
            }
            else -> return ""
        }
    }

    // original script by SANJAY GUPTA
    // (https://stackoverflow.com/questions/17546101/get-real-path-for-uri-android)
    fun getPathFromUri(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        var path: String? = null
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // DocumentProvider
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" +
                            split[1]
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                path = try {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id))
                    getDataColumn(context, contentUri, null, null)
                } catch (e: NumberFormatException) {
                    // Not numerical IDs, skipping
                    null
                }
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                path = getDataColumn(context, contentUri, selection,
                    selectionArgs)
            }
        }

        // Fallback
        if (path == null && ("content".equals(uri.scheme, ignoreCase = true) ||
                    "file".equals(uri.scheme, ignoreCase = true))
        ) {
            path = uri.path
            if (path != null) {
                path = path.replaceFirst("^/storage_root".toRegex(), "")
            }
        }
        if (path != null) {
            if (File(path).exists() === false) {
                path = ""
            }
        }
        return path
    }

    private fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (e: Exception) {
//            if (cursor != null) cursor.close()
            cursor?.close()
        } finally {
//            if (cursor != null) cursor.close()
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" ==
                uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" ==
                uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" ==
                uri.authority
    }

}