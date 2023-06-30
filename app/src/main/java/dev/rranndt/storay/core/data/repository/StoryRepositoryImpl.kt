package dev.rranndt.storay.core.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.rranndt.storay.R
import dev.rranndt.storay.core.data.mapper.toStoryResult
import dev.rranndt.storay.core.data.remote.RemoteDataSource
import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.core.domain.repository.StoryRepository
import dev.rranndt.storay.util.Helper.getErrorMessage
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    @ApplicationContext private val context: Context,
) : StoryRepository {

    override fun getStories(): Flow<Result<List<StoryResult>>> = flow {
        emit(Result.Loading())
        try {
            val response = remoteDataSource.getStories()
            val result = response.listStory.map { it.toStoryResult() }
            emit(Result.Success(result))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Result.Error(e.getErrorMessage()))
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Result.Error(context.getString(R.string.io_exception)))
        }
    }.flowOn(Dispatchers.IO)

    override fun getDetailStory(id: String): Flow<Result<StoryResult>> = flow {
        emit(Result.Loading())
        try {
            val response = remoteDataSource.getDetailStory(id)
            val result = response.listStory.toStoryResult()
            emit(Result.Success(result))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Result.Error(e.getErrorMessage()))
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Result.Error(context.getString(R.string.io_exception)))
        }
    }.flowOn(Dispatchers.IO)

    override fun addStory(story: AddStoryRequest): Flow<Result<AddStoryResponse>> = flow {
        emit(Result.Loading())
        try {
            val response = remoteDataSource.addStory(story)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Result.Error(e.getErrorMessage()))
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Result.Error(context.getString(R.string.io_exception)))
        }
    }.flowOn(Dispatchers.IO)

    override fun widgetStories(): Flow<List<StoryResult>> = flow {
        try {
            val response = remoteDataSource.getStories()
            val result = response.listStory.map { it.toStoryResult() }
            emit(result)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}