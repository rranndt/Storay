package dev.rranndt.storay.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import dev.rranndt.storay.R
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val useCase: StoryUseCase,
    private val context: Context,
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetBitmap = arrayListOf<Bitmap>()
    private val mItems = arrayListOf<StoryResult>()

    override fun onCreate() {}

    // For suspend function
    override fun onDataSetChanged() = runBlocking {
        try {
            val result = useCase.widgetStories().first()
            val bitmap = result.map {
                Glide.with(context)
                    .asBitmap()
                    .load(it.photoUrl)
                    .override(200, 200)
                    .submit()
                    .get()
            }
            mWidgetBitmap.clear()
            mWidgetBitmap.addAll(bitmap)
            mItems.clear()
            mItems.addAll(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        StoryBannerWidget.notifyDataSetChanged(context)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = mItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.ivStory, mWidgetBitmap[position])

        val extras = bundleOf(StoryBannerWidget.EXTRA_ITEM to mItems[position].id)
        val fillInIntent = Intent().apply { putExtras(extras) }

        rv.setOnClickFillInIntent(R.id.ivStory, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}