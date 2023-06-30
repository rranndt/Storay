package dev.rranndt.storay.util.permission

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

sealed class Permission(vararg val permissions: String) {
    object Camera : Permission(CAMERA)
    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

    companion object {
        fun from(permission: String) = when (permission) {
            CAMERA -> Camera
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}
