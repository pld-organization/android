package com.example.sahtek.network

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.util.Base64
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.sahtek.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.SecureRandom

class GoogleAuthManager(
    private val context: Context
) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun getGoogleIdToken(): String? {
        val activity = context.findActivity()
            ?: throw IllegalStateException("Google sign-in requires an Activity context.")
        val webClientId = activity.getString(R.string.google_web_client_id).trim()

        check(webClientId.isNotEmpty()) {
            "Missing google_web_client_id. Add your OAuth web client ID in res/values/strings.xml."
        }

        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .setNonce(generateSecureRandomNonce())
            .build()

        return try {
            requestGoogleIdToken(activity, signInWithGoogleOption)
        } catch (e: GetCredentialCancellationException) {
            Log.e("GoogleSignIn", "Credential flow was canceled", e)
            throw IllegalStateException(
                "Google sign-in was canceled. If you did not close the Google sheet, verify the OAuth client ID and this app's package/SHA configuration in Google Cloud.",
                e
            )
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "Credential flow could not start", e)
            throw IllegalStateException(
                "Google sign-in could not start. Verify google_web_client_id and the Google Cloud OAuth setup for this app.",
                e
            )
        }
    }

    private suspend fun requestGoogleIdToken(
        activity: Activity,
        option: GetSignInWithGoogleOption
    ): String? {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )

        return extractGoogleIdToken(result.credential)
    }

    private fun extractGoogleIdToken(credential: Credential): String? {
        return if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } else {
            null
        }
    }

    private fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(
            randomBytes,
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
