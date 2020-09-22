package com.nedellis.azalea.health

import com.linecorp.armeria.client.Clients
import com.nedellis.azalea.Config
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Logging
import com.nedellis.azalea.Table
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration

class HealthClient(private val config: Config) : Logging {
    private val connections = ConcurrentHashMap<URI, HealthGrpcKt.HealthCoroutineStub>()

    @ExperimentalTime
    private fun getConnection(address: URI) = connections.getOrDefault(
        address,
        Clients.builder(address)
            .responseTimeout(config.updateTableTimeout.toJavaDuration())
            .build(HealthGrpcKt.HealthCoroutineStub::class.java)
    )

    @ExperimentalTime
    suspend fun updateTable(address: URI, table: Table) = try {
        getConnection(address).update(table)
    } catch (e: Exception) {
        logger().error("Failed to update $address table: $e")
        if (config.isStrict) throw e
        Unit
    }
}
