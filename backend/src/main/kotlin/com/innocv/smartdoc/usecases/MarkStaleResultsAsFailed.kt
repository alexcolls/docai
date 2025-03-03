package com.innocv.smartdoc.usecases

import com.innocv.smartdoc.domain.Result
import com.innocv.smartdoc.repositories.ResultRepository
import com.innocv.smartdoc.utils.WithLogging
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.annotations.Recurring
import org.springframework.stereotype.Component
import java.util.*

@Component
class MarkStaleResultsAsFailed(
    val resultRepository: ResultRepository
) {
    companion object: WithLogging()

    @Job(name = "Mark stale results as failed")
    @Recurring(id = "markStaleResultsJob", cron = "0 * * * * *") // Every minute
    operator fun invoke() {
        val checkpointDatetime = Date(System.currentTimeMillis() - 60 * 60 * 1000)
        val staleResults = resultRepository.findByStatusAndUpdatedAtBefore(Result.Status.PROCESSING, checkpointDatetime)
        staleResults.forEach { result ->
            result.status = Result.Status.FAILED
            resultRepository.save(result)
            log.info("Result ${result.id} marked as ${result.status}")
        }
    }
}
