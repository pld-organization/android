package com.example.sahtek.ui.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import com.example.sahtek.ui.theme.*

/**
 * UI State for the Edit Profile Screen
 */
data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val speciality: String = "",
    val establishment: String = "",
    val password: String = "",
    val isDoctor: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBackClick: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSpecialityChange: (String) -> Unit,
    onEstablishmentChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Entrance animation state
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        contentVisible = true
    }

    // Handle success navigation
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SahtekBackground, Color.White)
                )
            )
    ) {
        // Decorative background element
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SahtekBlue.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (uiState.isDoctor) "Professional Profile" else "Account Settings",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SahtekTextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = if (uiState.isDoctor) "Update your practice details" else "Update your personal information",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SahtekTextSecondary,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(42.dp)
                                .shadow(4.dp, CircleShape, spotColor = SahtekShadow)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = SahtekTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.White.copy(alpha = 0.95f)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(800, easing = EaseOutQuivic)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        // Main Form Card with enhanced shadows
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 20.dp,
                                    shape = RoundedCornerShape(32.dp),
                                    ambientColor = SahtekShadow.copy(alpha = 0.4f),
                                    spotColor = SahtekShadow.copy(alpha = 0.8f)
                                ),
                            shape = RoundedCornerShape(32.dp),
                            color = SahtekSurface,
                            border = BorderStroke(1.dp, SahtekBorder.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .animateContentSize(),
                                verticalArrangement = Arrangement.spacedBy(22.dp)
                            ) {
                                // Section Header: Personal Info
                                Text(
                                    text = if (uiState.isDoctor) "MEDICAL INFORMATION" else "PERSONAL INFORMATION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SahtekBlue,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.2.sp
                                    )
                                )

                                // Professional Error Container
                                AnimatedVisibility(
                                    visible = uiState.errorMessage != null,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    uiState.errorMessage?.let { error ->
                                        PremiumErrorContainer(message = error)
                                    }
                                }

                                // Form Fields
                                SahtekPremiumInputField(
                                    label = "Full Name",
                                    value = uiState.fullName,
                                    onValueChange = onFullNameChange,
                                    icon = Icons.Default.Person,
                                    placeholder = "Dr. Ahmed Mansouri"
                                )

                                SahtekPremiumInputField(
                                    label = "Email Address",
                                    value = uiState.email,
                                    onValueChange = {},
                                    icon = Icons.Default.Email,
                                    readOnly = true,
                                    enabled = false,
                                    placeholder = "your.email@example.com",
                                    helperText = "Read only"
                                )

                                SahtekPremiumInputField(
                                    label = "Phone Number",
                                    value = uiState.phoneNumber,
                                    onValueChange = onPhoneNumberChange,
                                    icon = Icons.Default.Phone,
                                    placeholder = "+212 600 000 000",
                                    keyboardType = KeyboardType.Phone
                                )

                                if (uiState.isDoctor) {
                                    SahtekPremiumInputField(
                                        label = "Speciality",
                                        value = uiState.speciality,
                                        onValueChange = onSpecialityChange,
                                        icon = Icons.Default.MedicalServices,
                                        placeholder = "e.g. Cardiologist"
                                    )

                                    SahtekPremiumInputField(
                                        label = "Establishment",
                                        value = uiState.establishment,
                                        onValueChange = onEstablishmentChange,
                                        icon = Icons.Default.LocalHospital,
                                        placeholder = "e.g. Clinic Maamora"
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1.1f)) {
                                            SahtekPremiumInputField(
                                                label = "Birth Date",
                                                value = uiState.dateOfBirth,
                                                onValueChange = onDateOfBirthChange,
                                                icon = Icons.Default.DateRange,
                                                placeholder = "DD/MM/YYYY"
                                            )
                                        }
                                        Box(modifier = Modifier.weight(0.9f)) {
                                            SahtekPremiumInputField(
                                                label = "Gender",
                                                value = uiState.gender,
                                                onValueChange = onGenderChange,
                                                icon = Icons.Default.Wc,
                                                placeholder = "Select"
                                            )
                                        }
                                    }
                                }

                                // Security Section Divider
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = SahtekBorder.copy(alpha = 0.5f)
                                )

                                SahtekPremiumInputField(
                                    label = "New Password",
                                    value = uiState.password,
                                    onValueChange = onPasswordChange,
                                    icon = Icons.Default.Lock,
                                    placeholder = "Leave blank to keep current",
                                    keyboardType = KeyboardType.Password,
                                    isPassword = true
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Save Button
                                SahtekPremiumButton(
                                    text = "Save Changes",
                                    onClick = onSaveClick,
                                    isLoading = uiState.isLoading,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Secondary Discard Action
                        TextButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Discard and return",
                                color = SahtekTextSecondary,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumErrorContainer(message: String) {
    Surface(
        color = SahtekError.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, SahtekError.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = SahtekError,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = message,
                color = SahtekError,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun SahtekPremiumInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    helperText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = SahtekTextPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            )
            if (helperText != null) {
                Text(
                    text = helperText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SahtekTextSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { 
                Text(
                    text = placeholder, 
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SahtekTextSecondary.copy(alpha = 0.4f)
                    )
                ) 
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) SahtekBlue else SahtekTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SahtekBlue,
                unfocusedBorderColor = SahtekBorder,
                disabledBorderColor = SahtekBorder.copy(alpha = 0.3f),
                disabledTextColor = SahtekTextSecondary.copy(alpha = 0.7f),
                focusedContainerColor = SahtekSurface,
                unfocusedContainerColor = SahtekSurface,
                disabledContainerColor = SahtekBackground.copy(alpha = 0.3f),
                cursorColor = SahtekBlue
            ),
            readOnly = readOnly,
            enabled = enabled,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) SahtekTextPrimary else SahtekTextSecondary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun SahtekPremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = SahtekBlue.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SahtekBlue,
            contentColor = Color.White,
            disabledContainerColor = SahtekBlue.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}

// Cubic Easing for smoother animations
val EaseOutQuivic = CubicBezierEasing(0.23f, 1f, 0.32f, 1f)

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPremiumImprovedPreview() {
    SahtekTheme {
        EditProfileScreen(
            uiState = EditProfileUiState(
                isDoctor = true,
                fullName = "Dr. Ahmed Mansouri",
                email = "a.mansouri@sahtek.ma",
                speciality = "Cardiology",
                establishment = "Hopital Cheikh Khalifa"
            ),
            onBackClick = {},
            onFullNameChange = {},
            onPhoneNumberChange = {},
            onDateOfBirthChange = {},
            onGenderChange = {},
            onSpecialityChange = {},
            onEstablishmentChange = {},
            onPasswordChange = {},
            onSaveClick = {}
        )
    }
}
