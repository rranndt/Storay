package dev.rranndt.storay.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryDetailResponseDto(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("story")
    val listStory: StoryResultDto,
)