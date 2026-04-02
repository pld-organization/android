package com.example.sahtek.data.auth.model


data class AuthResponse(
    val message: String? = null,
    val token: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val role: String? = null
)
