package com.innocv.smartdoc.api.controllers

import com.innocv.smartdoc.domain.DocumentType
import com.innocv.smartdoc.domain.Result
import com.innocv.smartdoc.exceptions.DocumentNotFoundException
import com.innocv.smartdoc.exceptions.DocumentTypeNotFoundException
import com.innocv.smartdoc.repositories.DocumentRepository
import com.innocv.smartdoc.repositories.DocumentTypeRepository
import com.innocv.smartdoc.repositories.ResultRepository
import com.innocv.smartdoc.usecases.ProcessDocument
import org.jobrunr.scheduling.JobScheduler
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("document-types")
class DocumentTypeController(
    documentTypeRepository: DocumentTypeRepository,
    val processDocument: ProcessDocument,
    val documentRepository: DocumentRepository,
    val resultRepository: ResultRepository,
    val scheduler: JobScheduler
) :
    CrudController<DocumentType, String, DocumentTypeRepository>(documentTypeRepository) {

    @PostMapping("/{id}/create-process-document-job")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createProcessDocumentJob(
        @PathVariable id: String,
        @RequestParam("documentId") documentId: String
    ): Result {
        val result = createResult(id, documentId)
        scheduler.enqueue {
            processDocument(result.id!!)
        }
        return result
    }

    @PostMapping("/{id}/process-document")
    fun processExisting(
        @PathVariable id: String,
        @RequestParam("documentId") documentId: String
    ): Result {
        val result = createResult(id, documentId)

        return processDocument(result.id!!)
    }

    private fun createResult(id: String, documentId: String): Result {
        val documentType = repository.findByIdOrNull(id) ?: throw DocumentTypeNotFoundException()

        val document = documentRepository.findByIdOrNull(documentId) ?: throw DocumentNotFoundException()
        return resultRepository.save(
            Result(
                prompt = documentType.prompt,
                document = document,
                engine = "gpt4o",
                samples = documentType.samples?.toMutableList(),
                documentType = documentType
            )
        )
    }

    @GetMapping("/{id}/results")
    fun results( @PathVariable id: String): List<Result> {
        val documentType = repository.findByIdOrNull(id) ?: throw DocumentTypeNotFoundException()
        return resultRepository.findByDocumentType(documentType)
    }

    @DeleteMapping("/{id}")
    override fun delete(@PathVariable("id") id: String) {
        resultRepository.findByDocumentTypeId(id)
        repository.deleteById(id)
    }
}
