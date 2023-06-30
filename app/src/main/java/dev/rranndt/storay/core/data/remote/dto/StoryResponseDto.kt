package dev.rranndt.storay.core.data.remote.dto


import com.google.gson.annotations.SerializedName

data class StoryResponseDto(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("listStory")
    val listStory: List<StoryResultDto>,
)