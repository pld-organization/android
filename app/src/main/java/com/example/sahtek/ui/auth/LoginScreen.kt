package com.example.sahtek.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
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
import com.example.sahtek.ui.components.SahtekStatusMessage
import com.example.sahtek.ui.components.SahtekTextField
import com.example.sahtek.ui.components.SahtekTextLink
import com.example.sahtek.ui.theme.SahtekTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    role: String = "PATIENT",
    @DrawableRes brandHeaderRes: Int = R.drawable.ic_logo,
    @DrawableRes googleIconRes: Int = R.drawable.img,
    viewModel: AuthViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val googleAuthManager = remember(context) { NetworkGoogleAuthManager(context) }
    var isGoogleSignInInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginClick(
                uiState.currentUserRole
                    .ifBlank { role }
                    .trim()
                    .uppercase()
            )
            viewModel.resetLoginState()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
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
                eyebrow = if (role == "DOCTOR") "Doctor access" else "Patient access",
                title = "Welcome back",
                subtitle = "Securely sign in to access your Sahtek Online workspace, appointments, and medical records."
            )

            SahtekPanel {
                SahtekSectionHeader(
                    title = "Account access",
                    subtitle = "Your session stays encrypted and protected across devices."
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
                    placeholder = "Enter your password",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Done
                    )
                )

                Text(
                    text = "Protected medical access",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                uiState.errorMessage?.let { message ->
                    SahtekStatusMessage(
                        message = message,
                        isError = true
                    )
                }

                uiState.successMessage?.takeIf { uiState.errorMessage == null }?.let { message ->
                    SahtekStatusMessage(
                        message = message,
                        isError = false
                    )
                }

                SahtekPrimaryButton(
                    text = if (uiState.isLoading) "Signing in..." else "Log In",
                    onClick = { viewModel.login(role) },
                    enabled = !uiState.isLoading && !isGoogleSignInInProgress
                )
            }

            SahtekPanel {
                Text(
                    text = "Or continue with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SahtekGoogleButton(
                    googleIconRes = googleIconRes,
                    text = if (uiState.isLoading || isGoogleSignInInProgress) {
                        "Connecting..."
                    } else {
                        "Continue with Google"
                    },
                    enabled = !uiState.isLoading && !isGoogleSignInInProgress,
                    onClick = {
                        coroutineScope.launch {
                            isGoogleSignInInProgress = true
                            try {
                                val idToken = googleAuthManager.getGoogleIdToken()
                                if (idToken != null) {
                                    viewModel.loginWithGoogle(
                                        idToken = idToken,
                                        role = role
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Google ID token not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Log.e("GoogleSignIn", "Google sign-in failed in LoginScreen", e)
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
                        text = "Need a new account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SahtekTextLink(
                        text = "Sign Up",
                        onClick = onSignUpClick
                    )
                }

                Text(
                    text = "By continuing, you agree to the Terms of Service and Privacy Policy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    SahtekTheme {
        Surface {
            LoginScreen()
        }
    }
}
