package com.moraveco.challengeme.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Switch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.User
import androidx.core.content.edit
import com.moraveco.challengeme.ui.requests.RequestsScreen
import androidx.core.net.toUri
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.nav.Screens.EditProfile
import com.moraveco.challengeme.ui.theme.Bars

@Composable
fun MenuScreen(
    navController: NavController,
    user: User,
    logout: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Back navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface
            )
            Text(stringResource(id = R.string.back), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Bars)
                .padding(16.dp)
                .clickable { navController.navigate(EditProfile(user.uid)) },
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfileImage(55, user.profileImageUrl)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(77, 92, 142)
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

            }

            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Bars)
                .padding(16.dp)
                .clickable { /*navController.navigate(EditProfile(user.uid)) */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.fillMaxSize(0.3f)){
                Image(
                    painter = painterResource(R.drawable.donate),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                Text(
                    text = stringResource(R.string.support_us),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(77, 92, 142)
                )
                Text(
                    text = stringResource(R.string.sent_donate),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {}, shape = RoundedCornerShape(10.dp)) {
                    Text(text = stringResource(R.string.sent_now))
                }

            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Settings Section
        Text(
            text = stringResource(id = R.string.settingsandpreferences),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        SettingItem(
            icon = Icons.Default.Notifications,
            title = stringResource(id = R.string.notifications),
            onClick = {
            }
        )

        Text(
            text = stringResource(id = R.string.support),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface

        )

        SettingItem(
            icon = Icons.Default.Lock,
            title = stringResource(id = R.string.privacyapolicy),
            onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://mymedevelopers.com/blog/privacy-policy-rentme.html".toUri()
                )
                context.startActivity(intent)
            }
        )

        SettingItem(
            icon = Icons.Default.ContactSupport,
            title = stringResource(id = R.string.contactus),
            onClick = { /*navController.navigate(HomeActivity.ContactForm(email = user.email)) */}
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.logout),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        // Log Out Section
        SettingItem(
            icon = Icons.Default.Logout,
            title =stringResource(id = R.string.logout),
            onClick = { logout(); navController.navigate(Screens.Login) },
            textColor = Color.Red
        )

        Spacer(modifier = Modifier.height(70.dp))

    }
}

@Composable
fun ProfileImage(size: Int, imageUrl: String?) {
    if (imageUrl == null){
        Image(
            painter = painterResource(R.drawable.ic_default),
            contentDescription = null,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }else{
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }

}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(Bars)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (textColor != Color.Unspecified) textColor else Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        trailingContent?.invoke() ?: Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Preview(name = "MenuScreen", showBackground = true)
@Composable
private fun PreviewRequestsScreen() {
    MenuScreen(rememberNavController(), User.empty().copy(name = "Robinek Balonek", profileImageUrl = "https://mymedevelopers.com/ChallangeMe/profileImage/676bd63e2110c_example", email = "balonek@pojfm.cz")){}
}