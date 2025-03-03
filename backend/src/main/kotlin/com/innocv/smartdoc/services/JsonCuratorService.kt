package com.innocv.smartdoc.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class JsonCuratorService(private val objectMapper: ObjectMapper) {

    fun extractJsonOrNull(input: String, fixIncompleteJson: Boolean = true): String? {
        var openBraces = 0
        var jsonStart = -1

        for (i in input.indices) {
            when (input[i]) {
                '{' -> {
                    if (openBraces == 0) {
                        jsonStart = i
                    }
                    openBraces++
                }
                '}' -> {
                    openBraces--
                    if (openBraces == 0 && jsonStart != -1) {
                        val jsonEnd = i + 1
                        val jsonString = input.substring(jsonStart, jsonEnd)
                        if (isValidJson(jsonString)) {
                            return jsonString
                        }
                    }
                }
            }
        }

        if (fixIncompleteJson && jsonStart != -1 && openBraces > 0) {
            val jsonString = input.substring(jsonStart) + "}".repeat(openBraces)
            if (isValidJson(jsonString)) {
                return jsonString
            }
        }

        return null
    }

    private fun isValidJson(json: String): Boolean {
        return try {
            objectMapper.readTree(json)
            true
        } catch (e: Exception) {
            false
        }
    }
}
