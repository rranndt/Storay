package dev.rranndt.storay.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SignInResponseDto(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("loginResult")
    val signInResultDto: SignInResultDto,

    @SerializedName("message")
    val message: String,
)

data class SignInResultDto(

    @SerializedName("name")
    val name: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("userId")
    val userId: String,
)
