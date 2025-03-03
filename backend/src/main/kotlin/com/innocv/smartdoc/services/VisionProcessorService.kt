package com.innocv.smartdoc.services

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.ListContent
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.innocv.smartdoc.api.dto.LlmResponse
import com.innocv.smartdoc.domain.Document
import com.innocv.smartdoc.domain.DocumentType
import com.innocv.smartdoc.utils.WithLogging
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.poi.hslf.usermodel.HSLFSlideShow
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.time.measureTime

@OptIn(kotlin.time.ExperimentalTime::class)
@Service
class VisionProcessorService(
    val openAI: OpenAI, val documentService: DocumentService,
    val responseMergerService: ResponseMergerService,
) {
    companion object : WithLogging()

    @Value("\${smart-doc.openai.model-id}")
    lateinit var openaiApiVersion: String

    suspend fun processDocument(documentType: DocumentType, document: Document): LlmResponse {
        val messages = mutableListOf<ChatMessage>()

        val responseFormat =
            if (documentType.prompt.lowercase().contains("json")) ChatResponseFormat.JsonObject else null
        log.info(
            "Processing document: {}, samples={}, format={}",
            document.id,
            documentType.samples ?: 0,
            responseFormat
        )
        messages += ChatMessage(
            role = Role.System,
            content = documentType.prompt
        )

        documentType.samples?.forEachIndexed { _, sample ->
            messages += ChatMessage(
                role = Role.User,
                ListContent(
                    getDocumentContent(sample.document) +
                            TextPart("Expected response for this sample: \n${sample.response}")
                )
            )
        }

        messages += ChatMessage(
            role = Role.User,
            ListContent(
                getDocumentContent(document)
            )
        )

        var result: ChatCompletion
        val fullResponse = StringBuilder()
        var continueToken = true
        var totalTokens = 0
        var continueCount = 0
        val maxContinues = 20
        val contextLength = 50

        while (continueToken && continueCount < maxContinues) {
            val chatCompletionTime = measureTime {
                result = openAI.chatCompletion(
                    ChatCompletionRequest(
                        model = ModelId(openaiApiVersion),
                        messages = messages,
                        responseFormat = if (continueCount > 0) null else responseFormat
                    ),
                )
            }
            log.info("Chat completion for document {} took {}", document.id, chatCompletionTime)
            val choice = result.choices.first()
            val responseContent = choice.message.content.orEmpty()
            responseMergerService.mergeResponses(fullResponse, responseContent)
            totalTokens += result.usage?.totalTokens ?: 0

            continueToken = choice.finishReason == FinishReason.Length

            if (continueToken) {
                val context = if (responseContent.length > contextLength) {
                    responseContent.takeLast(contextLength)
                } else {
                    responseContent
                }
                messages += ChatMessage(
                    role = Role.Assistant,
                    content = responseContent
                )
                messages += ChatMessage(
                    role = Role.User,
                    content = "Please continue the response from where you left off. Here is the last part of the response for context:\n\n$context\n\n. Include this context part in your reply so i can know where to merge the responses."
                )
                continueCount++
            }
        }

        val response = LlmResponse(
            rawResponse = fullResponse.toString(),
            totalTokens = totalTokens
        )
        log.debug("document={} response={}", document, response)
        return response
    }

    fun getDocumentContent(document: Document): List<ImagePart> {
        val imageParts: MutableList<ImagePart> = mutableListOf()

        val totalExecutionTime = measureTime {
            val binaryData = documentService.getBinaryData(document)
            when(val extension = document.name?.substringAfterLast('.', "")?.lowercase().orEmpty()) {
                "jpg", "jpeg", "png", "webp", "gif" -> {
                    val base64 = java.util.Base64.getEncoder().encodeToString(binaryData)
                    imageParts.add(
                        ImagePart(
                            imageUrl = ImagePart.ImageURL(url = "data:image/$extension;base64,$base64", detail = "high")
                        )
                    )
                }

                "pdf" -> {
                    imageParts.addAll(convertPdfToImageParts(binaryData))
                }

                "ppt", "pptx" -> {
                    imageParts.addAll(convertPptToImageParts(binaryData, extension))
                }

                else -> throw IllegalArgumentException("Unsupported document type: $extension")
            }
        }
        log.info("getDocumentContent for document {} took {}", document.id, totalExecutionTime)
        return imageParts
    }

    fun convertPdfToImageParts(pdfData: ByteArray): List<ImagePart> {
        val imageParts = mutableListOf<ImagePart>()
        var document: PDDocument? = null

        try {
            document = PDDocument.load(pdfData)
            val pdfRenderer = PDFRenderer(document)

            for (page in 0 until document.numberOfPages) {
                val image: BufferedImage = pdfRenderer.renderImageWithDPI(page, 200f)
                val outputStream = ByteArrayOutputStream()
                ImageIO.write(image, "png", outputStream)
                val base64Image = java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray())
                imageParts.add(
                    ImagePart(
                        imageUrl = ImagePart.ImageURL(url = "data:image/png;base64,$base64Image", detail = "high")
                    )
                )
            }
        } finally {
            document?.close()
        }
        return imageParts
    }

    fun convertPptToImageParts(pptData: ByteArray, extension: String): List<ImagePart> {
        val imageParts = mutableListOf<ImagePart>()
        val slideShow = if (extension == "ppt") {
            HSLFSlideShow(ByteArrayInputStream(pptData))
        } else {
            XMLSlideShow(ByteArrayInputStream(pptData))
        }

        val slides = if (extension == "ppt") {
            (slideShow as HSLFSlideShow).slides
        } else {
            (slideShow as XMLSlideShow).slides
        }

        for (slide in slides) {
            val pageSize = slideShow.pageSize
            val image = BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_ARGB)
            val graphics: Graphics2D = image.createGraphics()

            if (extension == "ppt") {
                (slide as org.apache.poi.hslf.usermodel.HSLFSlide).draw(graphics)
            } else {
                (slide as XSLFSlide).draw(graphics)
            }

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            val base64Image = java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray())
            imageParts.add(
                ImagePart(
                    imageUrl = ImagePart.ImageURL(url = "data:image/png;base64,$base64Image", detail = "high")
                )
            )
        }

        slideShow.close()
        return imageParts
    }
}
