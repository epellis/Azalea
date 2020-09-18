package com.nedellis.azalea

import org.slf4j.LoggerFactory

interface Logging {
    fun logger() = LoggerFactory.getLogger(this::class.java)
}
