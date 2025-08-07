package com.moraveco.challengeme.ui.posts

import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.moraveco.challengeme.data.Like
import com.moraveco.challengeme.data.containsPostId
import com.moraveco.challengeme.data.likedPost
import java.time.LocalDate
import java.time.LocalDateTime


@Composable
fun PostScreen(
    name: String,
    id: String,
    myUid: String,
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel()
) {

    LaunchedEffect(id) {
        postViewModel.getPostById(id)
        postViewModel.getComments(id)
        postViewModel.getLikes(id)
    }

    val post by postViewModel.post.collectAsState()
    val comments by postViewModel.comments.collectAsState()
    val likes by postViewModel.likes.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val podminka = remember(post, likes) {
        post.time.isNotEmpty() &&
                LocalDate.parse(post.time).dayOfYear == LocalDate.now().dayOfYear &&
                !likes.containsPostId(id) &&
                post.uid != myUid
    }


    var text by remember { mutableStateOf(TextFieldValue("")) }
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
                // Původní obsah zůstává stejný až po CommentRecycler1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF53A1FD))
                        }
                        Text(
                            text = stringResource(R.string.back),
                            fontSize = 18.sp,
                            color = Color(0xFF53A1FD)
                        )
                    }

                    if (post.uid == myUid) {
                        IconButton(onClick = {showDeleteDialog = true}) {
                            Icon(imageVector = Icons.Default.Delete, null, tint = Color(0xFF53A1FD))
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
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Report, null, tint = Color(0xFF53A1FD))
                        }                    }


                }

                Column(modifier = Modifier.background(Bars, RoundedCornerShape(20.dp))) {

                    if (post.isVideo == "true") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            AndroidView(
                                factory = { context ->
                                    VideoView(context).apply {
                                        setVideoURI(post.image.toUri())
                                        setOnPreparedListener { mediaPlayer ->
                                            mediaPlayer.isLooping = true
                                            mediaPlayer.setVideoScalingMode(
                                                android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT
                                            )
                                            start()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    else {
                        // Fallback to image
                        AsyncImage(
                            model = post.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .aspectRatio(3f / 4f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Prompt text
                    Text(
                        text = post.description,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

                    // User info and likes/comments
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.5f)) {
                            AsyncImage(
                                model = post.profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop

                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.clickable(post.uid != myUid){navController.navigate(Screens.UserProfile(post.uid))}) {
                                Text(text = post.name + " " + post.lastName, color = Color.White, fontSize = 16.sp)
                                Text(text = post.time, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        val hasLiked = likes.containsPostId(post.id)

                        val likeIcon = if (hasLiked) {
                            R.drawable.heart_solid
                        } else {
                            R.drawable.heart_regular
                        }

                        val likeTint = when {
                            hasLiked -> Color.Red
                            podminka -> Color.Red
                            else -> Color.White
                        }

                        val likeEnabled = podminka || hasLiked

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Like section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val hasLiked = likes.containsPostId(post.id)
                                val likeIcon = if (hasLiked) {
                                    R.drawable.heart_solid
                                } else {
                                    R.drawable.heart_regular
                                }
                                val likeTint = when {
                                    hasLiked -> Color.Red
                                    podminka -> Color.Red
                                    else -> Color.White
                                }
                                val likeEnabled = podminka || hasLiked

                                Text(
                                    text = post.likes_count.toString(),
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Icon(
                                    painter = painterResource(likeIcon),
                                    contentDescription = "Like",
                                    tint = likeTint,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable(enabled = likeEnabled) {
                                            if (hasLiked) {
                                                postViewModel.deleteLike(myUid, post.id) {}
                                            } else {
                                                postViewModel.insertLike(
                                                    myUid, post.token, Like(
                                                        UUID.randomUUID().toString(),
                                                        post.uid,
                                                        myUid,
                                                        post.id,
                                                        LocalDate.now().toString()
                                                    )
                                                ) {}
                                            }
                                        }
                                )
                            }

                            // Comment section
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = post.comments_count.toString(),
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

                // ... (všechen další původní obsah)

                Spacer(Modifier.height(10.dp))
                CommentRecycler1(comments, navController)

                // Přidáme prostor pro input field
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Input field přesuneme do Box a umístíme na spodek
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
                    onValueChange = {text = it},
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
                IconButton(onClick = { postViewModel.sendComment(name, post.token, CommentData(UUID.randomUUID().toString(), myUid, post.id, text.text)){ text = TextFieldValue("") } }) {
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
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(top = 20.dp, start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = comment.profileImageUrl, contentDescription = null, modifier = Modifier
            .size(35.dp)
            .clip(
                CircleShape
            )
            .clickable {
                navController.navigate(Screens.UserProfile(comment.posterUid))
            }, contentScale = ContentScale.Crop)
        Card(shape = RoundedCornerShape(17.dp), modifier = Modifier.padding(horizontal = 10.dp), colors = CardDefaults.cardColors(containerColor = Bars)) {
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
    LazyColumn(modifier = Modifier.height(500.dp)){
        items(comments.size) {position ->
            CommentItem1(comment = comments[position], navController = navController)

        }
    }
}