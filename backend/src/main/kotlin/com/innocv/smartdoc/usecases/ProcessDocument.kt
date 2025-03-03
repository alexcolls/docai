package com.innocv.smartdoc.usecases

import com.innocv.smartdoc.domain.Result
import com.innocv.smartdoc.exceptions.ResultNotFoundException
import com.innocv.smartdoc.repositories.ResultRepository
import com.innocv.smartdoc.services.JsonCuratorService
import com.innocv.smartdoc.services.VisionProcessorService
import kotlinx.coroutines.runBlocking
import org.jobrunr.jobs.annotations.Job
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component


@Component
class ProcessDocument(
    val visionService: VisionProcessorService,
    val jsonCuratorService: JsonCuratorService,
    val resultRepository: ResultRepository
) {
    @Job(name = "Process document (resultId=%0)")
    operator fun invoke(resultId: String): Result = runBlocking {
        val result = resultRepository.findByIdOrNull(resultId) ?: throw ResultNotFoundException()
        val processResult = visionService.processDocument(result.documentType!!, result.document)

        resultRepository.save(result.apply {
            rawResponse = processResult.rawResponse
            jsonResponse = jsonCuratorService.extractJsonOrNull(processResult.rawResponse)
            totalTokens = processResult.totalTokens
            status = Result.Status.DONE
        })
    }
}