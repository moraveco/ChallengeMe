package com.moraveco.challengeme.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moraveco.challengeme.R
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars


@Composable
fun PostScreen(
    post: Post,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp)
    ) {

        // Top bar with back
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
                    text = "Zpět",
                    fontSize = 18.sp,
                    color = Color(0xFF53A1FD)
                )
            }

            Icon(imageVector = Icons.Default.Report, null, tint = Color(0xFF53A1FD))


        }

        Spacer(Modifier.height(8.dp))

        Column (modifier = Modifier.background(Bars, RoundedCornerShape(20.dp))){
            AsyncImage(
                model = post.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .aspectRatio(3f / 4f)
            )

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
                    Column {
                        Text(text = post.name + " " + post.lastName, color = Color.White, fontSize = 16.sp)
                        Text(text = post.time, color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = post.likes_count.toString(), color = Color.White, modifier = Modifier.padding(end = 10.dp))
                    Icon(painter = painterResource(R.drawable.heart_regular), contentDescription = "Like", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = post.comments_count.toString(), color = Color.White, modifier = Modifier.padding(end = 10.dp))
                    Icon(painter = painterResource(R.drawable.comment_regular), contentDescription = "Comment", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Post image


        Spacer(Modifier.weight(1f))

        // Comment input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Bars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text("Tvůj komentář…", color = Color.Gray)
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
            IconButton(onClick = { /* send comment */ }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF53A1FD))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
