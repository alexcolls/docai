import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.innocv.smartdoc.services.JsonCuratorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class JsonCuratorServiceTest {

    @InjectMocks
    lateinit var jsonCuratorService: JsonCuratorService

    @Spy
    val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `test extractJsonOrNull with valid JSON`() {
        val input = """{"key": "value"}"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(input, result)
    }

    @Test
    fun `test extractJsonOrNull with text before JSON`() {
        val input = """Some text before {"key": "value"}"""
        val expected = """{"key": "value"}"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(expected, result)
    }

    @Test
    fun `test extractJsonOrNull with text after JSON`() {
        val input = """{"key": "value"} Some text after"""
        val expected = """{"key": "value"}"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(expected, result)
    }

    @Test
    fun `test extractJsonOrNull with text before and after JSON`() {
        val input = """Some text before {"key": "value"} Some text after"""
        val expected = """{"key": "value"}"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(expected, result)
    }

    @Test
    fun `test extractJsonOrNull with no JSON`() {
        val input = """Some text without JSON"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertNull(result)
    }

    @Test
    fun `test extractJsonOrNull with multiple JSON objects`() {
        val input = """{"key1": "value1"} Some text {"key2": "value2"}"""
        val expected = """{"key1": "value1"}"""
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(expected, result)
    }
    @Test
    fun `test extractJsonOrNull real example`() {
        val input = "```json\n{\"name\": \"Pedro **\",\"surname\": \"*****\",  \"idNumber\": \"*****A\",\n  \"birthDate\": \"31.08.1970\"\n}\n```"
        val expected = "{\"name\": \"Pedro **\",\"surname\": \"*****\",  \"idNumber\": \"*****A\",\n  \"birthDate\": \"31.08.1970\"\n}"
        val result = jsonCuratorService.extractJsonOrNull(input)
        assertEquals(expected, result)
    }


}
