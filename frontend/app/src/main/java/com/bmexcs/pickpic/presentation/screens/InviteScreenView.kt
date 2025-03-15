package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.presentation.viewmodels.InviteViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import java.util.regex.Pattern

@Composable
fun InviteScreenView(
    navController: NavHostController,
    eventId: String // Pass only the eventId
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Existing email input and list UI
        EditableEmailField(eventId = eventId)

        Spacer(modifier = Modifier.height(16.dp))

        // Add a button to navigate to the QR screen
        Button(
            onClick = {
                navController.navigate("qrInviteView/$eventId")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
    }
}

@Composable
fun EditableEmailField(
    viewModel: InviteViewModel = hiltViewModel(),
    eventId: String
) {
    var email by remember { mutableStateOf("") }
    val emailList by viewModel.emailList.collectAsState()
    val showConfirmButton = emailList.isNotEmpty()
    var isError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Enter Friends Email",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = email, // Use 'email'
                onValueChange = { newText ->
                    email = newText
                    isError = !isValidEmail(newText)
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = if (isError) Color.Red else Color.LightGray,
                    errorSupportingTextColor = if (isError) Color.Red else Color.Gray,
                ),
                singleLine = true,
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text("Invalid email format", color = Color.Red)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (email.isNotBlank() && isValidEmail(email)) { // Use 'email'
                        viewModel.addEmail(email.trim()) // Use 'email' and trim
                        email = "" // Clear the input field
                        isError = false //reset error
                    } else {
                        isError = true // Show error message
                    }
                },
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            emailList.forEach { emailItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9C4EC), shape = RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = emailItem, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
                    Text(
                        text = "X",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                viewModel.removeEmail(emailItem)
                            }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (showConfirmButton) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.confirmInvites(emailList, eventId)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Invite")
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    val pattern = Pattern.compile(emailRegex)
    return pattern.matcher(email).matches()
}
