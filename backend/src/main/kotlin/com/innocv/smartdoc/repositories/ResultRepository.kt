package com.innocv.smartdoc.repositories


import com.innocv.smartdoc.domain.DocumentType
import com.innocv.smartdoc.domain.Result
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResultRepository : JpaRepository<Result, String> {
    fun findByDocumentType(documentType: DocumentType): List<Result>
    fun findByStatusAndUpdatedAtBefore(status: Result.Status, date: Date): List<Result>
    fun findByDocumentTypeId(documentTypeId: String): List<Result>
}