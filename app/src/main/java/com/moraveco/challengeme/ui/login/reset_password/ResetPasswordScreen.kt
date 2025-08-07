package com.moraveco.challengeme.ui.login.reset_password

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moraveco.challengeme.data.SendPasswordData
import com.moraveco.challengeme.ui.login.LoginViewModel


@Composable
fun ResetPasswordScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val email = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Back")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(com.moraveco.challengeme.R.string.back))
        }

        // Logo
        Image(
            painter = painterResource(id = R.drawable.gallery_thumb),
            contentDescription = "Logo",
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Instruction Text
        Text(
            text = stringResource(com.moraveco.challengeme.R.string.enter_email),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Email Input Field
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Message Display
        if (message.value.isNotEmpty()) {
            Text(
                text = message.value,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red // Optional: Display in red for error or feedback
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Send Button
        Button(
            onClick = {
                if (email.value.isNotBlank()) {
                    loginViewModel.resetPassword(SendPasswordData(email = email.value))
                    message.value =
                        context.getString(com.moraveco.challengeme.R.string.password_sent)
                } else {
                    message.value = context.getString(com.moraveco.challengeme.R.string.valid_email)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Send", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Send Icon",
                    tint = Color.White
                )
            }
        }
    }
}


@Preview(name = "ResetPasswordScreen")
@Composable
private fun PreviewResetPasswordScreen() {
    ResetPasswordScreen(rememberNavController())
}