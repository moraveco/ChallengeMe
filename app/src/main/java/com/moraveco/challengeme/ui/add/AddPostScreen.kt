package com.moraveco.challengeme.ui.add

import android.R.style
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.hardware.Camera.CameraInfo
import android.net.Uri
import android.net.Uri.fromFile
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
// Compose
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

// AndroidX CameraX
import androidx.camera.core.CameraSelector
import androidx.camera.video.VideoCapture

// Lifecycle and context
import androidx.lifecycle.LifecycleOwner

// Navigation (if using navigation Compose)

// Android
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.constants.Constants.Companion.BASE_URL
import com.moraveco.challengeme.data.DailyChallenge
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.containsPostId
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.home.PostCard
import com.moraveco.challengeme.ui.profile.edit.getFileFromUri
import com.moraveco.challengeme.ui.theme.Bars
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID


@SuppressLint("DefaultLocale")
@Composable
fun AddPostScreen(navController: NavController, myUid: String, myPost: Post?, viewModel: AddPostViewModel = hiltViewModel()) {

    val dailyChallenge by viewModel.dailyChallenge.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraReady by remember { mutableStateOf(false) }
    val recording = remember { mutableStateOf<Recording?>(null) }
    val isRecording = remember { mutableStateOf(false) }
    var capturedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var public by remember { mutableStateOf(true) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var scale by remember { mutableFloatStateOf(1f) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    val currentLanguage = Locale.getDefault().language // e.g., "cs", "en", "de", etc.

    val challengeText = if (currentLanguage == "cs") {
        dailyChallenge.cs
    } else {
        dailyChallenge.en
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val videoCapture = remember {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        VideoCapture.withOutput(recorder)
    }
    val uploadResponse by viewModel.uploadResponse.observeAsState()


    val imagePath = uploadResponse?.file_path?.let { path ->
        if (path.length > 2) {
            path.substring(2)
        } else {
            // Handle cases where path is too short or empty.
            // Option 1: Use the path as is (if it's valid without substring)
            // path
            // Option 2: Return an empty string or null to indicate an issue
            "" // Or null, depending on how you want to handle this
        }
    } ?: "" // Provide a default if uploadResponse or file_path is null, or if the let block returns null

    val post = Post(
        id = UUID.randomUUID().toString(),
        uid = myUid,
        image = if (imagePath.isNotEmpty()) BASE_URL + imagePath else "", // Or handle empty/default image URL differently
        description = challengeText,
        time = LocalDateTime.now().toString(),
        isPublic = public.toString(),
        isVideo = (capturedVideoUri != null).toString()
    )

    // Handle upload response
    LaunchedEffect(uploadResponse) {
        uploadResponse?.let { result ->
            if (result.success) {
                Toast.makeText(context, "Soubor byl úspěšně nahrán a komprimován", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Chyba: ${result.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Update camera setup to include scale in dependencies and store camera reference
    LaunchedEffect(previewView, cameraSelector, scale) {
        previewView?.let { pv ->
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.surfaceProvider = pv.surfaceProvider
                }

                cameraProvider.unbindAll()
                val cameraInstance = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture
                )

                // Store camera reference and apply zoom
                camera = cameraInstance
                cameraInstance.cameraControl.setZoomRatio(scale.coerceIn(1f, 5f))

                cameraReady = true
            } catch (exc: Exception) {
                Log.e("Camera", "Camera setup failed", exc)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010038))
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = challengeText,
            fontSize = 25.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .background(Color.Black, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {

            if (myPost != null){

            }else{
                if (capturedImageUri != null || capturedVideoUri != null) {
                    if (capturedImageUri != null) {
                        AsyncImage(
                            model = capturedImageUri,
                            contentDescription = "Captured photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                    } else if (capturedVideoUri != null) {
                        AndroidView(
                            factory = { context ->
                                VideoView(context).apply {
                                    setVideoURI(capturedVideoUri)
                                    setOnPreparedListener {
                                        it.isLooping = true
                                        start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )


                    }

                    LoadingBox(isLoading)

                    // Cancel button
                    IconButton(
                        onClick = {
                            capturedImageUri = null
                            capturedVideoUri = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                    }

                    // Privacy toggle
                    Button(
                        onClick = { public = !public },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = if (public) Bars else Color(100, 217, 46)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(if (public) Icons.Default.Public else Icons.Default.People, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (public) "PUBLIC" else "FRIENDS", color = Color.White)
                    }

                    // Save
                    IconButton(
                        onClick = {  },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Save", tint = Color.White)
                    }

                    // Send
                    Button(
                        onClick = {
                            if (capturedVideoUri != null){
                                uploadVideo(context, capturedVideoUri!!, viewModel)
                            }else{
                                uploadImage(context, capturedImageUri!!, viewModel)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.send), color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                } else {
                    // Show camera preview with gesture detection
                    AndroidView(
                        factory = {
                            PreviewView(it).also { view ->
                                previewView = view
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            // Add zoom gesture detection to live camera preview
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoomChange, _ ->
                                    val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                                    scale = newScale
                                    // Apply zoom immediately
                                    camera?.cameraControl?.setZoomRatio(newScale)
                                }
                            }
                            // Add double-tap gesture for camera flip to live camera preview
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                            CameraSelector.DEFAULT_FRONT_CAMERA
                                        } else {
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                        }
                                        // Reset zoom when switching cameras
                                        scale = 1f
                                    }
                                )
                            }
                    )

                    if (!cameraReady) {
                        CircularProgressIndicator(color = Color.White)
                    }

                    // Camera flip button (alternative to double-tap)
                    IconButton(
                        onClick = {
                            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                            scale = 1f // Reset zoom when switching cameras
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.FlipCameraAndroid,
                            contentDescription = "Flip Camera",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Zoom indicator
                    if (scale > 1f) {
                        Text(
                            text = "${String.format("%.1f", scale)}x",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Ring-style capture button
                    GestureCaptureButton(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        onPhotoClick = {
                            takePhoto(context, imageCapture) { uri ->
                                capturedImageUri = uri
                            }
                        },
                        onVideoStart = {
                            if (!isRecording.value) {
                                startVideoRecording(
                                    context,
                                    videoCapture,
                                    onRecordingStarted = {
                                        recording.value = it
                                        isRecording.value = true
                                    },
                                    onVideoFinalized = { uri ->
                                        capturedVideoUri = uri
                                        isRecording.value = false
                                    }
                                )
                            }
                        },
                        onVideoStop = {
                            if (isRecording.value) {
                                stopVideoRecording(recording.value)
                            }
                        }
                    )
                }
            }
        }

        uploadResponse?.let { result ->
            if (result.success) {
                viewModel.addPost(post) {
                    navController.navigate(Screens.Home)
                }
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                // Reset the upload result to avoid multiple triggers
                viewModel.clearUploadResult()

            }
        }
    }
}

fun uploadImage(
    context: Context,
    imageUri: Uri,
    viewModel: AddPostViewModel
) {
    getFileFromUri(context, imageUri)?.let { file ->
        viewModel.uploadCompressedPhoto(context, fromFile(file))
    }
}

fun uploadVideo(
    context: Context,
    imageUri: Uri,
    viewModel: AddPostViewModel
) {
    getFileFromUri(context, imageUri)?.let { file ->
        viewModel.uploadVideo(file, context)
    }
}

fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (Uri) -> Unit
) {
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Compose")
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues)
        .build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { onImageSaved(it) }
            }

            override fun onError(exc: ImageCaptureException) {
                Log.e("Camera", "Photo capture failed: ${exc.message}", exc)
            }
        }
    )
}
fun startVideoRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    onRecordingStarted: (Recording) -> Unit,
    onVideoFinalized: (Uri?) -> Unit
) {
    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraXCompose")
    }

    val outputOptions = MediaStoreOutputOptions.Builder(
        context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )
        .setContentValues(contentValues)
        .build()

    val recording = videoCapture.output
        .prepareRecording(context, outputOptions)
        .apply { withAudioEnabled() }
        .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Finalize -> {
                    onVideoFinalized(recordEvent.outputResults.outputUri)
                }
                // You could also handle Start, Status, Pause, Resume, etc.
            }
        }

    onRecordingStarted(recording)
}


fun stopVideoRecording(recording: Recording?) {
    recording?.stop()
    // onStopped() will be triggered in listener if needed, depending on Recording setup
}

@Composable
fun GestureCaptureButton(
    modifier: Modifier = Modifier,
    onPhotoClick: () -> Unit,
    onVideoStart: () -> Unit,
    onVideoStop: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(72.dp)
            .background(Color.Transparent, shape = CircleShape)
            .border(
                width = 6.dp,
                color = if (isPressed) Color.Red else Color.White,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isPressed = true
                        val startTime = System.currentTimeMillis()
                        val pressSucceeded = tryAwaitRelease()
                        val duration = System.currentTimeMillis() - startTime
                        isPressed = false

                        if (pressSucceeded && duration >= 500) {
                            // Long press - stop video
                            onVideoStop()
                        } else if (duration >= 500) {
                            // Long press - canceled
                            onVideoStop()
                        } else {
                            // Tap
                            onPhotoClick()
                        }
                    },
                    onLongPress = {
                        onVideoStart()
                    }
                )
            }
    )
}
