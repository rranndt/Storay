package dev.rranndt.storay.core.domain.model

import com.google.android.gms.maps.model.LatLng
import java.io.File

data class AddStoryRequest(
    val image: File,
    val description: String,
    val latLng: LatLng? = null
)
