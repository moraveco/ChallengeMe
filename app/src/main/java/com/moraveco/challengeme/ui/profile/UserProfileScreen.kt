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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Follow
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.time.LocalDateTime
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    user: ProfileUser,
    friend: Friend?,
    posts: List<Post>,
    myUid: String,
    navController: NavController,
    acceptRequest: (String) -> Unit,
    followUser: (Follow) -> Unit,
    deleteFriend: (String) -> Unit
) {
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
                Image(
                    painter = painterResource(R.drawable.profile_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight(0.7f)
                        .fillMaxWidth()
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Bars.copy(alpha = 0.3f),  // Přidaný mezikrok
                                    Bars.copy(alpha = 0.6f),  // Přidaný mezikrok
                                    Bars.copy(alpha = 0.9f),
                                    Bars.copy(alpha = 0.95f)  // Silnější koncová hodnota
                                ),
                                startY = size.height * 0.5f,  // Začíná výše
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(brush = gradient)
                            }

                        }
                )

                UserTopBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp),
                    navController,
                    myUid = myUid,
                    hisUid = user.uid,
                    user = friend ?: Friend.empty(),
                    acceptRequest = acceptRequest,
                    followUser = followUser,
                    deleteFriend = deleteFriend
                )

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
                ProfileStat(posts.size.toString(), "Příspěvky")
                ProfileStat(user.follow.toString(), "Přátelé")
                ProfileStat("4.", "Pořadí")
                ProfileStat(user.likes.toString(), "Líbí se")
            }

            Spacer(Modifier.height(8.dp))

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
                    AsyncImage(
                        model = posts[imageRes].image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate(Screens.Post(posts[imageRes].id))
                            }
                    )
                }
            }
        }
    }


}

@Composable
fun UserTopBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    myUid: String,
    hisUid: String,
    user: Friend,
    acceptRequest: (String) -> Unit = {},
    followUser: (Follow) -> Unit = {},
    deleteFriend: (String) -> Unit = {}
) {
    val color = if (user.uid.isNotEmpty()) {
        if (user.isAccept) {
            Color(247, 69, 69)

        } else if (user.receiverUid == myUid) {
            Color(107, 227, 77)
        } else {
            Color(222, 182, 51)
        }
    } else {
        Color.White
    }
    val text = if (user.uid.isNotEmpty()) {
        if (user.isAccept) {
            "Odstranit"

        } else if (user.receiverUid == myUid) {
            "Přijmout"
        }
        else {
            "Posláno"
        }
    } else {
        "Přidat"
    }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF53A1FD))
            }
            Text(
                text = "Zpět",
                fontSize = 18.sp,
                color = Color(0xFF53A1FD)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(7.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Bars), onClick = {
                if (user.uid.isNotEmpty()) {
                    if (user.isAccept) {
                        deleteFriend(user.id)
                    } else if (user.receiverUid == myUid) {
                        acceptRequest(user.id)
                    } else {
                        followUser(Follow(UUID.randomUUID().toString(), myUid, hisUid, "false",
                            LocalDateTime.now().toString()))
                    }
                }
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(7.dp)
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(text, color = color)
                }
            }
            Spacer(Modifier.width(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Bars)) {
                Icon(imageVector = Icons.Default.MoreHoriz, null, tint = Color.White)

            }

        }


    }
}


@Preview(name = "ProfileScreen")
@Composable
private fun PreviewProfileScreen() {
    UserProfileScreen(
        user = ProfileUser.empty(),
        friend = Friend.empty(),
        listOf(),
        myUid = "",
        rememberNavController(),
        {},
        {},
        {})
}