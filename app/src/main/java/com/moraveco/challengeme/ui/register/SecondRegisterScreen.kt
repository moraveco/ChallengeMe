package com.moraveco.challengeme.ui.register

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.ui.profile.edit.CountrySelector
import com.moraveco.challengeme.ui.profile.edit.ProfileTextField
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import java.util.UUID

@Composable
fun SecondRegisterScreen(navController: NavController, email: String, password: String, registerViewModel: RegisterViewModel = hiltViewModel()){
   val uiState = registerViewModel.uiState

    val nameState = remember { mutableStateOf("") }
    val lastNameState = remember { mutableStateOf("") }
    val profileImageState = remember { mutableStateOf("") }
    val secondImageState = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("Czech republic") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSecondImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    val secondImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedSecondImageUri = uri }

    val uid = UUID.randomUUID().toString()
    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Box(modifier = Modifier.fillMaxHeight(0.3f)){
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

                    }.clickable{
                        secondImagePickerLauncher.launch("image/*")
                    }
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .align(Alignment.BottomCenter)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_default),
                        contentDescription = "Default Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Icon",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        LoadingBox(uiState.isAuthenticating)
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            ProfileTextField("Jméno", nameState, icon = Icons.Default.Person, maxWidth = false)
            Spacer(Modifier.width(15.dp))
            ProfileTextField("Příjmení", lastNameState, icon = Icons.Default.Person, maxWidth = true)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            CountrySelector(icon = Icons.Default.LocationOn, selectedCountry = country)

        }
        Spacer(Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                registerViewModel.registerUser(
                    RegisterData(
                        uid = uid,
                        email = email,
                        password = password
                    ),
                    User(
                        uid = uid,
                        name = nameState.value,
                        lastName = lastNameState.value,
                        bio = "",
                        email = email,
                        profileImageUrl = profileImageState.value,
                        secondImageUrl = secondImageState.value,
                        country = country.value
                    )
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(8, 131, 255)), modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(text = "Registrovat")
            }
        }


    }

    LaunchedEffect(
        key1 = uiState.authenticationSucceed,
        key2 = uiState.authErrorMessage,
        block = {
            if (uiState.authenticationSucceed) {
                navController.navigate(Screens.Home)
            }

            if (uiState.authErrorMessage != null) {
                Toast.makeText(context, "Registrace neúspěšná", Toast.LENGTH_SHORT).show()
            }
        }
    )
}