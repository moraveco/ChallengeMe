package com.moraveco.challengeme.ui.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moraveco.challengeme.R
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()){
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val uiState = loginViewModel.uiState
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LoadingBox(isLoading = uiState.isAuthenticating)
        FirstHalf(modifier = Modifier.padding(top = 20.dp)) { navController.navigate(Screens.Register) }
        OrDivider()
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { loginViewModel.updateEmail(it) },
            label = {
                Text(
                    text = "Email",
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null)},
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { loginViewModel.updatePassword(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
            label = {
                Text(
                    text = stringResource(R.string.password),
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(
                    R.string.show_password
                )

                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description)
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = stringResource(R.string.forgot_password), fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = stringResource(R.string.click_here), color = Color(8, 131, 255), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {loginViewModel.loginUser2()}, colors = ButtonDefaults.buttonColors(containerColor = Color(8, 131, 255)), modifier = Modifier.fillMaxWidth(0.7f)) {
            Text(text = stringResource(R.string.sign_in), color = Color.White)
        }

    }
    LaunchedEffect(
        key1 = uiState.authenticationSucceed,
        key2 = uiState.authErrorMessage,
        block = {
            if (uiState.authenticationSucceed) {
                //context.startActivity(Intent(context, HomeActivity::class.java))
                navController.navigate(Screens.Home)
            }

            if (uiState.authErrorMessage != null) {
                Log.v("LoginScreen", uiState.authErrorMessage.toString())
                Toast.makeText(context, uiState.authErrorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Preview(name = "LoginScreen", showBackground = true)
@Composable
private fun PreviewLoginScreen() {
    LoginScreen(rememberNavController())
}


@Composable
fun FirstHalf(modifier: Modifier = Modifier, navigate: () -> Unit) {
    Column(modifier = modifier
        .fillMaxHeight(0.4f)
        .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.challengeme), contentDescription = null, modifier = Modifier
            .size(80.dp)
            .clip(
                RoundedCornerShape(20.dp)
            ))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.sign_in_challenge),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = stringResource(R.string.no_account), fontSize = 15.sp,             color = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = stringResource(R.string.register), color = Color(8, 131, 255), fontSize = 15.sp, modifier = Modifier.clickable { navigate() })
        }
    }
}

@Composable
fun OrDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f) ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.or),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            modifier = Modifier.weight(1f)
        )
    }
}

