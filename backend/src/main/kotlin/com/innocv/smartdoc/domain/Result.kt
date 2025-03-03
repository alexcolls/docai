package com.innocv.smartdoc.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class Result(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    val id: String? = null,

    @Column(columnDefinition = "TEXT")
    val prompt: String?,

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    val documentType: DocumentType?,

    @ManyToOne
    val document: Document,

    @Column(columnDefinition = "TEXT")
    var jsonResponse: String? = null,

    @Column(columnDefinition = "TEXT")
    var rawResponse: String? = null,

    var totalTokens: Int? = null,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    var updatedAt: Date = Date(),

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    var createdAt: Date = Date(),

    val engine: String?,

    @ManyToMany(fetch = FetchType.EAGER)
    var samples: List<Sample>?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT 'DONE'")
    var status: Status = Status.PROCESSING
) {
    enum class Status {
        PROCESSING,
        FAILED,
        DONE,
    }
}