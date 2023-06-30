package dev.rranndt.storay.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {

    @Inject
    lateinit var useCase: StoryUseCase

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StackRemoteViewsFactory(useCase, this.applicationContext)
}