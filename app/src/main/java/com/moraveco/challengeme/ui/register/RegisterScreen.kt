package com.moraveco.challengeme.ui.register

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moraveco.challengeme.R
import com.moraveco.challengeme.data.RegisterData
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.home.LoadingBox
import com.moraveco.challengeme.ui.login.OrDivider
import com.moraveco.challengeme.ui.login.md5
import com.moraveco.challengeme.ui.theme.Background
import java.util.UUID

@Composable
fun RegisterScreen(navController: NavController) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    var email by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var password by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var checked by remember {
        mutableStateOf(false)
    }
    var showMessage by remember {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        FirstHalfRegister { navController.navigate(Screens.Login) }
        OrDivider()
        Spacer(modifier = Modifier.height(20.dp))
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
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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
                val description = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = {checked = !checked})
                Text(text = "Souhlasím s ", color = Color.White)
                Text(text = "obchodními podmínky ", color = Color(8, 131, 255), modifier = Modifier.clickable {uriHandler.openUri("https://www.challengeme.com/blog/privacy-policy-challengeme.html")})

            }
            Row {
                Text(text = "& ", color = Color.White, modifier = Modifier.padding(start = 20.dp))
                Text(text = "EULA", color = Color(8, 131, 255))
            }

        }


        Spacer(modifier = Modifier.height(10.dp))

        if (showMessage){
            Text(text = "Zadejte všechny data", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (email.text.isNotBlank() && password.text.isNotBlank() && isValidEmail(email.text) && password.text.length > 7 && checked){
                    navController.navigate(
                        Screens.SecondRegister(
                            email.text,
                            md5(password.text)
                        )
                    )
                }else{
                    showMessage = true
                }

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(8, 131, 255)),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text(text = "Pokračovat dále")
        }

    }

}

@Preview(name = "RegisterScreen")
@Composable
private fun PreviewRegisterScreen() {
    RegisterScreen(rememberNavController())
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return email.matches(emailRegex)
}


@Composable
fun FirstHalfRegister(modifier: Modifier = Modifier, navigate: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.challengeme),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(
                    RoundedCornerShape(20.dp)
                )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Registrace do ChallengeMe",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Máte již účet?", fontSize = 15.sp, color = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "Přihlásit se",
                color = Color(8, 131, 255),
                fontSize = 15.sp,
                modifier = Modifier.clickable { navigate() })
        }

    }
}