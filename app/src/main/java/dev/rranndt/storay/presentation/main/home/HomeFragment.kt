package dev.rranndt.storay.presentation.main.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.databinding.FragmentHomeBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.presentation.main.home.adapter.ContentAdapter
import dev.rranndt.storay.presentation.main.home.adapter.LoadingStateAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private lateinit var contentAdapter: ContentAdapter

    override val viewModel: HomeViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToHomeEvent()
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(requireContext()) { selectedData ->
            val actionToDetail =
                HomeFragmentDirections.actionHomeFragmentToDetailActivity(selectedData)
            findNavController().navigate(actionToDetail)
        }

        binding?.apply {
            recyclerView.apply {
                layoutManager =
                    if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        LinearLayoutManager(context)
                    } else {
                        GridLayoutManager(context, 2)
                    }
                setHasFixedSize(true)
                adapter = contentAdapter
                adapter = contentAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { contentAdapter.retry() }
                )
                contentAdapter.addLoadStateListener {
                    if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && contentAdapter.itemCount < 1) {
                        tvNoData.isVisible = true
                    }
                }

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0) {
                            fabScrollUp.isVisible = true
                        } else if (dy < 0) {
                            fabScrollUp.isInvisible = true
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                fabScrollUp.isInvisible = true
                            }
                        }, 1000)
                    }
                })
            }
            fabScrollUp.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }
            swipeRefresh.setOnRefreshListener { contentAdapter.refresh() }
        }
    }

    private fun subscribeToHomeEvent() = lifecycleScope.launch {
        viewModel.getStories.collect { result ->
            contentAdapter.submitData(lifecycle, result.getStories)
            hideRefreshing()
        }
    }

    private fun hideRefreshing() {
        binding?.apply {
            swipeRefresh.isRefreshing = false
        }
    }
}