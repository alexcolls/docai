package com.innocv.smartdoc.api.controllers

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.*


abstract class CrudController<T : Any, ID : Any, R : JpaRepository<T, ID>>(val repository: R) {
    @GetMapping("/{id}")
    open fun findOne(@PathVariable("id") id: ID) = repository.findById(id)

    @GetMapping
    open fun findAll() = repository.findAll()

    @PostMapping
    open fun create(@RequestBody entity: T) = repository.save(entity)

    @PutMapping("/{id}")
    open fun update(@RequestBody entity: T) = repository.save(entity)

    @DeleteMapping("/{id}")
    open fun delete(@PathVariable("id") id: ID) = repository.deleteById(id)
}