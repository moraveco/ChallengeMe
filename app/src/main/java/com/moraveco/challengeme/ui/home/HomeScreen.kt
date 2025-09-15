package com.moraveco.challengeme.ui.home

import PermissionHandler
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.moraveco.challengeme.TopBar
import com.moraveco.challengeme.data.Post
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.containsPostId
import com.moraveco.challengeme.data.likedPost
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.time.LocalDate
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    name: String,
    friendsPosts: List<Post>,
    publicPosts: List<Post>,
    historyPosts: List<Post>,
    likes: List<Like>,
    navController: NavController,
    myUid: String,
    likePost: (String, String?, Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    val context = LocalContext.current
    Log.v("posts", historyPosts.size.toString())
    PermissionHandler(
        context = context,
        permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    ) {}
    Scaffold(
        topBar = { TopBar(navController) },
        containerColor = Background
    ) {
        PostsList(
            name = name,
            friendsPosts = friendsPosts,
            publicPosts = publicPosts,
            historyPosts = historyPosts,
            likes = likes,
            navController = navController,
            myUid = myUid,
            likePost = likePost,
            deleteLike = deleteLike
        )
    }
}

@Composable
private fun PostsList(
    name: String,
    friendsPosts: List<Post>,
    publicPosts: List<Post>,
    historyPosts: List<Post>,
    likes: List<Like>,
    navController: NavController,
    myUid: String,
    likePost: (String, String?, Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    // Get today's like (if any)
    val todayLike = likes.likedPost(myUid)

    LazyColumn(
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        postsSection(
            name = name,
            posts = friendsPosts,
            nextSectionTitle = R.string.public_posts,
            isHistorySection = false,
            todayLike = todayLike,
            likes = likes,
            myUid = myUid,
            navController = navController,
            likePost = likePost,
            deleteLike = deleteLike
        )

        postsSection(
            name = name,
            posts = publicPosts,
            nextSectionTitle = R.string.history,
            isHistorySection = false,
            todayLike = todayLike,
            likes = likes,
            myUid = myUid,
            navController = navController,
            likePost = likePost,
            deleteLike = deleteLike
        )

        postsSection(
            name = name,
            posts = historyPosts,
            nextSectionTitle = null,
            isHistorySection = true, // This is the history section
            todayLike = todayLike,
            likes = likes,
            myUid = myUid,
            navController = navController,
            likePost = likePost,
            deleteLike = deleteLike
        )
    }
}

@SuppressLint("UnrememberedMutableState")
private fun LazyListScope.postsSection(
    name: String,
    posts: List<Post>,
    nextSectionTitle: Int?,
    isHistorySection: Boolean,
    todayLike: Like?,
    likes: List<Like>,
    myUid: String,
    navController: NavController,
    likePost: (String, String?, Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    if (posts.isNotEmpty()) {
        itemsIndexed(posts) { _, post ->
            val existingLike = likes.find { it.postId == post.id && it.likeUid == myUid }
            val hasLikedThisPost = existingLike != null
            val hasLikedToday = todayLike != null
            val isThisTodaysLike = todayLike?.postId == post.id

            PostCard(
                name = name,
                post = post,
                isHistoryPost = isHistorySection,
                hasLikedToday = hasLikedToday,
                isThisTodaysLike = isThisTodaysLike,
                hasLikedThisPost = hasLikedThisPost,
                myUid = myUid,
                existingLike = existingLike,
                onClick = { navController.navigate(Screens.Post(post.id)) },
                likePost = likePost,
                deleteLike = deleteLike
            )
        }

        nextSectionTitle?.let {
            item {
                SectionDivider(title = stringResource(it))
            }
        }
    }
}

@Composable
private fun SectionDivider(title: String) {
    Column {
        Spacer(Modifier.height(10.dp))
        DividerWithText(title)
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        Text(
            text = text,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp
        )
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
    name: String,
    post: Post,
    isHistoryPost: Boolean,
    hasLikedToday: Boolean,
    isThisTodaysLike: Boolean,
    hasLikedThisPost: Boolean,
    myUid: String,
    existingLike: Like?,
    onClick: () -> Unit,
    likePost: (String, String?, Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    // Local state to track like status
    var isLiked by remember(hasLikedThisPost) { mutableStateOf(hasLikedThisPost) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Bars, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        PostImage(post.image, post.isVideo.toBoolean(), LocalContext.current)
        PostDescription(post.description)
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        PostFooter(
            post = post,
            isHistoryPost = isHistoryPost,
            hasLikedToday = hasLikedToday,
            isThisTodaysLike = isThisTodaysLike,
            isLiked = isLiked,
            onLikeClick = {
                // Determine if user can perform this action
                val canLike = !isHistoryPost && !hasLikedToday && !isLiked
                val canUnlike = !isHistoryPost && isThisTodaysLike && isLiked

                if (canLike) {
                    // User can like this post (it's today's post and they haven't liked anything today)
                    isLiked = true
                    likePost(
                        name,
                        post.token,
                        Like(UUID.randomUUID().toString(), post.uid, myUid, post.id, LocalDate.now().toString())
                    )
                } else if (canUnlike) {
                    // User can unlike this post (it's the post they liked today)
                    isLiked = false
                    existingLike?.let { deleteLike(it.id) }
                }
                // If neither condition is met, do nothing (the click is disabled)
            }
        )
    }
}

@Composable
private fun PostFooter(
    post: Post,
    isHistoryPost: Boolean,
    hasLikedToday: Boolean,
    isThisTodaysLike: Boolean,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        UserInfo(post)
        PostStats(
            post = post,
            isHistoryPost = isHistoryPost,
            hasLikedToday = hasLikedToday,
            isThisTodaysLike = isThisTodaysLike,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
        )
    }
}


@Composable
private fun PostStats(
    post: Post,
    isHistoryPost: Boolean,
    hasLikedToday: Boolean,
    isThisTodaysLike: Boolean,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    // Determine if the like button should be enabled
    val canInteract = when {
        isHistoryPost -> false // Can't interact with history posts
        isThisTodaysLike -> true // Can unlike today's liked post
        hasLikedToday -> false // Already liked something today, can't like another
        else -> true // Can like this post
    }

    // Determine icon and color
    val icon = if (isLiked) R.drawable.heart_solid else R.drawable.heart_regular
    val tint = when {
        isLiked -> Color.Red // Show red for liked posts
        !canInteract && hasLikedToday -> Color.Gray // Show gray for disabled (already liked today)
        else -> Color.White // Show white for available to like
    }

    if (post.likes_count != null && post.likes_count.isDigitsOnly() &&
        post.comments_count != null && post.comments_count.isDigitsOnly()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            StatItem(
                count = post.likes_count.toInt(), // Simply use the post's like count
                icon = icon,
                tint = tint,
                enabled = canInteract,
                onClick = onLikeClick
            )

            Spacer(modifier = Modifier.width(12.dp))

            StatItem(
                count = post.comments_count.toInt(),
                icon = R.drawable.comment_regular,
                tint = Color.White,
                enabled = false
            )
        }
    }
}

// Keep other composables unchanged (PostImage, PostDescription, UserInfo, StatItem, etc.)

@Composable
private fun PostImage(imageUrl: String, isVideo: Boolean, context: Context) {
    if (isVideo) {
        // Add video player with autoplay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

            AndroidView(
                factory = { context ->
                    TextureView(context).apply {
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                mediaPlayer = MediaPlayer().apply {
                                    setSurface(Surface(surface))
                                    setDataSource(context, imageUrl.toUri())
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
                                // Handle size changes if needed
                            }

                            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                mediaPlayer?.release()
                                mediaPlayer = null
                                return true
                            }

                            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                                // No action needed
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            DisposableEffect(imageUrl) {
                onDispose {
                    mediaPlayer?.release()
                }
            }
        }
    } else {
        // Keep original image handling
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .build(),
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
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = description,
        color = Color.White,
        fontSize = 16.sp,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}


@Composable
private fun UserInfo(post: Post) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
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
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = post.time,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    icon: Int,
    tint: Color,
    enabled: Boolean,
    onClick: () -> Unit = {}
) {
    Text(
        text = count.toString(),
        color = Color.White,
        modifier = Modifier.padding(end = 4.dp)
    )
    Icon(
        painterResource(icon),
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .size(20.dp)
            .clickable(enabled = enabled, onClick = onClick)
    )
}

