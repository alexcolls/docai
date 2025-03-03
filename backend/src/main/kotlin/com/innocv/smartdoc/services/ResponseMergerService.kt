package com.innocv.smartdoc.services

import org.springframework.stereotype.Service

@Service
class ResponseMergerService {
    private fun findOverlapIndex(previous: String, current: String): Int {
        for (i in previous.indices) {
            if (current.startsWith(previous.substring(i))) {
                return previous.length - i
            }
        }
        return -1
    }
    fun mergeResponses(fullResponse: StringBuilder, newResponse: String): StringBuilder {
        val overlapIndex = findOverlapIndex(fullResponse.toString(), newResponse)
        if (overlapIndex != -1) {
            fullResponse.append(newResponse.substring(overlapIndex))
        } else {
            fullResponse.append(newResponse)
        }
        return fullResponse
    }
}