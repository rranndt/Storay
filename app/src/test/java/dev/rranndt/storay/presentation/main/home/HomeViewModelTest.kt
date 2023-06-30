package dev.rranndt.storay.presentation.main.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import dev.rranndt.storay.core.data.local.entity.StoryEntity
import dev.rranndt.storay.core.data.mapper.map
import dev.rranndt.storay.core.data.mapper.toPagingStoryResult
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.flow.FakeStoryUseCase
import dev.rranndt.storay.presentation.main.home.adapter.ContentAdapter
import dev.rranndt.storay.utils.DataDummy
import dev.rranndt.storay.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val storyUseCase = FakeStoryUseCase()


    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val homeViewModel = HomeViewModel(storyUseCase)
        val differ = AsyncPagingDataDiffer(
            diffCallback = ContentAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        homeViewModel.onEvent(HomeEvent.GetStories)
        storyUseCase.delegate.emit(data.toPagingStoryResult())
        differ.submitData(homeViewModel.getStories.value.getStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories.map().first(), differ.snapshot().first())
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryResult> = PagingData.empty()
        val homeViewModel = HomeViewModel(storyUseCase)
        val differ = AsyncPagingDataDiffer(
            diffCallback = ContentAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        homeViewModel.onEvent(HomeEvent.GetStories)
        storyUseCase.delegate.emit(data)
        differ.submitData(homeViewModel.getStories.value.getStories)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> =
        LoadResult.Page(emptyList(), 0, 1)

    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> = PagingData.from(items)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}