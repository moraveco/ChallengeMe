package com.moraveco.challengeme.ui.register

import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.di.MediaCompressionUtil
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.ui.profile.edit.CountrySelector
import com.moraveco.challengeme.ui.profile.edit.ProfileTextField
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SecondRegisterScreen(
    navController: NavController,
    email: String,
    password: String,
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by registerViewModel.uiState.collectAsState()

    val profileImageState = remember { mutableStateOf("") }
    val secondImageState = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("Czech republic") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSecondImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        // Here you would typically upload the image and get the URL
        // For now, we'll use the URI string as a placeholder
        uri?.let { profileImageState.value = it.toString() }
    }

    val secondImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedSecondImageUri = uri
        // Here you would typically upload the image and get the URL
        uri?.let { secondImageState.value = it.toString() }
    }

    val uid = UUID.randomUUID().toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section with Background Image and Profile Picture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // set a fixed height for the header section
        ) {
            if (selectedSecondImageUri != null) {
                AsyncImage(
                    model = selectedSecondImageUri,
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize() // now fills just the 200.dp box
                        .align(Alignment.TopCenter)
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Bars.copy(alpha = 0.3f),
                                    Bars.copy(alpha = 0.6f),
                                    Bars.copy(alpha = 0.9f),
                                    Bars.copy(alpha = 0.95f)
                                ),
                                startY = size.height * 0.5f,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(brush = gradient)
                            }
                        }
                        .clickable {
                            secondImagePickerLauncher.launch("image/*")
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter)
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Bars.copy(alpha = 0.3f),
                                    Bars.copy(alpha = 0.6f),
                                    Bars.copy(alpha = 0.9f),
                                    Bars.copy(alpha = 0.95f)
                                ),
                                startY = size.height * 0.5f,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(brush = gradient)
                            }
                        }
                        .clickable {
                            secondImagePickerLauncher.launch("image/*")
                        }
                )
            }

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .align(Alignment.BottomCenter)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_default),
                        contentDescription = "Default Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Camera overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                    )
                }
            }
        }

        // Loading overlay
        LoadingBox(uiState.isAuthenticating)

        Spacer(modifier = Modifier.height(24.dp))

        // Form Section
        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            // Instructions
            Text(
                text = "Dokončete svůj profil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Vyplňte své údaje pro dokončení registrace. Profilové a pozadí fotky jsou volitelné.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name fields
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileTextField(
                    label = "Jméno",
                    value = uiState.name,
                    onValueChange = { registerViewModel.updateName(it) },
                    icon = Icons.Default.Person,
                    maxWidth = false
                )
                Spacer(Modifier.width(15.dp))
                ProfileTextField(
                    label = "Příjmení",
                    value = uiState.lastName,
                    onValueChange = { registerViewModel.updateLastName(it) },
                    icon = Icons.Default.Person,
                    maxWidth = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country selector
            CountrySelector(
                icon = Icons.Default.LocationOn,
                selectedCountry = country
            )

            Spacer(Modifier.height(40.dp))

            val validation by registerViewModel.validationErrors.collectAsState()


            // Register button
            Button(
                onClick = {
                    if (uiState.name.isBlank() || uiState.lastName.isBlank()) {
                        Toast.makeText(context, "Vyplňte všechna povinná pole", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    registerViewModel.viewModelScope.launch {
                        val profilePart = selectedImageUri?.let { registerViewModel.toMultiPart(context, it) }
                        val secondPart = selectedSecondImageUri?.let { registerViewModel.toMultiPart(context, it) }

                        // You could also upload these files here and get their URLs if your API requires URLs

                        registerViewModel.updateEmail(email)
                        registerViewModel.updatePassword(password)
                        registerViewModel.updateCountry(country.value)
                        registerViewModel.updateTermsAccepted(true)
                        registerViewModel.registerUser(
                            RegisterData(
                                uid = uid,
                                email = uiState.email,
                                password = md5(uiState.password),
                                name = uiState.name,
                                lastName = uiState.lastName,
                                country = uiState.country,
                                profileImage = profilePart,   // or profilePart?.body if you need only the RequestBody
                                secondImage = secondPart
                            )
                        )
                        validation.forEach {
                            Log.v("register", it.key + " - " + it.value)
                        }


                    }
                },

                colors = ButtonDefaults.buttonColors(containerColor = Color(8, 131, 255)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isAuthenticating && uiState.name.isNotBlank() && uiState.lastName.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (uiState.isAuthenticating) "Registruji..." else "Dokončit registraci",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    LaunchedEffect(
        key1 = uiState.authenticationSucceed,
        key2 = uiState.authErrorMessage,
        block = {
            if (uiState.authenticationSucceed) {
                navController.navigate(Screens.Home) {
                    // Clear the back stack so user can't go back to registration
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }

            if (uiState.authErrorMessage != null) {
                Toast.makeText(context, "Registrace neúspěšná: ${uiState.authErrorMessage}", Toast.LENGTH_LONG).show()
            }
        }
    )
}