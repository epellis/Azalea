package com.nedellis.azalea.health

import com.nedellis.azalea.HealthGrpcKt
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Main")

class HealthServiceImpl : HealthGrpcKt.HealthCoroutineImplBase()
