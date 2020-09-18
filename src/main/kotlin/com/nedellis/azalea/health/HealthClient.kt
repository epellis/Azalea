package com.nedellis.azalea.health

import com.linecorp.armeria.client.Clients
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Table
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

object HealthClient {
    private val connections = ConcurrentHashMap<URI, HealthGrpcKt.HealthCoroutineStub>()

    private fun getConnection(address: URI) = connections.getOrDefault(
        address,
        Clients.newClient(address, HealthGrpcKt.HealthCoroutineStub::class.java)
    )

    suspend fun updateTable(address: URI, table: Table) = getConnection(address).update(table)
}
