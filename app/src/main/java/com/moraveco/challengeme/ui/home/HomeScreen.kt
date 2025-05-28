package com.moraveco.challengeme.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.containsPostId
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    friendsPosts: List<Post>,
    publicPosts: List<Post>,
    historyPosts: List<Post>,
    likes: List<Like>,
    navController: NavController,
    myUid: String,
    likePost: (Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    Scaffold(
        topBar = { TopBar(navController) },
        containerColor = Background
    ) {
        PostsList(
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
    friendsPosts: List<Post>,
    publicPosts: List<Post>,
    historyPosts: List<Post>,
    likes: List<Like>,
    navController: NavController,
    myUid: String,
    likePost: (Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        postsSection(
            posts = friendsPosts,
            nextSectionTitle = "Veřejné příspěvky",
            isOldPost = false,
            likes = likes,
            myUid = myUid,
            navController = navController,
            likePost = likePost,
            deleteLike = deleteLike
        )

        postsSection(
            posts = publicPosts,
            nextSectionTitle = "Historie",
            isOldPost = false,
            likes = likes,
            myUid = myUid,
            navController = navController,
            likePost = likePost,
            deleteLike = deleteLike
        )

        postsSection(
            posts = historyPosts,
            nextSectionTitle = null,
            isOldPost = true,
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
    posts: List<Post>,
    nextSectionTitle: String?,
    isOldPost: Boolean,
    likes: List<Like>,
    myUid: String,
    navController: NavController,
    likePost: (Like) -> Unit,
    deleteLike: (String) -> Unit
) {
    if (posts.isNotEmpty()) {
        itemsIndexed(posts) { _, post ->
            PostCard(
                post = post,
                isOldPost = isOldPost,
                myUid = myUid,
                like = likes.find { it.postId == post.id && it.likeUid == myUid },
                containsLike = mutableStateOf(likes.containsPostId(post.id)),
                onClick = { navController.navigate(Screens.Post(post.id)) },
                likePost = likePost,
                deleteLike = deleteLike
            )
        }

        nextSectionTitle?.let {
            item {
                SectionDivider(title = it)
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
    if (isLoading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }

}


@Composable
fun PostCard(
    post: Post,
    isOldPost: Boolean,
    myUid: String,
    like: Like?,
    containsLike: MutableState<Boolean>,
    onClick: () -> Unit,
    likePost: (Like) -> Unit,
    deleteLike: (String) -> Unit
) {
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
            isOldPost = isOldPost,
            myUid = myUid,
            containsLike = containsLike,
            onLikeClick = {
                if (!containsLike.value){
                    containsLike.value = true  // Aktualizujeme stav při kliknutí
                    likePost(Like(UUID.randomUUID().toString(), post.uid, myUid, post.id))
                }else{
                    containsLike.value = false
                    if (like != null) deleteLike(like.id)
                }

            }
        )
    }
}

@Composable
private fun PostFooter(
    post: Post,
    isOldPost: Boolean,
    myUid: String,
    containsLike: MutableState<Boolean>,
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
            isOldPost = isOldPost,
            containsLike = containsLike,
            onLikeClick = onLikeClick,
            myUid = myUid

        )
    }
}

@Composable
private fun PostStats(
    post: Post,
    myUid: String,
    isOldPost: Boolean,
    containsLike: MutableState<Boolean>,
    onLikeClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(0.5f)
    ) {
        StatItem(
            count = post.likes_count ?: 0,
            icon = if (isOldPost || containsLike.value || post.uid == myUid) R.drawable.heart_solid else R.drawable.heart_regular,
            tint = if (isOldPost || post.uid == myUid) Color.White else Color.Red,
            enabled = !isOldPost || post.uid != myUid,
            onClick = onLikeClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        StatItem(
            count = post.comments_count ?: 0,
            icon = R.drawable.comment_regular,
            tint = Color.White,
            enabled = false
        )
    }
}


@Composable
private fun PostImage(imageUrl: String, isVideo: Boolean, context: Context) {
    val model = if (isVideo) {
        ImageRequest.Builder(context)
            .data(imageUrl) // This should be the video URL
            .videoFrameMillis(1000) // Take frame at 1 second
            .decoderFactory { result, options, _ ->
                VideoFrameDecoder(result.source, options)
            }
            .build()
    } else {
        ImageRequest.Builder(context)
            .data(imageUrl) // This should be the image URL
            .build()
    }
    AsyncImage(
        model = model,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop
    )
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

