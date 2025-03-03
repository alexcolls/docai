package com.innocv.smartdoc

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
class SmartDocApplication {
    @Bean
    fun openAi(@Value("\${smart-doc.openai.api-key}") apiKey: String) = OpenAI(
        apiKey,
        logging = LoggingConfig(
            logLevel = LogLevel.Info,
            logger = Logger.Default
        ),
        timeout = Timeout(socket = 15.minutes)
    )

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOriginPattern("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}

fun main(args: Array<String>) {
    runApplication<SmartDocApplication>(*args)
}
