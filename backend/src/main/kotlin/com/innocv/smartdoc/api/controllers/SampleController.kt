package com.innocv.smartdoc.api.controllers

import com.innocv.smartdoc.domain.Sample
import com.innocv.smartdoc.repositories.SampleRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("samples")
class SampleController(
    sampleRepository: SampleRepository
) :
    CrudController<Sample, String, SampleRepository>(sampleRepository)


