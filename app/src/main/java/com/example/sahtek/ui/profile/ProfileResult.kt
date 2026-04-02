package com.example.sahtek.ui.profile

import com.example.sahtek.ui.home.viewmodel.UpdateProfileResponse

sealed class ProfileResult{
    data class Success(val profile: UpdateProfileResponse): ProfileResult()
    data class Error(val message: String): ProfileResult()
}