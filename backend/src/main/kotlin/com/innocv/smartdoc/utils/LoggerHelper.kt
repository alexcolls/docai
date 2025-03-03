package com.innocv.smartdoc.utils


import org.jobrunr.jobs.context.JobRunrDashboardLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

fun <T : Any> logger(forClass: Class<T>): Logger = JobRunrDashboardLogger(LoggerFactory.getLogger((forClass.enclosingClass?.takeIf {
    forClass.enclosingClass.kotlin.companionObject?.java == forClass
} ?: forClass).name))


fun <T : Any> T.logger(): Logger {
    return logger(this.javaClass)
}

abstract class WithLogging {
    val log = logger()
}
