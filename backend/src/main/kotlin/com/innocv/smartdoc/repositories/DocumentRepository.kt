package com.innocv.smartdoc.repositories


import com.innocv.smartdoc.domain.Document
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentRepository : JpaRepository<Document, String>