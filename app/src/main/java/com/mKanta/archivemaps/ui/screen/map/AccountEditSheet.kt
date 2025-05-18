package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme

@Composable
fun AccountEditSheet(
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    accountName: String,
    accountId: String,
    onAccountNameChange: (String) -> Unit,
    isLoading: Boolean = false,
) {
    ArchivemapsTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.account_edit_title),
                // style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier =
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
            )

            Text(
                stringResource(R.string.account_id_label),
                color = Color.Gray,
                // modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                text = accountId,
                color = Color.Gray,
                // modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = accountName,
                onValueChange = onAccountNameChange,
                label = { Text(stringResource(R.string.account_name_label), color = Color.Gray) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                enabled = !isLoading,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
            )

            Button(
                onClick = onSignOut,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                enabled = !isLoading,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(stringResource(R.string.sign_out))
            }

            Button(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
            ) {
                Text(stringResource(R.string.delete_account))
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun AccountEditSheetPreview(
    onDismiss: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    accountName: String = "test",
    accountId: String = "test",
) {
    AccountEditSheet(
        onDismiss = onDismiss,
        onSignOut = onSignOut,
        onDeleteAccount = onDeleteAccount,
        accountName = accountName,
        accountId = accountId,
        onAccountNameChange = {},
        isLoading = false,
    )
}
