package com.example.sahtek.ui.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sahtek.R
import com.example.sahtek.ui.components.SahtekBrandHeader
import com.example.sahtek.ui.components.SahtekPanel
import com.example.sahtek.ui.components.SahtekPrimaryButton
import com.example.sahtek.ui.components.SahtekRoleCard
import com.example.sahtek.ui.components.SahtekScreenBackground
import com.example.sahtek.ui.components.SahtekSectionHeader
import com.example.sahtek.ui.theme.SahtekTheme

@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    onPatientClick: () -> Unit = {},
    onDoctorClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    @DrawableRes brandHeaderRes: Int = R.drawable.ic_logo,
    @DrawableRes patientImageRes: Int = R.drawable.img_1,
    @DrawableRes doctorImageRes: Int = R.drawable.img1_doctor,
) {
    val scrollState = rememberScrollState()

    SahtekScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SahtekBrandHeader(
                brandHeaderRes = brandHeaderRes,
                eyebrow = "Sahtek Online",
                title = "Choose your care experience",
                subtitle = "Continue as a patient to manage your health, or as a doctor to review cases and appointments."
            )

            SahtekPanel {
                SahtekSectionHeader(
                    title = "Who are you today?",
                    subtitle = "Each workspace is tailored to the tools and information you need most."
                )
                SahtekRoleCard(
                    title = "Patient",
                    subtitle = "Upload analyses, track AI insights, and manage your appointments in one secure place.",
                    imageRes = patientImageRes,
                    onClick = onPatientClick
                )
                SahtekRoleCard(
                    title = "Doctor",
                    subtitle = "Review consultations, manage schedules, and follow patient cases with a professional workflow.",
                    imageRes = doctorImageRes,
                    onClick = onDoctorClick
                )
            }

            SahtekPanel {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SahtekPrimaryButton(
                    text = "Log In",
                    onClick = onLoginClick
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RoleSelectionScreenPreview() {
    SahtekTheme {
        Surface {
            RoleSelectionScreen()
        }
    }
}
