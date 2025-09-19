package com.moraveco.challengeme.ui.profile.edit

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.moraveco.challengeme.R
import com.moraveco.challengeme.constants.Constants.Companion.BASE_URL
import com.moraveco.challengeme.data.UpdateProfileData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.theme.Bars
import java.io.File

@Composable
fun EditProfileScreen(
    user: User,
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel(),
    logout: () -> Unit
) {
    // Editable states
    var nameState by remember { mutableStateOf(user.name) }
    var lastNameState by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }
    val country = remember { mutableStateOf(user.country) }

    // Current images
    val profileImageState = remember { mutableStateOf(user.profileImageUrl) }
    val secondImageState = remember { mutableStateOf(user.secondImageUrl) }

    // Selected new images (local only before upload)
    var selectedProfileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSecondImageUri by remember { mutableStateOf<Uri?>(null) }

    // Determine what to display
    val profileImageToDisplay = selectedProfileImageUri?.toString() ?: profileImageState.value
    val secondImageToDisplay = selectedSecondImageUri?.toString() ?: secondImageState.value

    // Upload responses
    val uploadResponse by viewModel.uploadResponse.observeAsState()
    val uploadSecondResponse by viewModel.uploadSecondResponse.observeAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val showDeleteDialog = remember { mutableStateOf(false) }

    // Launchers
    val profileImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedProfileImageUri = uri }

    val secondImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedSecondImageUri = uri }

    // Handle upload success for profile image
    LaunchedEffect(uploadResponse) {
        uploadResponse?.let { result ->
            if (result.success) {
                profileImageState.value = BASE_URL + result.file_path.removePrefix("./")
                viewModel.updateProfile(
                    UpdateProfileData(
                        user.uid, nameState, lastNameState, email,
                        profileImageState.value ?: "", secondImageState.value ?: ""
                    )
                )
                navController.navigate(Screens.Profile)
            } else {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Handle upload success for second image
    LaunchedEffect(uploadSecondResponse) {
        uploadSecondResponse?.let { result ->
            if (result.success) {
                secondImageState.value = BASE_URL + result.file_path.removePrefix("./")
                viewModel.updateProfile(
                    UpdateProfileData(
                        user.uid, nameState, lastNameState, email,
                        profileImageState.value ?: "", secondImageState.value ?: ""
                    )
                )
                navController.navigate(Screens.Profile)
            } else {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top section with background and profile image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            // Second image (background banner)
            if (secondImageToDisplay.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.profile_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .fillMaxWidth()
                        .clickable { secondImagePickerLauncher.launch("image/*") }
                        .gradientOverlay()
                )
            } else {
                AsyncImage(
                    model = secondImageToDisplay,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .fillMaxWidth()
                        .clickable { secondImagePickerLauncher.launch("image/*") }
                        .gradientOverlay()
                )
            }

            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.TopStart)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = stringResource(id = R.string.back),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Profile image (circle)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .align(Alignment.BottomCenter)
                    .clickable { profileImagePickerLauncher.launch("image/*") }
            ) {
                if (profileImageToDisplay.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.heart_solid),
                        contentDescription = "Default Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = profileImageToDisplay,
                        contentDescription = "Profile Image",
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
                        Icons.Default.CameraAlt,
                        contentDescription = "Camera Icon",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Editable fields
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileTextField(
                    stringResource(R.string.name),
                    nameState,
                    onValueChange = { nameState = it },
                    icon = Icons.Default.Person,
                    maxWidth = false
                )
                Spacer(Modifier.width(15.dp))
                ProfileTextField(
                    stringResource(R.string.lastname),
                    lastNameState,
                    onValueChange = { lastNameState = it },
                    icon = Icons.Default.Person,
                    maxWidth = true
                )
            }

            ProfileTextField("Email", email, onValueChange = { email = it },icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
            CountrySelector(icon = Icons.Default.LocationOn, selectedCountry = country)

            SettingItem2(Icons.Default.Key, stringResource(R.string.new_password), onClick = {
                navController.navigate(Screens.EditPassword)
            })
            SettingItem3(Icons.Default.PersonRemove, stringResource(R.string.delete_account), onClick = {
                showDeleteDialog.value = true
            })

            Spacer(modifier = Modifier.height(10.dp))
            LoadingBox(isLoading = isLoading)

            // Save button
            Button(
                onClick = {
                    when {
                        selectedProfileImageUri != null -> uploadImage(context, selectedProfileImageUri!!, viewModel)
                        selectedSecondImageUri != null -> uploadSecondImage(context, selectedSecondImageUri!!, viewModel)
                        else -> {
                            viewModel.updateProfile(
                                UpdateProfileData(
                                    user.uid, nameState, lastNameState, email,
                                    profileImageState.value ?: "", secondImageState.value ?: ""
                                )
                            )
                            navController.navigate(Screens.Profile)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Bars)
            ) {
                Text("Uložit", color = Color.White)
            }
        }
    }

    // Delete Account Dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.really_delete_account)) },
            confirmButton = {
                Button(
                    onClick = {
                        logout()
                        viewModel.deleteAccount(user.uid)
                        showDeleteDialog.value = false
                        navController.navigate(Screens.Login)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.confirm), color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(stringResource(R.string.cancel), color = Color.White)
                }
            }
        )
    }
}

// --- Helper Composables and Functions ---

@Composable
private fun Modifier.gradientOverlay(): Modifier = this.drawWithCache {
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

fun uploadImage(context: Context, imageUri: Uri, viewModel: EditProfileViewModel) {
    getFileFromUri(context, imageUri)?.let { file -> viewModel.uploadPhoto(file) }
}

fun uploadSecondImage(context: Context, imageUri: Uri, viewModel: EditProfileViewModel) {
    getFileFromUri(context, imageUri)?.let { file -> viewModel.uploadSecondPhoto(file) }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    cursor?.moveToFirst()
    val filePath = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
    cursor?.close()
    return filePath?.let { File(it) }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    maxWidth: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .height(45.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = if (maxWidth) Modifier.fillMaxWidth() else Modifier.fillMaxWidth(0.37f),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
        }
    }
}

@Composable
fun SettingItem2(
    icon: ImageVector,
    title: String,
    value: String? = null, // Make 'value' optional
    onClick: (() -> Unit)? = null,
    textColor: Color = Color.Black
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // Title Text
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = textColor
        )

        // View Container
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(45.dp)
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Display Value (if provided)
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.weight(1f))

            // Trailing Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun SettingItem3(
    icon: ImageVector,
    title: String,
    value: String? = null, // Make 'value' optional
    onClick: (() -> Unit)? = null,
    textColor: Color = Color.Black
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // Title Text
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = textColor
        )

        // View Container
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(45.dp)
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Red
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Display Value (if provided)
            Text(
                text = title,
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.weight(1f))

            // Trailing Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}


@Composable
fun CountrySelector(
    selectedCountry: MutableState<String>,
    icon: ImageVector
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            stringResource(R.string.country),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .height(45.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))

            var expanded by remember { mutableStateOf(false) }
            val countries = listOf(stringResource(R.string.czech),
                stringResource(R.string.slovakia)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            ) {
                Text(
                    text = selectedCountry.value.ifEmpty { stringResource(R.string.choose_country) },
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colorScheme.onSurface
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                selectedCountry.value = country
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "EditProfile", showBackground = true)
@Composable
private fun PreviewRequestsScreen() {
    EditProfileScreen(
        user = User.empty(),
        navController = rememberNavController(),
        logout = { }
    )
}
