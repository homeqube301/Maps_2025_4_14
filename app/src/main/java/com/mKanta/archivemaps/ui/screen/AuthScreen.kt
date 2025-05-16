package com.mKanta.archivemaps.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mKanta.archivemaps.ui.stateholder.AuthViewModel
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
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
                OutlinedTextField(
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.Gray) },
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
                    label = { Text("Password", color = Color.Gray) },
                    enabled = !uiState.isLoading,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { viewModel.signIn(email, password) },
                    enabled = !uiState.isLoading,
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("ログイン")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = { viewModel.signUp(email, password) },
                    enabled = !uiState.isLoading,
                ) {
                    Text("新規登録")
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
