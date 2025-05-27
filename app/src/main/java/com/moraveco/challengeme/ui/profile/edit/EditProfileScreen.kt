package com.moraveco.challengeme.ui.profile.edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.net.toFile
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
import com.moraveco.challengeme.ui.profile.MenuScreen
import com.moraveco.challengeme.ui.profile.edit.EditProfileViewModel
import com.moraveco.challengeme.ui.theme.Bars
import java.io.ByteArrayOutputStream
import java.io.File


@Composable
fun EditProfileScreen(
    user: User,
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel(),
    logout: () -> Unit
) {
    val nameState = remember { mutableStateOf(user.name) }
    val lastNameState = remember { mutableStateOf(user.lastName) }
    val email = remember { mutableStateOf(user.email) }
    val profileImageState = remember { mutableStateOf(user.profileImageUrl) }
    val country = remember { mutableStateOf(user.country) }
    val uploadResponse by viewModel.uploadResponse.observeAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            getFileFromUri(
                context,
                selectedImageUri ?: Uri.EMPTY
            )?.let { viewModel.uploadPhoto(it) }
        }
    }
    val showDeleteDialog = remember { mutableStateOf(false) }
    // Update profileImageState when uploadResponse is available
    LaunchedEffect(uploadResponse) {
        uploadResponse?.let { responses ->
            responses.file_path.let { url ->
                profileImageState.value = url // Set the updated profile image URL
            }
        }


    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)) {

            Image(
                painter = painterResource(R.drawable.profile_background),
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

                    }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.TopStart)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    stringResource(id = R.string.back),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f))
                    .align(Alignment.BottomCenter)
                    .clickable { imagePickerLauncher.launch("image/*") }
            ) {
                if (false) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    if (profileImageState.value?.isEmpty() == true) {
                        Image(
                            painter = painterResource(id = R.drawable.heart_solid),
                            contentDescription = "Default Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = profileImageState.value,
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
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


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Image Picker

            Spacer(modifier = Modifier.height(20.dp))

            // Editable fields
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileTextField(
                    label = "Jméno",
                    value = nameState,
                    maxWidth = false,
                    icon = Icons.Default.Person
                )
                Spacer(Modifier.width(15.dp))
                ProfileTextField(
                    label = "Příjmení",
                    value = lastNameState,
                    maxWidth = true,
                    icon = Icons.Default.Person
                )
            }

            ProfileTextField(
                label = "Email",
                value = email,
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            CountrySelector(icon = Icons.Default.LocationOn, selectedCountry = country)
            SettingItem2(
                icon = Icons.Default.Key,
                title = "Nastavit nové heslo",
                onClick = { navController.navigate("") })
            SettingItem3(
                icon = Icons.Default.PersonRemove,
                title = "Odstranit účet",
                onClick = { showDeleteDialog.value = true })
            Spacer(modifier = Modifier.height(10.dp))
            LoadingBox(isLoading = isLoading)
            uploadResponse?.let { result ->
                Log.v("upload", result.message)
                if (result.success) {
                    viewModel.updateProfile(
                        UpdateProfileData(
                            user.uid,
                            user.name,
                            user.lastName,
                            user.email,
                            BASE_URL + result.file_path.substring(1)
                        )

                    )

                } else {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }


            }
            // Save Button
            Button(
                onClick = {
                    // Update profile with new data

                    viewModel.updateProfile(
                        UpdateProfileData(
                            uid = user.uid,
                            name = nameState.value,
                            lastName = lastNameState.value,
                            email = email.value,
                            profileImageUrl = profileImageState.value ?: "",
                        )
                    )
                    navController.popBackStack()
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
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Odstranit účet") },
            text = { Text("Opravdu chcete odstranit účet?") },
            confirmButton = {
                Button(
                    onClick = {
                        //viewModel.deleteAccount(DeleteData(uid = uid))
                        logout()
                        showDeleteDialog.value = false
                        navController.navigate(Screens.Login)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Red confirm button
                ) {
                    Text("Potvrdit", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray) // Gray cancel button
                ) {
                    Text("Zrušit", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: MutableState<String>,
    icon: ImageVector,
    maxWidth: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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
                value = value.value,
                onValueChange = { value.value = it },
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
            "Země",
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
            val countries = listOf("Česká republika", "Slovensko")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            ) {
                Text(
                    text = selectedCountry.value.ifEmpty { "Vyberte zemi" },
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

fun getFileFromUri(context: Context, uri: Uri): File? {
    val cursor =
        context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    cursor?.moveToFirst()
    val filePath = cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
    cursor?.close()
    return filePath?.let { File(it) }
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