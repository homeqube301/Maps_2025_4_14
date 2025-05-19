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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.ui.stateholder.AuthUiState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    uiState: AuthUiState,
    signUp: (String, String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onSignUpSuccess()
        }
    }

    ArchivemapsTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.auth_signUp),
                            color = Color.White,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.tertiary),
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "戻る",
                                tint = Color.White,
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Map Marker",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier
                                .size(80.dp)
                                .align(Alignment.CenterHorizontally),
                    )

                    Spacer(modifier = Modifier.height(64.dp))

                    Text(
                        text = stringResource(id = R.string.auth_signUpTitle),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                    )
                    Text(
                        text =
                            stringResource(id = R.string.auth_signUpDisc) + "\n" +
                                stringResource(
                                    id = R.string.auth_signUpDisc2,
                                ),
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
                        enabled = !uiState.isLoading,
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
                        enabled = !uiState.isLoading,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = {
                            Text(
                                text = stringResource(id = R.string.auth_signUp_Password),
                                color = Color.Gray,
                            )
                        },
                        enabled = !uiState.isLoading,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            if (password == confirmPassword) {
                                signUp(email, password)
                            }
                        },
                        enabled = !uiState.isLoading && password == confirmPassword,
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.auth_signUp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }

                    uiState.error?.let {
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
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        onNavigateBack = {},
        onSignUpSuccess = {},
        uiState = AuthUiState(),
        signUp = { _, _ -> },
    )
}
