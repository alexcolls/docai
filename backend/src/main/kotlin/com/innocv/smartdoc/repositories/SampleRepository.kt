package com.innocv.smartdoc.repositories



import com.innocv.smartdoc.domain.Sample
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SampleRepository : JpaRepository<Sample, String>