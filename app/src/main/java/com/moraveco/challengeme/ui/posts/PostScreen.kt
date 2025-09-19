package com.moraveco.challengeme.ui.posts

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.moraveco.challengeme.R
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.data.Comment
import com.moraveco.challengeme.data.CommentData
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.util.UUID
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.containsPostId
import com.moraveco.challengeme.data.likedPost
import com.moraveco.challengeme.ui.home.LikeButton
import java.time.LocalDate
import java.time.LocalDateTime


@Composable
fun PostScreen(
    email: String,
    id: String,
    myUid: String,
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel()
) {
    LaunchedEffect(id) {
        if (!id.isDigitsOnly()) {
            postViewModel.loadPostDetail(id)

        } else {
            postViewModel.loadAdDetail(id)
        }
    }

    val uiState by postViewModel.postDetailState.collectAsState()
    // Also get home UI state to access user likes and today's like status

    val post = uiState.post
    val comments = uiState.comments
    val context = LocalContext.current


    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(TextFieldValue("")) }

    // Check if user has liked this specific post

    postViewModel.likeManager.getLikeState(post?.id ?: "")
    val likeState by postViewModel.likeManager.likesState.collectAsState()
    // Check like/unlike permissions
// Determine if user can interact with likes
    val isHistoryPost =
        if (post != null) !postViewModel.likeManager.isPostFromToday(post.time) else true
    val canLike = if (isHistoryPost || post == null) {
        false
    } else {
        postViewModel.likeManager.canLikePost(post, myUid)
    }

    val canUnlike = if (isHistoryPost || post == null) {
        false
    } else {
        postViewModel.likeManager.canUnlikePost(post, myUid)
    }




    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (post == null) {
        Text("Post not found", color = Color.White)
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(horizontal = 16.dp)
            ) {
                // ====== Header bar ======
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF53A1FD)
                            )
                        }
                        Text(
                            text = stringResource(R.string.back),
                            fontSize = 18.sp,
                            color = Color(0xFF53A1FD)
                        )
                    }

                    if (post.uid == myUid) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFF53A1FD))
                        }
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text(stringResource(R.string.delete_post)) },
                                text = { Text(stringResource(R.string.really_delete_post)) },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            postViewModel.deletePost(post.id) {
                                                navController.navigate(Screens.Home)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text(stringResource(R.string.confirm), color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showDeleteDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                    ) {
                                        Text(stringResource(R.string.cancel), color = Color.White)
                                    }
                                }
                            )
                        }
                    } else {
                        if (post.likes_count?.isDigitsOnly() == true){
                            IconButton(onClick = { showReportDialog = true }) {
                                Icon(Icons.Default.Report, null, tint = Color(0xFF53A1FD))
                            }
                        }

                        if (showReportDialog) {
                            AlertDialog(
                                onDismissRequest = { showReportDialog = false },
                                title = { Text(stringResource(R.string.report_post)) },
                                text = { Text(stringResource(R.string.really_report)) },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            postViewModel.sendReport(
                                                email = email,
                                                myUid = myUid,
                                                postId = post.id,
                                                image = post.image
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.report_sent_successfully),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate(Screens.Home)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) {
                                        Text(stringResource(R.string.confirm), color = Color.White)
                                    }
                                },
                                dismissButton = {
                                    Button(
                                        onClick = { showReportDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                    ) {
                                        Text(stringResource(R.string.cancel), color = Color.White)
                                    }
                                }
                            )
                        }
                    }
                }

                // ====== Post obsah ======
                Column(modifier = Modifier.background(Bars, RoundedCornerShape(20.dp))) {

                    if (post.isVideo == "true") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .aspectRatio(3f / 4f)
                                .background(Color.Black)
                        ) {
                            var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

                            AndroidView(
                                factory = { context ->
                                    TextureView(context).apply {
                                        surfaceTextureListener =
                                            object : TextureView.SurfaceTextureListener {
                                                override fun onSurfaceTextureAvailable(
                                                    surface: SurfaceTexture,
                                                    width: Int,
                                                    height: Int
                                                ) {
                                                    mediaPlayer = MediaPlayer().apply {
                                                        setSurface(Surface(surface))
                                                        setDataSource(context, post.image.toUri())
                                                        prepareAsync()
                                                        setOnPreparedListener { mp ->
                                                            mp.isLooping = true
                                                            mp.start()
                                                        }
                                                    }
                                                }

                                                override fun onSurfaceTextureSizeChanged(
                                                    surface: SurfaceTexture,
                                                    width: Int,
                                                    height: Int
                                                ) {
                                                }

                                                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                                    mediaPlayer?.release()
                                                    mediaPlayer = null
                                                    return true
                                                }

                                                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                                            }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            DisposableEffect(post.image) {
                                onDispose {
                                    mediaPlayer?.release()
                                }
                            }
                        }
                    } else {
                        AsyncImage(
                            model = post.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .aspectRatio(3f / 4f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = post.description,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = post.profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${post.name} ${post.lastName}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(text = post.time, color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        if (post.likes_count?.isDigitsOnly() == true) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LikeButton(
                                    count = likeState[post.id]?.likeCount ?: 0,
                                    isLiked = likeState[post.id]?.isLiked ?: false,
                                    enabled = canLike || canUnlike,
                                    onClick = {
                                        postViewModel.toggleLike(post, myUid)
                                    }
                                )
                                Spacer(Modifier.width(16.dp))

                                Text(
                                    text = post.comments_count ?: "0",
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.comment_regular),
                                    contentDescription = "Comment",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                    }
                }

                Spacer(Modifier.height(10.dp))
                CommentRecycler1(comments, navController)
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // ====== Vstup pro komentáře ======
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Bars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(stringResource(R.string.your_comment), color = Color.Gray)
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                IconButton(
                    onClick = {
                        if (text.text.isNotBlank()) {
                            postViewModel.sendComment(
                                CommentData(
                                    UUID.randomUUID().toString(),
                                    myUid,
                                    post.id,
                                    text.text
                                )
                            )
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF53A1FD))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CommentItem1(
    comment: Comment,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = comment.profileImageUrl, contentDescription = null, modifier = Modifier
                .size(35.dp)
                .clip(
                    CircleShape
                )
                .clickable {
                    navController.navigate(Screens.UserProfile(comment.posterUid))
                }, contentScale = ContentScale.Crop
        )
        Card(
            shape = RoundedCornerShape(17.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
            colors = CardDefaults.cardColors(containerColor = Bars)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(text = comment.name, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = comment.comment, color = Color.White)
            }
        }
    }
}

@Composable
fun CommentRecycler1(
    comments: List<Comment>,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.height(500.dp)) {
        items(comments.size) { position ->
            CommentItem1(comment = comments[position], navController = navController)

        }
    }
}