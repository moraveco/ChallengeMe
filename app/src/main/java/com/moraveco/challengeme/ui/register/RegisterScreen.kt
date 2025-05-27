package com.moraveco.challengeme.ui.register

//noinspection SuspiciousImport
import android.R
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.login.OrDivider
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.ui.theme.Background
import java.util.UUID

@Composable
fun RegisterScreen(navController: NavController, registerViewModel: RegisterViewModel = hiltViewModel()){
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val isLoading by registerViewModel.isLoading.collectAsState()
    val success by registerViewModel.succeed.collectAsState()
    val error by registerViewModel.error.collectAsState()

    var name by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var email by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var password by remember {
        mutableStateOf(TextFieldValue(""))
    }
    val uid = UUID.randomUUID().toString()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LoadingBox(isLoading = isLoading)
        FirstHalfRegister(modifier = Modifier.padding(top = 20.dp)){ navController.navigate(Screens.Login) }
        OrDivider()
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(
                    text = "Name",
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    text = "Email",
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            label = {
                Text(
                    text = "Password",
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {registerViewModel.registerUser(
            registerData = RegisterData(
                uid = uid,
                email = email.text,
                password = md5(password.text)
            ),
            user = User(
                uid = uid,
                name = name.text,
                lastName = name.text,
                bio = "",
                profileImageUrl = "",
                secondImageUrl = "",
                token = "",
                country = "",
                email = email.text



            )
        )}, colors = ButtonDefaults.buttonColors(containerColor = Background), modifier = Modifier.fillMaxWidth(0.7f)) {
            Text(text = "Login")
        }

    }
    LaunchedEffect(
        key1 = success,
        key2 = error,
        block = {
            if (success) {
               // context.startActivity(Intent(context, HomeActivity::class.java))
            }

            if (error) {
                Toast.makeText(context, "Registrace neúspěšná", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Preview(name = "RegisterScreen")
@Composable
private fun PreviewRegisterScreen() {
    RegisterScreen(rememberNavController())
}

@Composable
fun FirstHalfRegister(modifier: Modifier = Modifier, navigate: () -> Unit) {
    Column(modifier = modifier
        .fillMaxHeight(0.4f)
        .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.ic_menu_gallery), contentDescription = null, modifier = Modifier
            .size(80.dp)
            .clip(
                CircleShape
            ))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Register in ChatMe",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Already registered?", fontSize = 15.sp, color = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "Sign in", color = Color.White, fontSize = 15.sp, modifier = Modifier.clickable { navigate() })
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}