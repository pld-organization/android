package com.example.sahtek.ui.notification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingNotificationState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF3B7FE5),
            modifier = Modifier.height(48.dp)
        )
    }
}

@Composable
fun ErrorNotificationState(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error Loading Notifications",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = errorMessage,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF9CA3AF)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        androidx.compose.material3.Button(
            onClick = onRetry,
            modifier = Modifier.height(40.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B7FE5)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Retry",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
