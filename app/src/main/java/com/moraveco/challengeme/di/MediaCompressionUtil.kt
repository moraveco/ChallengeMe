package com.moraveco.challengeme.di

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object MediaCompressionUtil {

    /**
     * Compress image file
     * @param context Application context
     * @param imageFile Original image file
     * @param maxWidth Maximum width (default 1080)
     * @param maxHeight Maximum height (default 1080)
     * @param quality Compression quality 0-100 (default 80)
     * @param maxSizeKB Maximum file size in KB (default 500KB)
     * @return Compressed file or original if compression fails
     */
    suspend fun compressImage(
        context: Context,
        imageFile: File,
        maxWidth: Int = 1080,
        maxHeight: Int = 1080,
        quality: Int = 80,
        maxSizeKB: Long = 500
    ): File = withContext(Dispatchers.IO) {
        try {
            val compressedFile = Compressor.compress(context, imageFile) {
                resolution(maxWidth, maxHeight)
                quality(quality)
                format(Bitmap.CompressFormat.JPEG)
                size(maxSizeKB * 1024) // Convert KB to bytes
            }
            Log.d(
                "Compression",
                "Image compressed from ${imageFile.length() / 1024}KB to ${compressedFile.length() / 1024}KB"
            )
            compressedFile
        } catch (e: Exception) {
            Log.e("Compression", "Image compression failed: ${e.message}")
            imageFile // Return original file if compression fails
        }
    }

    /**
     * Compress image from URI
     */
    suspend fun compressImageFromUri(
        context: Context,
        imageUri: Uri,
        maxWidth: Int = 1080,
        maxHeight: Int = 1080,
        quality: Int = 80,
        maxSizeKB: Long = 500
    ): File? = withContext(Dispatchers.IO) {
        try {
            val originalFile = getFileFromUri(context, imageUri) ?: return@withContext null
            compressImage(context, originalFile, maxWidth, maxHeight, quality, maxSizeKB)
        } catch (e: Exception) {
            Log.e("Compression", "Image compression from URI failed: ${e.message}")
            null
        }
    }

    /**
     * Compress video from URI with improved error handling
     */
    fun compressVideoFromUri(
        context: Context,
        videoUri: Uri,
        quality: VideoQuality = VideoQuality.MEDIUM,
        onProgress: (Float) -> Unit = {},
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            // First, copy the URI to a local file if needed
            val inputFile = getFileFromUri(context, videoUri)
            if (inputFile == null || !inputFile.exists()) {
                onFailure("Cannot access video file from URI")
                return
            }

            val outputFileName = "compressed_${System.currentTimeMillis()}.mp4"
            val outputDir = File(context.cacheDir, "compressed_videos")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val outputFile = File(outputDir, outputFileName)

            Log.d("Compression", "Input file: ${inputFile.absolutePath}, exists: ${inputFile.exists()}, size: ${inputFile.length()}")
            Log.d("Compression", "Output file: ${outputFile.absolutePath}")

            VideoCompressor.start(
                context = context,
                uris = listOf(videoUri),
                isStreamable = false,
                storageConfiguration = StorageConfigurationImpl(
                    saveAt = outputDir.absolutePath,
                    isExternal = false,
                    fileName = outputFileName
                ),
                configureWith = Configuration(
                    quality = quality,
                    isMinBitrateCheckEnabled = false, // Disable to avoid compression failures
                    videoBitrateInMbps = when (quality) {
                        VideoQuality.LOW -> 1
                        VideoQuality.MEDIUM -> 2
                        VideoQuality.HIGH -> 3
                        else -> 2
                    },
                    disableAudio = false,
                    keepOriginalResolution = false,
                    videoWidth = when (quality) {
                        VideoQuality.LOW -> 480.0
                        VideoQuality.MEDIUM -> 720.0
                        VideoQuality.HIGH -> 1080.0
                        else -> 720.0
                    },
                    videoHeight = when (quality) {
                        VideoQuality.LOW -> 854.0  // Fixed aspect ratio
                        VideoQuality.MEDIUM -> 1280.0
                        VideoQuality.HIGH -> 1920.0
                        else -> 1280.0
                    },
                    videoNames = listOf(outputFileName)
                ),
                listener = object : CompressionListener {
                    override fun onProgress(index: Int, percent: Float) {
                        Log.d("Compression", "Video compression progress: $percent%")
                        onProgress(percent)
                    }

                    override fun onStart(index: Int) {
                        Log.d("Compression", "Video compression started from URI")
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        Log.d("Compression", "Video compression successful. Path: $path, Size: ${size / 1024}KB")
                        path?.let {
                            val file = File(it)
                            if (file.exists()) {
                                onSuccess(it)
                            } else {
                                Log.e("Compression", "Compressed file does not exist at path: $it")
                                onFailure("Compressed file not found")
                            }
                        } ?: run {
                            Log.e("Compression", "Compression path is null")
                            onFailure("Compression path is null")
                        }
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        Log.e("Compression", "Video compression failed: $failureMessage")
                        onFailure(failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        Log.w("Compression", "Video compression cancelled")
                        onFailure("Compression cancelled")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("Compression", "Error setting up video compression", e)
            onFailure("Setup error: ${e.message}")
        }
    }

    /**
     * Compress video file with path
     */
    fun compressVideo(
        context: Context,
        videoPath: String,
        outputPath: String,
        quality: VideoQuality = VideoQuality.MEDIUM,
        onProgress: (Float) -> Unit = {},
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val inputFile = File(videoPath)
            if (!inputFile.exists()) {
                onFailure("Input video file does not exist")
                return
            }

            val outputFile = File(outputPath)
            val outputDir = outputFile.parentFile ?: context.cacheDir
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            VideoCompressor.start(
                context = context,
                uris = listOf(inputFile.toUri()),
                isStreamable = false,
                storageConfiguration = StorageConfigurationImpl(
                    saveAt = outputDir.absolutePath,
                    isExternal = false,
                    fileName = outputFile.name
                ),
                configureWith = Configuration(
                    quality = quality,
                    isMinBitrateCheckEnabled = false,
                    videoBitrateInMbps = when (quality) {
                        VideoQuality.LOW -> 1
                        VideoQuality.MEDIUM -> 2
                        VideoQuality.HIGH -> 3
                        else -> 2
                    },
                    disableAudio = false,
                    keepOriginalResolution = false,
                    videoWidth = when (quality) {
                        VideoQuality.LOW -> 480.0
                        VideoQuality.MEDIUM -> 720.0
                        VideoQuality.HIGH -> 1080.0
                        else -> 720.0
                    },
                    videoHeight = when (quality) {
                        VideoQuality.LOW -> 854.0
                        VideoQuality.MEDIUM -> 1280.0
                        VideoQuality.HIGH -> 1920.0
                        else -> 1280.0
                    },
                    videoNames = listOf(outputFile.name)
                ),
                listener = object : CompressionListener {
                    override fun onProgress(index: Int, percent: Float) {
                        onProgress(percent)
                    }

                    override fun onStart(index: Int) {
                        Log.d("Compression", "Video compression started")
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        path?.let {
                            Log.d("Compression", "Video compressed successfully: $it (Size: ${size / 1024}KB)")
                            onSuccess(it)
                        } ?: onFailure("Compression path is null")
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        Log.e("Compression", "Video compression failed: $failureMessage")
                        onFailure(failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        Log.w("Compression", "Video compression cancelled")
                        onFailure("Compression cancelled")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("Compression", "Error setting up video compression", e)
            onFailure("Setup error: ${e.message}")
        }
    }

    /**
     * Get file from URI with better error handling
     */
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            // First try to get the direct file path
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Video.Media.DATA),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndex(MediaStore.Video.Media.DATA)
                    if (columnIndex != -1) {
                        val filePath = it.getString(columnIndex)
                        if (!filePath.isNullOrEmpty()) {
                            val file = File(filePath)
                            if (file.exists()) {
                                return file
                            }
                        }
                    }
                }
            }

            // If direct path doesn't work, copy to cache
            Log.d("Compression", "Direct file path not available, copying to cache")
            copyUriToFile(context, uri)
        } catch (e: Exception) {
            Log.e("Compression", "Error getting file from URI: ${e.message}")
            copyUriToFile(context, uri)
        }
    }

    /**
     * Copy URI content to a file in app's cache directory
     */
    private fun copyUriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            // Determine file extension based on MIME type
            val mimeType = context.contentResolver.getType(uri)
            val extension = when {
                mimeType?.contains("video") == true -> {
                    when {
                        mimeType.contains("mp4") -> ".mp4"
                        mimeType.contains("avi") -> ".avi"
                        mimeType.contains("mov") -> ".mov"
                        else -> ".mp4" // Default to mp4
                    }
                }
                mimeType?.contains("image") == true -> ".jpg"
                else -> ".mp4"
            }

            val fileName = "temp_media_${System.currentTimeMillis()}$extension"
            val cacheDir = File(context.cacheDir, "temp_media")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val outputFile = File(cacheDir, fileName)

            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("Compression", "Copied URI to file: ${outputFile.absolutePath}, size: ${outputFile.length()}")
            outputFile
        } catch (e: IOException) {
            Log.e("Compression", "Error copying URI to file: ${e.message}")
            null
        }
    }

    /**
     * Get file size in MB
     */
    fun getFileSizeInMB(file: File): Double {
        return file.length() / (1024.0 * 1024.0)
    }

    /**
     * Generate output path for compressed video
     */
    fun generateCompressedVideoPath(context: Context, originalPath: String): String {
        val fileName = "compressed_video_${System.currentTimeMillis()}.mp4"
        val outputDir = File(context.cacheDir, "compressed_videos")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        return File(outputDir, fileName).absolutePath
    }

    /**
     * Check if file is video
     */
    fun isVideoFile(path: String): Boolean {
        val videoExtensions = listOf("mp4", "avi", "mov", "mkv", "webm", "flv", "wmv", "3gp")
        return videoExtensions.any { path.lowercase().endsWith(it) }
    }

    /**
     * Check if file is image
     */
    fun isImageFile(path: String): Boolean {
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
        return imageExtensions.any { path.lowercase().endsWith(it) }
    }
}

data class StorageConfigurationImpl(
    val saveAt: String,
    val isExternal: Boolean,
    val fileName: String
) : StorageConfiguration {
    override fun createFileToSave(
        context: Context,
        videoFile: File,
        fileName: String,
        shouldSave: Boolean
    ): File {
        val outputDir = File(saveAt)

        // Ensure the output directory exists
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // Use the provided fileName or fall back to the class property
        val finalFileName = fileName.ifBlank { this.fileName }

        return File(outputDir, finalFileName)
    }
}