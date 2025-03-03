package com.innocv.smartdoc.api.controllers

import com.innocv.smartdoc.domain.Result
import com.innocv.smartdoc.repositories.ResultRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("results")
class ResultController(
    resultRepository: ResultRepository
) :
    CrudController<Result, String, ResultRepository>(resultRepository)


