package com.example.spotter

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "KOT_${timeStamp}.jpg").apply {
            parentFile?.mkdirs()
        }
    }

    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun copyUriToFile(context: Context, uri: Uri, file: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                    true
                }
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isImageFileExists(path: String?): Boolean{
        return path?.let { File(it).exists() } ?: false
    }

    fun hasCameraPermission(context: Context): Boolean {
        return(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasPhotoPermission(context: Context): Boolean {
        return(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED)
    }

    fun deleteImageFile(imagePath: String?): Boolean{
        if(imagePath.isNullOrEmpty()) return false
        val file = File(imagePath)
        return if(file.exists()){
            file.delete()
        }else{
            false
        }
    }

}