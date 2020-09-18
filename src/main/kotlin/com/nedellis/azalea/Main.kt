package com.nedellis.azalea

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.ServerListenerAdapter
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.LoggingService
import com.nedellis.azalea.health.HealthClient
import com.nedellis.azalea.health.HealthServiceImpl
import com.nedellis.azalea.registration.localAddress
import com.nedellis.azalea.registration.register
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URI
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
    val config = Config(ConfigFactory.load())
    val azaleaWrapper = AzaleaWrapper(config)
    val healthServer = HealthServiceImpl(azaleaWrapper)
    val logger = LoggerFactory.getLogger("Main")

    val port: Int = System.getenv("PORT").toInt()

    // Setup components after server has started
    val initializer = object : ServerListenerAdapter() {
        override fun serverStarted(server: Server) {
            val localAddress = URI("gproto+http://${localAddress()}:${server.activeLocalPort()}/")
            azaleaWrapper.init(localAddress)
            val neighbors = register(localAddress, config.redisURI)
            logger.info("Neighbors: $neighbors")
            runBlocking {
                launch { azaleaWrapper.run() }

                neighbors.forEach { neighbor ->
                    HealthClient.updateTable(neighbor, azaleaWrapper.table())
                }
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
