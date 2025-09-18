package com.moraveco.challengeme.ui.home

import PermissionHandler
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moraveco.challengeme.R
import com.moraveco.challengeme.TopBar
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.likedPost
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.posts.LikeManager
import com.moraveco.challengeme.ui.posts.PostViewModel
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.time.LocalDate
import java.util.*

@Composable
fun HomeScreen(
    name: String,
    navController: NavController,
    myUid: String,
    postViewModel: PostViewModel = hiltViewModel()
) {
// Load posts when screen opens
    LaunchedEffect(myUid) {
        postViewModel.loadHomePosts(myUid)
    }// Collect combined state for real-time updates
    val combinedState by postViewModel.combinedHomeState.collectAsStateWithLifecycle()
    val context = LocalContext.current// Permission handler
    PermissionHandler(
        context = context,
        permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    ) {}


    Scaffold (
            topBar = { TopBar(navController) },
    containerColor = Background
    ) { paddingValues ->
        when {
            combinedState.posts.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            combinedState.posts.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${combinedState.posts.error}",
                            color = Color.Red
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { postViewModel.loadHomePosts(myUid) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Friends posts section
                    postsSection(
                        title = "Friends",
                        posts = combinedState.posts.friendsPosts,
                        myUid = myUid,
                        navController = navController,
                        likesState = combinedState.likesState,
                        postViewModel = postViewModel,
                        isHistory = false
                    )                // Public posts section
                    postsSection(
                        title = "Public",
                        posts = combinedState.posts.publicPosts,
                        myUid = myUid,
                        navController = navController,
                        likesState = combinedState.likesState,
                        postViewModel = postViewModel,
                        isHistory = false
                    )                // History posts section
                    postsSection(
                        title = "History",
                        posts = combinedState.posts.historyPosts,
                        myUid = myUid,
                        navController = navController,
                        likesState = combinedState.likesState,
                        postViewModel = postViewModel,
                        isHistory = true
                    )
                }
            }
        }
    }
}

private fun LazyListScope.postsSection(
    title: String,
    posts: List<Post>,
    myUid: String,
    navController: NavController,
    likesState: Map<String, LikeManager.LikeState>,
    postViewModel: PostViewModel,
    isHistory: Boolean
) {
    if (posts.isNotEmpty()) {
        item {
            SectionHeader(title = title)
        }
        itemsIndexed(
            items = posts,
            key = { _, post -> post.id }
        ) { _, post ->
            val likeManager = remember { postViewModel.likeManager }

            PostCard(
                post = post,
                currentUserId = myUid,
                likeState = likesState[post.id],
                likeManager = likeManager,
                isHistoryPost = isHistory,
                onOpenPost = {
                    navController.navigate(Screens.Post(post.id))
                },
                onLike = {
                    postViewModel.toggleLike(post, myUid)
                }
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.Gray.copy(alpha = 0.5f)
            )
            Text(
                text = title,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}


@Composable
private fun SectionDivider(title: String) {
    Column {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.Gray.copy(alpha = 0.5f)
            )
            Text(
                text = title,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.Gray.copy(alpha = 0.5f)
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun LoadingBox(isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUserId: String,
    likeState: LikeManager.LikeState?,
    likeManager: LikeManager,
    isHistoryPost: Boolean,
    onOpenPost: () -> Unit,
    onLike: () -> Unit
) {
// Get real-time like state
    val currentLikeState = likeState ?: LikeManager.LikeState(
        postId = post.id,
        isLiked = false,
        likeCount = post.likes_count?.toIntOrNull() ?: 0
    )
// Determine if user can interact with likes
    val canLike = if (isHistoryPost) {
        false
    } else {
        likeManager.canLikePost(post, currentUserId)
    }

    val canUnlike = if (isHistoryPost) {
        false
    } else {
        likeManager.canUnlikePost(post, currentUserId)
    }

    val isLikeEnabled = canLike || canUnlike

// Animate like button
    val likeScale by animateFloatAsState(
        targetValue = if (currentLikeState.isProcessing) 1.2f else 1f,
        label = "like_scale"
    )

    val likeColor by animateColorAsState(
        targetValue = when {
            currentLikeState.isLiked -> Color.Red
            isLikeEnabled -> Color.White
            else -> Color.Gray
        },
        label = "like_color"
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Bars, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .clickable { onOpenPost() }
    ) {
        PostImage(post.image, post.isVideo.toBoolean(), context)
        PostDescription(post.description)
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(Modifier.padding(horizontal = 10.dp))
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UserInfo(post)
            Row {
                LikeButton(
                    count = currentLikeState.likeCount,
                    isLiked = currentLikeState.isLiked,
                    enabled = canLike || canUnlike,
                    onClick = onLike
                )
                Spacer(Modifier.width(10.dp))
                CommentButton(post.comments_count?.toIntOrNull() ?: 0)
            }
        }
    }
}

@Composable
fun LikeButton(
    count: Int,
    isLiked: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = count.toString(), color = Color.White, modifier = Modifier.padding(end = 4.dp))
        Icon(
            painter = painterResource(if (isLiked) R.drawable.heart_solid else R.drawable.heart_regular),
            contentDescription = null,
            tint = if (isLiked) Color.Red else if (enabled) Color.White else Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .clickable(enabled = enabled, onClick = onClick)
        )
    }
}

@Composable
fun CommentButton(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = count.toString(), color = Color.White, modifier = Modifier.padding(end = 4.dp))
        Icon(
            painter = painterResource(R.drawable.comment_regular),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PostImage(imageUrl: String, isVideo: Boolean, context: Context) {
    if (isVideo) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
        ) {
            var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).apply {
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                                mediaPlayer = MediaPlayer().apply {
                                    setSurface(Surface(surface))
                                    setDataSource(ctx, imageUrl.toUri())
                                    prepareAsync()
                                    setOnPreparedListener { it.isLooping = true; it.start() }
                                }
                            }
                            override fun onSurfaceTextureSizeChanged(s: SurfaceTexture, w: Int, h: Int) {}
                            override fun onSurfaceTextureDestroyed(s: SurfaceTexture): Boolean {
                                mediaPlayer?.release(); mediaPlayer = null; return true
                            }
                            override fun onSurfaceTextureUpdated(s: SurfaceTexture) {}
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            DisposableEffect(imageUrl) { onDispose { mediaPlayer?.release() } }
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(context).data(imageUrl).build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PostDescription(description: String) {
    Spacer(Modifier.height(8.dp))
    Text(text = description, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 4.dp))
}

@Composable
private fun UserInfo(post: Post) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = post.profileImageUrl,
            contentDescription = null,
            modifier = Modifier.size(36.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text("${post.name} ${post.lastName}", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(post.time, color = Color.Gray, fontSize = 12.sp)
        }
    }
}