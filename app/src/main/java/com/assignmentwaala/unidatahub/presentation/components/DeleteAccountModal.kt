package com.assignmentwaala.unidatahub.presentation.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.ui.theme.primaryBlue

@Composable
fun DeleteAccountModal( showDialog: Boolean,
                        onDismiss: () -> Unit,
                        onAccountDelete: (String) -> Unit
) {

    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }


    var isCurrentPasswordVisible by remember { mutableStateOf(false) }

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Top Image
//                    Image(
//                        painter = painterResource(id = R.drawable.unidatahub), // Replace with your drawable
//                        contentDescription = "Password Reset",
//                        modifier = Modifier
//                            .size(120.dp)
//                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Delete Your Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter your password to confirm account deletion",
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Gray),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Password") },
                        visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                                Icon(
                                    imageVector = if (isCurrentPasswordVisible) ImageVector.vectorResource(
                                        R.drawable.ic_visibility) else ImageVector.vectorResource(R.drawable.ic_visibility_off),
                                    contentDescription = "Toggle Password Visibility",
                                    tint = primaryBlue
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reset Password Button
                    Button(
                        onClick = {
                            onAccountDelete(currentPassword)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),

                        ) {
                        Text(text = "Delete Account", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cancel Button
                    TextButton(onClick = { onDismiss() }) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}