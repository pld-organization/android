package com.example.sahtek.ui.home.modern

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sahtek.ui.animation.AnimationDurations
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekError
import com.example.sahtek.ui.theme.SahtekSuccess
import com.example.sahtek.ui.theme.SahtekSurface
import com.example.sahtek.ui.theme.SahtekTextPrimary
import com.example.sahtek.ui.theme.SahtekTextSecondary
import com.example.sahtek.ui.theme.SahtekWarning

// ============ ANIMATED TAB BAR ============
@Composable
fun AnimatedTabBar(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                AnimatedTabItem(
                    title = title,
                    isSelected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnimatedTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SahtekBlue.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "Tab Background"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) SahtekBlue else SahtekTextSecondary,
        animationSpec = tween(durationMillis = 300),
        label = "Tab Color"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "Tab Scale"
    )
    
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .graphicsLayer(scaleY = scale)
            .shadow(
                elevation = if (isSelected) 4.dp else 0.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = if (isSelected) 13.sp else 12.sp
            )
        }
    }
}

// ============ PREMIUM STAT CARD ============
@Composable
fun PremiumStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "Card Press"
    )
    
    Card(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SahtekSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(backgroundColor, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )
                }
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = SahtekTextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SahtekTextSecondary
            )
        }
    }
}

// ============ FEATURED SECTION CARD ============
@Composable
fun FeaturedSectionCard(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isNew: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "Featured Card Press"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SahtekBlueLight
        ),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = SahtekBlue.copy(alpha = 0.3f)
            ).brush
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = SahtekTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (isNew) {
                            Surface(
                                color = SahtekSuccess,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "New",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = SahtekTextSecondary
                    )
                }
                
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(40.dp),
                        tint = SahtekBlue.copy(alpha = 0.3f)
                    )
                }
            }
            
            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahtekBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ============ ANIMATED LIST ITEM ============
@Composable
fun AnimatedListItem(
    title: String,
    subtitle: String,
    leadingContent: @Composable () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = delay),
        label = "Item Alpha"
    )
    
    val translateY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 20f,
        animationSpec = tween(durationMillis = 400, delayMillis = delay),
        label = "Item Translate"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = alpha,
                translationY = translateY
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        color = SahtekSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(SahtekBlueLight, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                leadingContent()
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
            }
            
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
