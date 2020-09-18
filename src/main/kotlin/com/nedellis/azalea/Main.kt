package com.nedellis.azalea

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.ServerListenerAdapter
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.nedellis.azalea.health.HealthServiceImpl
import com.nedellis.azalea.registration.localAddress
import com.nedellis.azalea.registration.register
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import java.net.URI
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main(args: Array<String>) {
    val config = Config(ConfigFactory.load())
    val azaleaWrapper = AzaleaWrapper(config)
    val healthServer = HealthServiceImpl(azaleaWrapper)

    val port: Int = System.getenv("PORT").toInt()

    // Setup components after server has started
    val initializer = object : ServerListenerAdapter() {
        override fun serverStarted(server: Server) {
            runBlocking {
                val localAddress = URI("gproto+http://${localAddress()}:${server.activeLocalPort()}/")
                azaleaWrapper.init(localAddress)
                register(localAddress, config.redisURI)
            }
        }
    }

    val healthService = GrpcService.builder()
        .addService(healthServer)
        .supportedSerializationFormats(GrpcSerializationFormats.values())
        .build()
    val server = Server.builder()
        .http(port)
        .service(healthService, LoggingService.newDecorator())
        .serviceUnder("/docs", DocService())
        .serverListener(initializer)
        .build()

    server.start().join()
}
