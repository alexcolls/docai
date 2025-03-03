package com.innocv.smartdoc.api.controllers

import com.innocv.smartdoc.domain.Document
import com.innocv.smartdoc.exceptions.DocumentNotFoundException
import com.innocv.smartdoc.repositories.DocumentRepository
import com.innocv.smartdoc.services.DocumentService
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name


@RestController
@RequestMapping("documents")
class DocumentController(
    val documentService: DocumentService,
    val documentRepository: DocumentRepository
) {
    @GetMapping("/{id}")
    fun findOne(@PathVariable("id") id: String) = documentRepository.findById(id)

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestParam("file") file: MultipartFile): Document =
        documentService.saveDocument(file)

    @GetMapping("/{id}/download")
    fun download(@PathVariable("id") id: String): ResponseEntity<Resource> {
        val document = documentRepository.findByIdOrNull(id) ?: throw DocumentNotFoundException()
        val path = documentService.getPath(document)
        val resource = InputStreamResource(FileInputStream(path.toFile()))

        return ResponseEntity.ok()
            .contentLength(path.toFile().length())
            .header("Content-Disposition", "attachment; filename=${document.name}")
            .contentType(getMediaType(path))
            .body(resource)

    }
    @GetMapping("/{id}/preview")
    fun preview(@PathVariable("id") id: String): ResponseEntity<Resource> {
        val document = documentRepository.findByIdOrNull(id) ?: throw DocumentNotFoundException()
        val path = documentService.getPreviewPath(document)
        val resource = InputStreamResource(FileInputStream(path.toFile()))
        val name = if (path.name.contains(".")) path.fileName else document.name
        return ResponseEntity.ok()
            .contentLength(path.toFile().length())
            .header("Content-Disposition", "attachment; filename=${name}")
            .contentType(getMediaType(path))
            .body(resource)
    }

    private fun getMediaType(path: Path): MediaType {
        val mimeType = Files.probeContentType(path) ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
        return MediaType.parseMediaType(mimeType)
    }
}