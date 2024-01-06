package com.yusmp.plan.presentation.auth.blure

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yusmp.domain.common.extentions.orFalse
import com.yusmp.plan.R
import com.yusmp.plan.presentation.common.IMAGE_MANIPULATION_WORK_NAME
import com.yusmp.plan.presentation.common.KEY_IMAGE_URI
import com.yusmp.plan.presentation.common.TAG_OUTPUT
import com.yusmp.plan.presentation.common.baseFragment.BaseViewModel
import com.yusmp.plan.presentation.workers.BlurWorker
import com.yusmp.plan.presentation.workers.CleanupWorker
import com.yusmp.plan.presentation.workers.SaveImageToFileWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BlurViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
) : BaseViewModel<BlurUiState, BlurUiEvent>(BlurUiState()) {

    private var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    private val workManager = WorkManager.getInstance(appContext)

    override fun refresh(isUpdateAll: Boolean) = Unit

    init {
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow()
            .onEach { listOfWorkInfo ->
                val workInfo = listOfWorkInfo.firstOrNull()
                val workState = workInfo?.state
                updateUiState {
                    copy(
                        workState = when {
                            workState == null -> WorkState.NONE
                            workState.isFinished.orFalse() -> WorkState.FINISHED
                            else -> WorkState.PROGRESS
                        }
                    )
                }
                if (workState?.isFinished.orFalse()) {

                    val outputImageUri = workInfo?.outputData?.getString(KEY_IMAGE_URI)

                    if (!outputImageUri.isNullOrEmpty()) {
                        setOutputUri(outputImageUri)
                    }
                }
            }
            .launchIn(viewModelScope)

        imageUri = getImageUri(appContext)
    }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }

    internal fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )

        // Add WorkRequests to blur the image the number of times requested
        for (i in 0 until blurLevel) {
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()

            // Input the Uri if this is the first blur operation
            // After the first blur operation the input will be the output of previous
            // blur operations.
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurBuilder.build())
        }

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        // Add WorkRequest to save the image to the filesystem
        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .setConstraints(constraints)
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)

        // Actually start the work
        continuation.enqueue()
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()
    }

    private fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}