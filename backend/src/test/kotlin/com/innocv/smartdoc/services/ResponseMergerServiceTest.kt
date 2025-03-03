package com.innocv.smartdoc.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ResponseMergerServiceTest {
    @InjectMocks
    lateinit var service: ResponseMergerService

    @Test
    fun `test merge responses with overlap`() {
        val fullResponse = StringBuilder("The quick brown fox jumps")
        val newResponse = "umps over the lazy dog"

        val expectedResponse = "The quick brown fox jumps over the lazy dog"

        val mergedResponse = service.mergeResponses(fullResponse, newResponse)

        assertEquals(expectedResponse, mergedResponse.toString())
    }

    @Test
    fun `test merge responses without overlap`() {
        val fullResponse = StringBuilder("The quick brown fox jumps")
        val newResponse = " over the lazy dog"

        val expectedResponse = "The quick brown fox jumps over the lazy dog"

        val mergedResponse = service.mergeResponses(fullResponse, newResponse)

        assertEquals(expectedResponse, mergedResponse.toString())
    }

    @Test
    fun `test merge responses with multiple overlaps`() {
        val fullResponse = StringBuilder("To be or not to be")
        val newResponse1 = "to be, that is the question"
        val newResponse2 = "stion whether 'tis nobler in the mind"

        val expectedResponse = "To be or not to be, that is the question whether 'tis nobler in the mind"

        service.mergeResponses(fullResponse, newResponse1)
        val mergedResponse = service.mergeResponses(fullResponse, newResponse2)

        assertEquals(expectedResponse, mergedResponse.toString())
    }

    @Test
    fun `test merge responses with no overlap`() {
        val fullResponse = StringBuilder("All that glitters is not gold")
        val newResponse = "A journey of a thousand miles begins with a single step"

        val expectedResponse = "All that glitters is not goldA journey of a thousand miles begins with a single step"

        val mergedResponse = service.mergeResponses(fullResponse, newResponse)

        assertEquals(expectedResponse, mergedResponse.toString())
    }

}