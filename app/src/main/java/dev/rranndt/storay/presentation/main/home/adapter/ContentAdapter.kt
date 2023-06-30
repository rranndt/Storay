package dev.rranndt.storay.presentation.main.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.databinding.ItemStoryBinding
import dev.rranndt.storay.util.Helper.colorBackground
import dev.rranndt.storay.util.Helper.parseToAddress
import dev.rranndt.storay.util.Helper.parseToTimeAgo
import dev.rranndt.storay.util.Helper.showFirstLetter

class ContentAdapter(
    private val context: Context,
    private val onItemClick: (StoryResult) -> Unit,
) :
    PagingDataAdapter<StoryResult, ContentAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResult) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivImgContent)

                tvProfile.colorBackground()
                tvProfile.text = story.name.showFirstLetter()
                tvName.text = story.name
                tvLocation.text =
                    parseToAddress(itemView.context, story.lat, story.lon)
                tvUploaded.text = itemView.context.getString(
                    R.string.text_uploaded,
                    story.createdAt.parseToTimeAgo(context),
                )
            }

            itemView.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResult>() {
            override fun areItemsTheSame(oldItem: StoryResult, newItem: StoryResult): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryResult,
                newItem: StoryResult,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}