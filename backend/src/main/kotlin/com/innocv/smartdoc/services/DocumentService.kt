package com.innocv.smartdoc.services

import com.innocv.smartdoc.domain.Document
import com.innocv.smartdoc.repositories.DocumentRepository
import com.innocv.smartdoc.utils.WithLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.exists

@Service
class DocumentService(
    val documentRepository: DocumentRepository,
    @Value("\${document.folder}") documentFolderPath: String
) {

    companion object : WithLogging()

    private val documentFolder = Paths.get(documentFolderPath)

    init {
        if (!Files.exists(documentFolder)) {
            Files.createDirectories(documentFolder)
        }
    }

    @Transactional
    fun saveDocument(file: MultipartFile): Document {
        val doc = documentRepository.save(Document(name = file.originalFilename!!))
        val filePath = documentFolder.resolve(doc.id!!)
        Files.copy(file.inputStream, filePath)
        log.debug("Document saved {} at {}",doc, filePath.toFile().absolutePath)
        return doc
    }

    fun getPath(file: Document): Path = documentFolder.resolve(file.id!!)

    fun getBinaryData(file: Document): ByteArray {
        val filePath = getPath(file)
        return Files.readAllBytes(filePath)
    }

    fun getPreviewPath(document: Document): Path {
        val path = getPath(document)
        if (document.name?.lowercase()?.endsWith(".pdf") != true) {
            return path
        }
        val previewPath = documentFolder.resolve("${document.id}_preview.png")
        if (previewPath.exists()) {
            return previewPath
        }
        val pdfDoc = PDDocument.load(getBinaryData(document))
        val pdfRenderer = PDFRenderer(pdfDoc)
        val image: BufferedImage = pdfRenderer.renderImageWithDPI(0, 300f)
        val outputStream = FileOutputStream(previewPath.toFile())
        ImageIO.write(image, "png", outputStream)
        return previewPath
    }
}