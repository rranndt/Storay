package dev.rranndt.storay.presentation.main.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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


class ContentAdapter(private val context: Context) :
    RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    private var listData = ArrayList<StoryResult>()
    var onItemClick: ((story: StoryResult) -> Unit)? = null

    fun setData(newListData: List<StoryResult>) {
        val diffCallback = DiffUtils(listData, newListData)
        val diffUtil = DiffUtil.calculateDiff(diffCallback)
        listData.clear()
        listData.addAll(newListData)
        diffUtil.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listData.size

    inner class ViewHolder(private val binding: ItemStoryBinding) :
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
                tvLocation.text = parseToAddress(itemView.context, story.lat, story.lon)
                tvUploaded.text = itemView.context.getString(
                    R.string.text_uploaded,
                    story.createdAt.parseToTimeAgo(context),
                )
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }

}

class DiffUtils<O, N>(private val oldList: List<O>, private val newList: List<N>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}