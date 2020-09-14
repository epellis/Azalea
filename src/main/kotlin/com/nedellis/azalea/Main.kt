package com.nedellis.azalea

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.nedellis.azalea.health.HealthServiceImpl
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    val healthService = GrpcService.builder()
        .addService(HealthServiceImpl())
        .supportedSerializationFormats(GrpcSerializationFormats.values())
        .build()
    val server = Server.builder()
        .serviceUnder("/health", healthService)
        .serviceUnder("/docs", DocService())
        .build()

    server.start().join()
}
