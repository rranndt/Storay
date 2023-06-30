package dev.rranndt.storay.core.data.remote

import dev.rranndt.storay.core.data.remote.dto.AddStoryResponseDto
import dev.rranndt.storay.core.data.remote.dto.SignInResponseDto
import dev.rranndt.storay.core.data.remote.dto.SignUpResponseDto
import dev.rranndt.storay.core.data.remote.dto.StoryDetailResponseDto
import dev.rranndt.storay.core.data.remote.dto.StoryResponseDto
import dev.rranndt.storay.util.Constant.DESCRIPTION
import dev.rranndt.storay.util.Constant.EMAIL
import dev.rranndt.storay.util.Constant.ID
import dev.rranndt.storay.util.Constant.LOGIN
import dev.rranndt.storay.util.Constant.NAME
import dev.rranndt.storay.util.Constant.PASSWORD
import dev.rranndt.storay.util.Constant.REGISTER
import dev.rranndt.storay.util.Constant.STORIES
import dev.rranndt.storay.util.Constant.STORIES_ID
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StoryApi {

    /**
     * WRITE
     **/
    @FormUrlEncoded
    @POST(REGISTER)
    suspend fun signUp(
        @Field(NAME) name: String,
        @Field(EMAIL) email: String,
        @Field(PASSWORD) password: String,
    ): SignUpResponseDto

    @FormUrlEncoded
    @POST(LOGIN)
    suspend fun signIn(
        @Field(EMAIL) email: String,
        @Field(PASSWORD) password: String,
    ): SignInResponseDto

    @Multipart
    @POST(STORIES)
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part(DESCRIPTION) description: RequestBody,
    ): AddStoryResponseDto

    /**
     * READ
     **/
    @GET(STORIES)
    suspend fun getStories(): StoryResponseDto

    @GET(STORIES_ID)
    suspend fun getDetailStory(
        @Path(ID) id: String,
    ): StoryDetailResponseDto
}