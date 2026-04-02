package com.example.sahtek.data.auth.model


sealed class AuthResult {
    class Success(val token: String, val role: String?=null) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
