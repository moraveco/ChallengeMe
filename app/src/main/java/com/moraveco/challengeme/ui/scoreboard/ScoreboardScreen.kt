package com.moraveco.challengeme.ui.scoreboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.LeadeboardUser
import com.moraveco.challengeme.ui.theme.Bars

@Composable
fun ScoreboardScreen(
    today: List<LeadeboardUser>,
    global: List<LeadeboardUser>,
    friends: List<LeadeboardUser>
) {
    val selectedTab = remember { mutableIntStateOf(1) } // 0 = Dnes, 1 = Globální, 2 = Přátelé
    val tabTitles = listOf(stringResource(R.string.today),
        stringResource(R.string.global), stringResource(R.string.friends))

    val currentList = when (selectedTab.intValue) {
        0 -> today
        1 -> global
        2 -> friends
        else -> global
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010038)) // Background color
            .padding(16.dp)
    ) {
        Text(
            text = "Žebříček",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab.intValue,
            containerColor = Bars,
            contentColor = Color.White,
            indicator = {
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(it[selectedTab.intValue]),
                    color = Color(0xFF1E6CFF)
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab.intValue == index,
                    onClick = { selectedTab.intValue = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTab.intValue == index) Color.White else Color.LightGray
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Top 3 section
        if (currentList.size >= 3) {
            TopThreeSection(currentList)
            Spacer(Modifier.height(24.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(Bars)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentList.size) { index ->
                LeaderboardRow(index + 1, currentList[index])
            }
        }
    }
}





@Composable
fun TopThreeSection(leadeboardUser: List<LeadeboardUser>) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth(),
    ) {
        TopUserCard(leadeboardUser[1].name, leadeboardUser[1].profileImageUrl ?: "", 2, Color.LightGray)
        TopUserCard(leadeboardUser[0].name, leadeboardUser[0].profileImageUrl ?: "", 1, Color.Yellow)
        TopUserCard(leadeboardUser[2].name, leadeboardUser[2].profileImageUrl ?: "", 3, Color(0xFFB08D57))
    }
}


@Composable
fun TopUserCard(name: String, profileImageUrl: String, rank: Int, borderColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Bars),
        modifier = Modifier
            .width(if (rank == 1) 120.dp else 100.dp)
            .height(if (rank == 1) 165.dp else 150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            //modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(if (rank == 1) 65.dp else 55.dp)
                    .clip(CircleShape)
                    .border(3.dp, borderColor, CircleShape)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = "$rank.",
                color = borderColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun LeaderboardRow(rank: Int, user: LeadeboardUser) {
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
            Spacer(Modifier.width(8.dp))
            Column {
                Text(user.name, color = Color.White, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Likes",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(" ${user.likes_count}", color = Color.White)
                    Icon(
                        Icons.Default.Whatshot,
                        contentDescription = "Flames",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(" ${user.streaks}", color = Color.White)
                }
            }
        }
        Text("$rank.", color = Color.White)
    }

    HorizontalDivider()
}


@Preview(name = "ScoreboardScreen")
@Composable
private fun PreviewScoreboardScreen() {
    ScoreboardScreen(listOf(), listOf(), listOf())
}