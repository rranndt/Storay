package dev.rranndt.storay.core.domain.model

import java.io.File

data class AddStoryRequest(
    val image: File,
    val description: String,
)
