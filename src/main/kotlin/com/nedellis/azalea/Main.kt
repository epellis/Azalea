package com.nedellis.azalea

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.ServerListenerAdapter
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.nedellis.azalea.health.HealthClient
import com.nedellis.azalea.health.HealthServiceImpl
import com.typesafe.config.ConfigFactory
import java.net.URI

fun main(args: Array<String>) {
    val healthServer = HealthServiceImpl()
    val healthClient = HealthClient()
    val config = ConfigFactory.load()

    // Setup components after server has started
    val initializer = object : ServerListenerAdapter() {
        override fun serverStarted(server: Server) {
            Azalea(
                URI("gproto+http://127.0.0.1:${server.activeLocalPort()}/"),
                Children(healthServer, healthClient),
                config
            )
        }
    }

    val healthService = GrpcService.builder()
        .addService(healthServer)
        .supportedSerializationFormats(GrpcSerializationFormats.values())
        .build()
    val server = Server.builder()
        .service(healthService, LoggingService.newDecorator())
        .serviceUnder("/docs", DocService())
        .serverListener(initializer)
        .build()

    server.start().join()
}
