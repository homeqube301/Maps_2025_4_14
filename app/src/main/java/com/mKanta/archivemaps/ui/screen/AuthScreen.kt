package com.mKanta.archivemaps.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.ui.state.AccountLoadingState
import com.mKanta.archivemaps.ui.state.AuthUiState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    uiState: AuthUiState,
    signIn: (String, String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    val isLoading =
        when (uiState.isLoading) {
            is AccountLoadingState.Loading -> true
            else -> false
        }

    val errorMessage =
        when (uiState.isLoading) {
            is AccountLoadingState.Error -> uiState.isLoading.message
            else -> uiState.error
        }

    ArchivemapsTheme {
        Column(
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Map Marker",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally),
                )

//                Image(
//                    painter = painterResource(id = R.drawable.ic_launcher_round),
//                    contentDescription = "App Icon",
//                    modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally),
//                )

                Spacer(modifier = Modifier.height(64.dp))

                Text(
                    text = stringResource(id = R.string.auth_AppTitle),
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 32.sp,
                )
                Text(
                    text = stringResource(id = R.string.auth_AppDisc) + "\n" + stringResource(id = R.string.auth_AppDisc2),
                    color = Color.White,
                    fontSize = 16.sp,
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.auth_email),
                            color = Color.Gray,
                        )
                    },
                    enabled = !isLoading,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = stringResource(id = R.string.auth_password),
                            color = Color.Gray,
                        )
                    },
                    enabled = !isLoading,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { signIn(email, password) },
                    enabled = !isLoading,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                        ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.auth_login),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onNavigateToSignUp,
                    enabled = !isLoading,
                ) {
                    Text(
                        text = stringResource(id = R.string.auth_signUp),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(
        onLoginSuccess = {},
        onNavigateToSignUp = {},
        uiState = AuthUiState(),
        signIn = { _, _ -> },
    )
}
