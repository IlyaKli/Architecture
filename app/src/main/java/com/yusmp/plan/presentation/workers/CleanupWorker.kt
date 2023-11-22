package com.yusmp.plan.presentation.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yusmp.plan.presentation.common.DELAY_TIME_MILLIS
import com.yusmp.plan.presentation.common.OUTPUT_PATH
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.io.File

@HiltWorker
class CleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        makeStatusNotification("Cleaning up old temporary files", applicationContext)

        delay(DELAY_TIME_MILLIS)

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }

    companion object {

        private const val TAG = "CleanupWorker"
    }
}
