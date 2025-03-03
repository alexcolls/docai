package com.innocv.smartdoc.domain

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.sql.Types
import java.util.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class DocumentType(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    val id: String? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    var updatedAt: Date = Date(),

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    var createdAt: Date = Date(),

    @Column(columnDefinition = "TEXT")
    val prompt: String,

    @Fetch(FetchMode.JOIN)
    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE])
    var samples: List<Sample>?,
)