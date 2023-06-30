package dev.rranndt.storay.core.data.remote

import dev.rranndt.storay.core.data.mapper.toStoryUpload
import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.util.Constant.IMAGE_JPEG
import dev.rranndt.storay.util.Constant.PHOTO
import dev.rranndt.storay.util.Constant.TEXT_PLAIN
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: StoryApi,
) {

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ) = api.signUp(name, email, password)

    suspend fun signIn(
        email: String,
        password: String,
    ) = api.signIn(email, password)

    suspend fun getStories() = api.getStories()

    suspend fun getDetailStory(id: String) = api.getDetailStory(id)

    suspend fun addStory(story: AddStoryRequest): AddStoryResponse {
        val multipartBody = MultipartBody.Part.createFormData(
            name = PHOTO,
            filename = story.image.name,
            body = story.image.asRequestBody(IMAGE_JPEG.toMediaTypeOrNull())
        )
        val description = story.description.toRequestBody(TEXT_PLAIN.toMediaType())

        var lat: RequestBody? = null
        var lng: RequestBody? = null
        story.latLng?.let {
            lat = it.latitude.toString().toRequestBody("text/plain".toMediaType())
            lng = it.longitude.toString().toRequestBody("text/plain".toMediaType())
        }

        return api.addStory(multipartBody, description, lat, lng).toStoryUpload()
    }
}