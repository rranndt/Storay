package dev.rranndt.storay.core.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dev.rranndt.storay.core.data.local.db.StoryDatabase
import dev.rranndt.storay.core.data.local.entity.RemoteKey
import dev.rranndt.storay.core.data.local.entity.StoryEntity
import dev.rranndt.storay.core.data.mapper.toStoryEntity
import dev.rranndt.storay.core.data.remote.api.StoryApi
import dev.rranndt.storay.util.Constant.STARTING_PAGE_INDEX
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val db: StoryDatabase,
    private val api: StoryApi,
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>,
    ): MediatorResult {
        val page = when (val pageKeyData = getPageKeyData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }

            else -> pageKeyData as Int
        }

        try {
            val response = api.getStories(page = page, size = state.config.pageSize)
            val isEndOfList = response.listStory.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.getStoriesDao().deleteAll()
                    db.getKeysDao().deleteAll()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.listStory.map {
                    RemoteKey(it.id, prevKey, nextKey)
                }
                db.getKeysDao().insertAll(keys)
                db.getStoriesDao().insertAll(response.listStory.map { it.toStoryEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getPageKeyData(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>,
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, StoryEntity>,
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.getKeysDao().remoteKeysStoryId(id)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { story ->
                db.getKeysDao().remoteKeysStoryId(story.id)
            }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, StoryEntity>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { story ->
                db.getKeysDao().remoteKeysStoryId(story.id)
            }
    }
}