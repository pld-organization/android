package com.example.sahtek.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sahtek.ui.animation.AnimationDurations
import com.example.sahtek.ui.animation.AnimationSpecs
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekError
import com.example.sahtek.ui.theme.SahtekBackground
import com.example.sahtek.ui.theme.SahtekSurface
import com.example.sahtek.ui.theme.SahtekSuccess
import com.example.sahtek.ui.theme.SahtekSurfaceAlt
import com.example.sahtek.ui.theme.SahtekTextPrimary
import com.example.sahtek.ui.theme.SahtekTextSecondary
import com.example.sahtek.ui.theme.SahtekWarning

// ============ PREMIUM BUTTON ============
@Composable
fun PremiumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.LARGE
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "Button Scale"
    )
    
    val backgroundColor = when (variant) {
        ButtonVariant.PRIMARY -> SahtekBlue
        ButtonVariant.SECONDARY -> SahtekSurfaceAlt
        ButtonVariant.DANGER -> SahtekError
        ButtonVariant.SUCCESS -> SahtekSuccess
    }
    
    val textColor = when (variant) {
        ButtonVariant.PRIMARY -> Color.White
        ButtonVariant.SECONDARY -> SahtekTextPrimary
        ButtonVariant.DANGER -> Color.White
        ButtonVariant.SUCCESS -> Color.White
    }
    
    val buttonPadding = when (size) {
        ButtonSize.SMALL -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ButtonSize.LARGE -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    }
    
    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = buttonPadding,
        interactionSource = interactionSource
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = textColor
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

enum class ButtonVariant {
    PRIMARY, SECONDARY, DANGER, SUCCESS
}

enum class ButtonSize {
    SMALL, LARGE
}

// ============ PREMIUM CARD ============
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    elevation: Float = 8f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(),
        label = "Card Scale"
    )
    
    val cardElevation by animateFloatAsState(
        targetValue = if (isPressed) 4f else elevation,
        animationSpec = spring(),
        label = "Card Elevation"
    )
    
    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(elevation = cardElevation.dp, shape = RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SahtekSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation.dp)
    ) {
        content()
    }
}

// ============ PREMIUM INPUT FIELD ============
@Composable
fun PremiumInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> SahtekError
            isFocused -> SahtekBlue
            else -> Color(0xFFE2E8F0)
        },
        animationSpec = tween(durationMillis = 200),
        label = "Border Color"
    )
    
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = SahtekTextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) 8.dp else 0.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            placeholder = {
                Text(
                    placeholder,
                    color = SahtekTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = isError
        )
        
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = SahtekError,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// ============ STATUS BADGE ============
@Composable
fun StatusBadge(
    status: StatusType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        StatusType.SUCCESS -> SahtekSuccess.copy(alpha = 0.1f)
        StatusType.WARNING -> SahtekWarning.copy(alpha = 0.1f)
        StatusType.ERROR -> SahtekError.copy(alpha = 0.1f)
        StatusType.INFO -> SahtekBlue.copy(alpha = 0.1f)
    }
    
    val textColor = when (status) {
        StatusType.SUCCESS -> SahtekSuccess
        StatusType.WARNING -> SahtekWarning
        StatusType.ERROR -> SahtekError
        StatusType.INFO -> SahtekBlue
    }
    
    val icon = when (status) {
        StatusType.SUCCESS -> Icons.Default.CheckCircle
        StatusType.ERROR -> Icons.Default.Error
        StatusType.WARNING -> Icons.Default.Error
        StatusType.INFO -> Icons.Default.Info
    }
    
    Row(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.name,
            modifier = Modifier.size(16.dp),
            tint = textColor
        )
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

enum class StatusType(val displayName: String) {
    SUCCESS("Confirmed"),
    WARNING("Pending"),
    ERROR("Cancelled"),
    INFO("Scheduled")
}

// ============ ANIMATED LOADING INDICATOR ============
@Composable
fun PremiumLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = SahtekBlue,
            strokeWidth = 4.dp
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SahtekTextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============ ANIMATED EMPTY STATE ============
@Composable
fun PremiumEmptyState(
    title: String,
    message: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(SahtekSurfaceAlt, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📭",
                fontSize = 40.sp
            )
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = SahtekTextPrimary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SahtekTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ============ ANIMATED SUCCESS DIALOG ============
@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(true) }
    
    if (showDialog) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.32f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SahtekSurface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                SahtekSuccess.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            modifier = Modifier.size(40.dp),
                            tint = SahtekSuccess
                        )
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SahtekTextPrimary
                    )
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SahtekTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    PremiumButton(
                        text = "Done",
                        onClick = {
                            showDialog = false
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
