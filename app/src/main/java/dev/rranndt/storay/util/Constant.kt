package dev.rranndt.storay.util

import android.Manifest

object Constant {

    // token
    const val AUTHORIZATION = "Authorization"
    const val BEARER = "Bearer"

    // Multipart body
    const val PHOTO = "photo"
    const val IMAGE_JPEG = "image/jpeg"
    const val TEXT_PLAIN = "text/plain"

    // Api
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val STORIES = "stories"
    const val STORIES_ID = "stories/{id}"
    const val NAME = "name"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val DESCRIPTION = "description"
    const val ID = "id"

    // Preference
    const val USER_PREFERENCES = "userPreferences"
    const val USER_TOKEN = "userToken"
    const val LOGIN_STATUS = "loginStatus"

    // Media
    const val IMAGE = "image/*"
    const val JPG = ".jpg"

    // Size
    const val MAXIMAL_SIZE = 1000000

    // Object
    const val MESSAGE = "message"

    // Parse time
    private const val SECOND = 1
    const val MINUTE = 60 * SECOND
    const val HOUR = 60 * MINUTE
    const val DAY = 24 * HOUR
    const val WEEK = 7 * DAY
    const val MONTH = 30 * DAY
    const val YEAR = 12 * MONTH

    // Format date
    const val TIME_ZONE = "UTC"
    const val DEFAULT_TIME = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"
    const val PARSE_DEFAULT_TIME = "dd-MMM-yyyy"

    // Encryption
    const val PASS_PHRASE = "storay"

    // Database
    const val DB_NAME = "story.db"

    // Paging
    const val STARTING_PAGE_INDEX = 1
    const val PAGE_SIZE = 30

    // Permissions
    val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}