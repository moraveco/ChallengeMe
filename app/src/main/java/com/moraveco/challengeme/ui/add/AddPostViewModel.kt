
package com.moraveco.challengeme.ui.add

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.UploadResponse
import com.moraveco.challengeme.di.MediaCompressionUtil
import com.moraveco.challengeme.notifications.FcmMessage
import com.moraveco.challengeme.notifications.Message
import com.moraveco.challengeme.repo_impl.DailyChallengeRepositoryImpl
import com.moraveco.challengeme.repo_impl.NotificationRepositoryImpl
import com.moraveco.challengeme.ui.profile.edit.getFileFromUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val repository: DailyChallengeRepositoryImpl,
    private val notificationRepository: NotificationRepositoryImpl
) : ViewModel() {

    companion object {
        private const val TAG = "AddPostViewModel"
        private const val IMAGE_MAX_WIDTH = 1080
        private const val IMAGE_MAX_HEIGHT = 1080
        private const val IMAGE_QUALITY = 85
        private const val IMAGE_MAX_SIZE_KB = 800L
        private val VIDEO_QUALITY = VideoQuality.MEDIUM
        private const val NOTIFICATION_BATCH_SIZE = 10
        private const val NOTIFICATION_DELAY_MS = 100L
        private const val MAX_VIDEO_SIZE_MB = 50L // Maximum video size before compression
    }

    private val _dailyChallenge = MutableStateFlow(
        DailyChallenge(
            "1",
            "No challenge available for this day",
            "Žádný úkol na dnešek",
            "Žiadna úloha na dnešok",
            LocalDate.now().toString()
        )
    )
    val dailyChallenge: StateFlow<DailyChallenge> = _dailyChallenge.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _compressionProgress = MutableStateFlow(0f)
    val compressionProgress: StateFlow<Float> = _compressionProgress.asStateFlow()

    private val _compressionStatus = MutableStateFlow("")
    val compressionStatus: StateFlow<String> = _compressionStatus.asStateFlow()

    private val _uploadResponse = MutableLiveData<UploadResponse?>()
    val uploadResponse: LiveData<UploadResponse?> = _uploadResponse

    private val _notificationProgress = MutableStateFlow<NotificationProgress?>(null)
    val notificationProgress: StateFlow<NotificationProgress?> = _notificationProgress.asStateFlow()

    data class NotificationProgress(
        val total: Int,
        val sent: Int,
        val failed: Int
    )

    init {
        getDailyChallenge()
    }

    private fun getDailyChallenge() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val challenge = repository.getDailyChallenge()
                _dailyChallenge.value = challenge
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get daily challenge", e)
            }
        }
    }

    /**
     * Upload compressed photo with better error handling
     */
    fun uploadCompressedPhoto(context: Context, imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateLoadingState(true, "Komprimování obrázku...", 0f)

                // Compress image
                val compressedFile = MediaCompressionUtil.compressImageFromUri(
                    context = context,
                    imageUri = imageUri,
                    maxWidth = IMAGE_MAX_WIDTH,
                    maxHeight = IMAGE_MAX_HEIGHT,
                    quality = IMAGE_QUALITY,
                    maxSizeKB = IMAGE_MAX_SIZE_KB
                )

                if (compressedFile == null) {
                    handleUploadError("Komprese obrázku selhala")
                    return@launch
                }

                updateLoadingState(true, "Nahrávání obrázku...", 50f)

                // Upload compressed file
                val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    compressedFile.name,
                    requestBody
                )

                val response = repository.uploadPhoto(multipartBody)

                if (response.isSuccessful) {
                    val originalSize = getFileSizeFromUri(context, imageUri)
                    val compressedSize = MediaCompressionUtil.getFileSizeInMB(compressedFile)

                    Log.d(TAG, "Image upload successful - Original: ${originalSize}MB, Compressed: ${compressedSize}MB")

                    _uploadResponse.postValue(
                        UploadResponse(
                            true,
                            "Obrázek úspěšně nahrán",
                            response.body() ?: ""
                        )
                    )
                    updateLoadingState(false, "Nahrávání dokončeno", 100f)
                } else {
                    handleUploadError("Nahrávání selhalo: ${response.code()}")
                }

                // Clean up temporary file
                compressedFile.deleteOnExit()

            } catch (e: Exception) {
                Log.e(TAG, "Error uploading compressed photo", e)
                handleUploadError("Chyba při nahrávání: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Upload compressed video with improved error handling and fallback
     */
    fun uploadCompressedVideo(context: Context, videoUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateLoadingState(true, "Připravuji video...", 0f)

                // Check video size first
                val originalSize = getFileSizeFromUri(context, videoUri)
                Log.d(TAG, "Original video size: ${originalSize}MB")

                // If video is already small enough, try uploading without compression first
                if (originalSize < 10.0) { // Less than 10MB
                    Log.d(TAG, "Video is small enough, attempting direct upload first")
                    val originalFile = getFileFromUri(context, videoUri)
                    if (originalFile != null && attemptDirectVideoUpload(originalFile)) {
                        return@launch
                    }
                    Log.d(TAG, "Direct upload failed or file not accessible, proceeding with compression")
                }

                // Proceed with compression
                MediaCompressionUtil.compressVideoFromUri(
                    context = context,
                    videoUri = videoUri,
                    quality = determineVideoQuality(originalSize),
                    onProgress = { progress ->
                        viewModelScope.launch {
                            _compressionProgress.value = progress * 0.7f // 70% for compression
                            _compressionStatus.value = "Komprimování videa: ${progress.toInt()}%"
                        }
                    },
                    onSuccess = { compressedPath ->
                        uploadCompressedVideoFile(compressedPath)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Compression failed: $error")
                        handleCompressionFailure(context, videoUri, error)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error in video compression setup", e)
                handleUploadError("Chyba při zpracování videa: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Determine video quality based on file size
     */
    private fun determineVideoQuality(fileSizeMB: Double): VideoQuality {
        return when {
            fileSizeMB > 100 -> VideoQuality.LOW
            fileSizeMB > 50 -> VideoQuality.MEDIUM
            else -> VideoQuality.HIGH
        }
    }

    /**
     * Handle compression failure with fallback strategies
     */
    private fun handleCompressionFailure(context: Context, videoUri: Uri, error: String) {
        Log.w(TAG, "Trying fallback strategies after compression failure: $error")

        try {
            // Fallback 1: Try with lower quality
            updateLoadingState(true, "Zkouším nižší kvalitu...", 0f)

            MediaCompressionUtil.compressVideoFromUri(
                context = context,
                videoUri = videoUri,
                quality = VideoQuality.LOW,
                onProgress = { progress ->
                    viewModelScope.launch {
                        _compressionProgress.value = progress * 0.7f
                        _compressionStatus.value = "Komprimování (nízká kvalita): ${progress.toInt()}%"
                    }
                },
                onSuccess = { compressedPath ->
                    uploadCompressedVideoFile(compressedPath)
                },
                onFailure = { lowQualityError ->
                    Log.e(TAG, "Low quality compression also failed: $lowQualityError")
                    // Fallback 2: Try direct upload of original
                    handleFinalFallback(context, videoUri, "Komprese selhala, zkouším původní soubor")
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Fallback compression failed", e)
            handleFinalFallback(context, videoUri, "Všechny pokusy o kompresi selhaly")
        }
    }

    /**
     * Final fallback: attempt direct upload
     */
    private fun handleFinalFallback(context: Context, videoUri: Uri, message: String) {
        try {
            updateLoadingState(true, message, 0f)
            val originalFile = getFileFromUri(context, videoUri)

            viewModelScope.launch {
                if (originalFile != null) {
                    val fileSize = MediaCompressionUtil.getFileSizeInMB(originalFile)
                    if (fileSize < MAX_VIDEO_SIZE_MB) {
                        Log.d(TAG, "Attempting direct upload as final fallback")
                        if (attemptDirectVideoUpload(originalFile)) {
                            return@launch
                        }
                    }
                }

                handleUploadError("Video je příliš velké nebo se nepodařilo zpracovat")
            }
        }catch (e: Exception) {

            Log.e(TAG, "Final fallback failed", e)
            handleUploadError("Nepodařilo se nahrát video: ${e.localizedMessage}")
        }
    }

    /**
     * Attempt direct video upload without compression
     */
    private suspend fun attemptDirectVideoUpload(videoFile: File): Boolean {
        return try {
            updateLoadingState(true, "Nahrávání videa...", 70f)

            val requestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                videoFile.name,
                requestBody
            )

            val response = repository.uploadVideo(multipartBody)

            if (response.isSuccessful) {
                val fileSize = MediaCompressionUtil.getFileSizeInMB(videoFile)
                Log.d(TAG, "Direct video upload successful - Size: ${fileSize}MB")

                _uploadResponse.postValue(
                    UploadResponse(
                        true,
                        "Video úspěšně nahráno",
                        response.body() ?: ""
                    )
                )
                updateLoadingState(false, "Nahrávání dokončeno", 100f)
                true
            } else {
                Log.e(TAG, "Direct upload failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Direct upload exception", e)
            false
        }
    }

    private fun uploadCompressedVideoFile(compressedPath: String) {
        try {
            updateLoadingState(true, "Nahrávání videa...", 70f)

            val compressedFile = File(compressedPath)
            if (!compressedFile.exists()) {
                handleUploadError("Komprimovaný soubor nebyl nalezen")
                return
            }

            val requestBody = compressedFile.asRequestBody("video/mp4".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                compressedFile.name,
                requestBody
            )

            viewModelScope.launch {
                val response = repository.uploadVideo(multipartBody)

                if (response.isSuccessful) {
                    val fileSize = MediaCompressionUtil.getFileSizeInMB(compressedFile)
                    Log.d(TAG, "Video uploaded successfully - Size: ${fileSize}MB")

                    _uploadResponse.postValue(
                        UploadResponse(
                            true,
                            "Video úspěšně nahráno",
                            response.body() ?: ""
                        )
                    )
                    updateLoadingState(false, "Nahrávání dokončeno", 100f)
                } else {
                    handleUploadError("Nahrávání videa selhalo: ${response.code()}")
                }

                // Clean up compressed file
                compressedFile.deleteOnExit()
            }



        } catch (e: Exception) {
            Log.e(TAG, "Error uploading compressed video", e)
            handleUploadError("Chyba při nahrávání videa: ${e.localizedMessage}")
        }
    }

    /**
     * Add post and send notifications to friends with better error handling and batching
     */
    fun addPost(post: Post, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uploadResult = _uploadResponse.value
                if (uploadResult == null || !uploadResult.success) {
                    Log.e(TAG, "Cannot add post without successful upload")
                    return@launch
                }

                updateLoadingState(true, "Ukládání příspěvku...", 0f)

                // Create the post
                repository.createPost(post)

                // Send notifications to friends


                withContext(Dispatchers.Main) {
                    onSuccess()
                }

                updateLoadingState(false, "Příspěvek přidán", 100f)

            } catch (e: Exception) {
                Log.e(TAG, "Error adding post", e)
                updateLoadingState(false, "Chyba při přidávání příspěvku", 0f)
            }
        }
    }

    /**
     * Send notifications to friends with batching and error handling
     */


    /**
     * Helper function to update loading states
     */
    private fun updateLoadingState(loading: Boolean, status: String, progress: Float) {
        _isLoading.value = loading
        _compressionStatus.value = status
        _compressionProgress.value = progress
    }

    /**
     * Helper function to handle upload errors
     */
    private fun handleUploadError(errorMessage: String) {
        Log.e(TAG, errorMessage)
        _uploadResponse.postValue(UploadResponse(false, errorMessage, ""))
        updateLoadingState(false, errorMessage, 0f)
    }

    /**
     * Get file size from URI
     */
    private fun getFileSizeFromUri(context: Context, uri: Uri): Double {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.available() / (1024.0 * 1024.0)
            } ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    /**
     * Clear upload result and reset states
     */
    fun clearUploadResult() {
        _uploadResponse.value = null
        _compressionProgress.value = 0f
        _compressionStatus.value = ""
        _notificationProgress.value = null
    }

    /**
     * Legacy upload methods - redirect to new implementations
     */
    @Deprecated("Use uploadCompressedPhoto instead", ReplaceWith("uploadCompressedPhoto(context, Uri.fromFile(photoFile))"))
    fun uploadPhoto(photoFile: File) {
        throw IllegalStateException("This method is deprecated. Use uploadCompressedPhoto with context instead.")
    }

    @Deprecated("Use uploadCompressedVideo instead", ReplaceWith("uploadCompressedVideo(context, Uri.fromFile(videoFile))"))
    fun uploadVideo(videoFile: File, context: Context) {
        uploadCompressedVideo(context, Uri.fromFile(videoFile))
    }

    override fun onCleared() {
        super.onCleared()
        clearUploadResult()
    }
}