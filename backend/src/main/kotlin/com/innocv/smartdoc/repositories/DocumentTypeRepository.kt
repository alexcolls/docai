package com.innocv.smartdoc.repositories


import com.innocv.smartdoc.domain.DocumentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentTypeRepository : JpaRepository<DocumentType, String>