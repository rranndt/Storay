package dev.rranndt.storay.presentation.main.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.databinding.ActivityDetailBinding
import dev.rranndt.storay.util.Helper
import dev.rranndt.storay.util.Helper.colorBackground
import dev.rranndt.storay.util.Helper.parseToTimeAgo
import dev.rranndt.storay.util.Helper.showFirstLetter
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private val args by navArgs<DetailActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbarDetail.toolbar)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_detail_title)
        }

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
                    binding.apply {
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
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivImgContent)

            tvProfile.colorBackground()
            tvProfile.text = story.name.showFirstLetter()
            tvName.text = story.name
            tvLocation.text = Helper.parseToAddress(this@DetailActivity, story.lat, story.lon)
            tvDescription.text = story.description
            tvUploaded.text = this@DetailActivity.getString(
                R.string.text_uploaded,
                story.createdAt.parseToTimeAgo(this@DetailActivity)
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}