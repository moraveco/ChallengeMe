package com.moraveco.challengeme.ui.requests

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.data.AcceptRequest
import com.moraveco.challengeme.data.Friend
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.DividerWithText
import com.moraveco.challengeme.ui.theme.Bars

@Composable
fun RequestsScreen(friends: List<Friend> = emptyList(), navController: NavController, acceptRequest: (String) -> Unit, deleteFriend: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.align(
                    Alignment.CenterStart
                ).clickable{
                    navController.popBackStack()
                }
            )
            Text(
                "Přátelé a žádosti",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(Modifier.height(20.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(friends.size) {
                FriendItem(friends[it], acceptRequest, deleteFriend)
            }
        }
    }
}

@Preview(name = "RequestsScreen")
@Composable
private fun PreviewRequestsScreen() {
    RequestsScreen(listOf(), rememberNavController(), {}){}
}

@Composable
fun FriendItem(user: Friend, acceptRequest: (String) -> Unit, deleteFriend: (String) -> Unit) {
    val color = if (user.isAccept) Color(247, 69, 69) else Color(107, 227, 77)
    val text = if (user.isAccept) "Odstranit" else "Přijmout"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.profileImageUrl,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "${user.name} ${user.lastName}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

        }
        Card(colors = CardDefaults.cardColors(containerColor = Bars), onClick = {
            if (user.isAccept){
                deleteFriend(user.id)
            } else{
                acceptRequest(user.id)
            }
        }) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(7.dp)) {
                Icon(Icons.Default.PersonAdd, null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(5.dp))
                Text(text, color = color)
            }
        }
    }
}