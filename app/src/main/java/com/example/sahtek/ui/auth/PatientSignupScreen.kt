package com.example.sahtek.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sahtek.R
import com.example.sahtek.network.GoogleAuthManager as NetworkGoogleAuthManager
import com.example.sahtek.ui.components.SahtekBrandHeader
import com.example.sahtek.ui.components.SahtekGoogleButton
import com.example.sahtek.ui.components.SahtekPanel
import com.example.sahtek.ui.components.SahtekPrimaryButton
import com.example.sahtek.ui.components.SahtekScreenBackground
import com.example.sahtek.ui.components.SahtekSectionHeader
import com.example.sahtek.ui.components.SahtekSelectorField
import com.example.sahtek.ui.components.SahtekStatusMessage
import com.example.sahtek.ui.components.SahtekTextField
import com.example.sahtek.ui.components.SahtekTextLink
import com.example.sahtek.ui.theme.SahtekTheme
import kotlinx.coroutines.launch

@Composable
fun PatientSignupScreen(
    modifier: Modifier = Modifier,
    onCreateAccountClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    @DrawableRes brandHeaderRes: Int = R.drawable.ic_logo,
    @DrawableRes googleIconRes: Int = R.drawable.img,
    viewModel: PatientSignupViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val googleAuthManager = remember(context) { NetworkGoogleAuthManager(context) }
    var isBloodTypeMenuExpanded by remember { mutableStateOf(false) }
    var isGenderMenuExpanded by remember { mutableStateOf(false) }
    var isGoogleSignInInProgress by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val errorMessage = uiState.errorMessage ?: authUiState.errorMessage
    val successMessage = authUiState.successMessage

    LaunchedEffect(uiState.isSignupSuccess) {
        if (uiState.isSignupSuccess) {
            onCreateAccountClick()
            viewModel.resetSignupState()
        }
    }

    LaunchedEffect(authUiState.isLoginSuccess) {
        if (authUiState.isLoginSuccess) {
            onGoogleClick()
            authViewModel.resetLoginState()
        }
    }

    LaunchedEffect(authUiState.successMessage) {
        authUiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    SahtekScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .widthIn(max = 420.dp)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SahtekBrandHeader(
                brandHeaderRes = brandHeaderRes,
                eyebrow = "Patient onboarding",
                title = "Create your patient account",
                subtitle = "Secure your medical records, upload analyses, and book appointments with confidence."
            )

            SahtekPanel {
                SahtekSectionHeader(
                    title = "Personal information",
                    subtitle = "Use your real details so your records and appointments stay accurate."
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SahtekTextField(
                        label = "First name",
                        value = uiState.firstName,
                        onValueChange = viewModel::onFirstNameChange,
                        placeholder = "Yasmine",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )
                    SahtekTextField(
                        label = "Last name",
                        value = uiState.lastName,
                        onValueChange = viewModel::onLastNameChange,
                        placeholder = "Benali",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                SahtekTextField(
                    label = "Phone number",
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    placeholder = "5XX XX XX XX",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Next
                    ),
                    prefix = {
                        Text(
                            text = "+213 ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )

                Box {
                    SahtekSelectorField(
                        label = "Blood type",
                        value = uiState.bloodType,
                        placeholder = "Choose your blood type",
                        onClick = { isBloodTypeMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isBloodTypeMenuExpanded,
                        onDismissRequest = { isBloodTypeMenuExpanded = false }
                    ) {
                        listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").forEach { bloodType ->
                            DropdownMenuItem(
                                text = { Text(text = bloodType) },
                                onClick = {
                                    viewModel.onBloodTypeChange(bloodType)
                                    isBloodTypeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Box {
                    SahtekSelectorField(
                        label = "Gender",
                        value = uiState.selectedGender,
                        placeholder = "Choose your gender",
                        onClick = { isGenderMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isGenderMenuExpanded,
                        onDismissRequest = { isGenderMenuExpanded = false }
                    ) {
                        listOf("MALE", "FEMALE").forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(text = gender.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.onGenderChange(gender)
                                    isGenderMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            SahtekPanel {
                SahtekSectionHeader(
                    title = "Secure sign in",
                    subtitle = "These credentials will be used for your future access to Sahtek Online."
                )

                SahtekTextField(
                    label = "Email",
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "name@example.com",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Next
                    )
                )

                SahtekTextField(
                    label = "Password",
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = "Create a strong password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Done
                    )
                )

                errorMessage?.let { message ->
                    SahtekStatusMessage(
                        message = message,
                        isError = true
                    )
                }

                successMessage?.takeIf { errorMessage == null }?.let { message ->
                    SahtekStatusMessage(
                        message = message,
                        isError = false
                    )
                }

                SahtekPrimaryButton(
                    text = if (uiState.isLoading) "Creating account..." else "Create Account",
                    onClick = { viewModel.signup() },
                    enabled = !uiState.isLoading && !authUiState.isLoading && !isGoogleSignInInProgress
                )

                Text(
                    text = "Or continue with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SahtekGoogleButton(
                    googleIconRes = googleIconRes,
                    text = if (authUiState.isLoading || isGoogleSignInInProgress) {
                        "Connecting..."
                    } else {
                        "Continue with Google"
                    },
                    enabled = !uiState.isLoading && !authUiState.isLoading && !isGoogleSignInInProgress,
                    onClick = {
                        coroutineScope.launch {
                            isGoogleSignInInProgress = true
                            try {
                                val idToken = googleAuthManager.getGoogleIdToken()
                                if (idToken != null) {
                                    authViewModel.loginWithGoogle(
                                        idToken = idToken,
                                        role = "PATIENT"
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Google ID token not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Log.e("GoogleSignIn", "Google sign-in failed in PatientSignupScreen", e)
                                Toast.makeText(
                                    context,
                                    e.message ?: "Google sign-in failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isGoogleSignInInProgress = false
                            }
                        }
                    }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SahtekTextLink(
                        text = "Log In",
                        onClick = onLoginClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PatientSignupScreenPreview() {
    SahtekTheme {
        Surface {
            PatientSignupScreen()
        }
    }
}
