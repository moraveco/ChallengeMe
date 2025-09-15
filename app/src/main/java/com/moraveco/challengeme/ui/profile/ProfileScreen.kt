package com.moraveco.challengeme.ui.profile

import android.annotation.SuppressLint
import android.graphics.Shader
import android.os.Build
import com.moraveco.challengeme.R
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(user: ProfileUser, posts: List<Post>, navController: NavController) {
    val context = LocalContext.current
    Scaffold(containerColor = Background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.Transparent)
            ) {
                if (user.secondImageUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_background),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Transparent,
                                        Background.copy(alpha = 0.1f),
                                        Background.copy(alpha = 0.3f),
                                        Background.copy(alpha = 0.5f),
                                        Background.copy(alpha = 0.7f),
                                        Background.copy(alpha = 0.85f),
                                        Background.copy(alpha = 0.95f),
                                        Background
                                    ),
                                    startY = size.height * 0.2f,  // Start gradient at 20% from top
                                    endY = size.height
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(brush = gradient)
                                }
                            }
                    )
                } else {
                    AsyncImage(
                        model = user.secondImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .drawWithCache {
                                val gradient = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Transparent,
                                        Background.copy(alpha = 0.1f),
                                        Background.copy(alpha = 0.3f),
                                        Background.copy(alpha = 0.5f),
                                        Background.copy(alpha = 0.7f),
                                        Background.copy(alpha = 0.85f),
                                        Background.copy(alpha = 0.95f),
                                        Background
                                    ),
                                    startY = size.height * 0.2f,  // Start gradient at 20% from top
                                    endY = size.height
                                )
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(brush = gradient)
                                }
                            }
                    )
                }

                TopBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp)  // Volitelné odsazení do stran
                ){
                    navController.navigate(Screens.Menu)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(20.dp))
                    AsyncImage(
                        model = user.profileImageUrl, // Profile image
                        contentDescription = "Profile Picture",
                        modifier = Modifier.run {
                            size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        user.name + " " + user.lastName,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(user.country, color = Color.Gray, fontSize = 14.sp)
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(Color(0xFF0B0D47), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                ProfileStat(posts.size.toString(), stringResource(R.string.posts))
                ProfileStat(user.follow.toString(), stringResource(R.string.friends))
                ProfileStat("4.", stringResource(R.string.order))
                ProfileStat(user.likes.toString(), stringResource(R.string.likes))
            }

            Spacer(Modifier.height(8.dp))

            if (posts.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.camera),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                        )
                        Text(text = stringResource(R.string.no_posts), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                    }

                }
            }else{
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(500.dp)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 64.dp)
                ) {

                    items(posts.size) { imageRes ->
                        val post = posts[imageRes]
                        val model = if (post.isVideo == "true") {
                            ImageRequest.Builder(context)
                                .data(post.image) // This should be the video URL
                                .videoFrameMillis(1000) // Take frame at 1 second
                                .decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(result.source, options)
                                }
                                .build()
                        } else {
                            ImageRequest.Builder(context)
                                .data(post.image) // This should be the image URL
                                .build()
                        }
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    navController.navigate(Screens.Post(post.id))
                                }
                        )
                    }
                }
            }


        }
    }


}

@Composable
fun TopBar(modifier: Modifier = Modifier, navigate: () -> Unit) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Default.LocalFireDepartment, null, tint = Color.White)
        }
        IconButton(onClick = navigate) {
            Icon(imageVector = Icons.Default.Menu, null, tint = Color.White)
        }
    }
}

@Composable
fun ProfileStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
        Text(text = label, color = Color.LightGray, fontSize = 14.sp)
    }
}


@Preview(name = "ProfileScreen")
@Composable
private fun PreviewProfileScreen() {
    ProfileScreen(user = ProfileUser.empty(), listOf(), rememberNavController())
}