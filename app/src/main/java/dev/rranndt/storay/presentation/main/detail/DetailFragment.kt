package dev.rranndt.storay.presentation.main.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.databinding.FragmentDetailBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.util.Helper.colorBackground
import dev.rranndt.storay.util.Helper.parseToAddress
import dev.rranndt.storay.util.Helper.parseToTimeAgo
import dev.rranndt.storay.util.Helper.showFirstLetter
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding, DetailViewModel>() {

    private val args by navArgs<DetailFragmentArgs>()

    override val viewModel: DetailViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentDetailBinding = FragmentDetailBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.storyResult.let { viewModel.onEvent(DetailEvent.GetDetailStory(it.id)) }
        subscribeToDetailEvent()
    }

    private fun subscribeToDetailEvent() = lifecycleScope.launch {
        viewModel.getDetailStory.collect { result ->
            when (result.getDetailStory) {
                is Result.Success -> {
                    result.getDetailStory.data?.let { fetchData(it) }
                    showLoading(false)
                }

                is Result.Error -> {
                    showLoading(false)
                    binding?.apply {
                        detailContainer.visibility = View.GONE
                        layoutNoData.apply {
                            avNoData.isVisible = true
                            tvTitle.apply {
                                isVisible = true
                                text = result.getDetailStory.message
                            }
                        }
                    }
                }

                is Result.Loading -> {
                    showLoading(true)
                }

                else -> {}
            }
        }
    }

    private fun fetchData(story: StoryResult) {
        binding?.apply {
            Glide.with(requireContext())
                .load(story.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivImgContent)

            tvProfile.colorBackground()
            tvProfile.text = story.name.showFirstLetter()
            tvName.text = story.name
            tvLocation.text = parseToAddress(requireContext(), story.lat, story.lon)
            tvDescription.text = story.description
            tvUploaded.text = requireContext().getString(
                R.string.text_uploaded,
                story.createdAt.parseToTimeAgo(requireContext())
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }
}