package com.nedellis.azalea.health

import com.linecorp.armeria.client.Clients
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Table
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

object HealthClient {
    private val connections = ConcurrentHashMap<URI, HealthGrpcKt.HealthCoroutineStub>()

    private fun getConnection(addr: URI) = connections.getOrDefault(
        addr,
        Clients.newClient(addr, HealthGrpcKt.HealthCoroutineStub::class.java)
    )

    suspend fun updateTable(addr: URI, table: Table) = getConnection(addr).update(table)
}
