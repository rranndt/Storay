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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.databinding.FragmentHomeBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Result
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
        binding?.swipeRefresh?.setOnRefreshListener { viewModel.onEvent(HomeEvent.GetStories) }
        subscribeToHomeEvent()

        isRefreshing(true)
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(requireContext())

        binding?.apply {
            recyclerView.apply {
                layoutManager = if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    LinearLayoutManager(context)
                } else {
                    GridLayoutManager(context, 2)
                }
                setHasFixedSize(true)
                adapter = contentAdapter
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
        }
        contentAdapter.onItemClick = { selectedData ->
            val actionToDetail =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(selectedData)
            findNavController().navigate(actionToDetail)
        }

    }

    private fun subscribeToHomeEvent() = lifecycleScope.launch {
        viewModel.getStories.collect { result ->
            when (result.getStories) {
                is Result.Success -> {
                    showLoading(false)
                    isRefreshing(false)
                    result.getStories.data?.let { contentAdapter.setData(it) }
                }

                is Result.Error -> {
                    showLoading(false)
                    isRefreshing(false)
                    binding?.apply {
                        recyclerView.isInvisible = true
                        layoutNoData.apply {
                            avNoData.isVisible = true
                            tvTitle.apply {
                                isVisible = true
                                text = result.getStories.message
                            }
                        }
                        viewModel.errorText.collect {
                            it.getContentIfNotHandled()?.let { text ->
                                root.showShortSnackBar(text)
                            }
                        }
                    }
                }

                is Result.Loading -> {
                    showLoading(true)
                    isRefreshing(false)
                }

                else -> {}
            }
        }
    }

    private fun isRefreshing(isRefreshing: Boolean) {
        binding?.swipeRefresh?.isRefreshing = isRefreshing
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }
}