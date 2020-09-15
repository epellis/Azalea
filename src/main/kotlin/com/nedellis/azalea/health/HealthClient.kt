package com.nedellis.azalea.health

import com.linecorp.armeria.client.Clients
import com.nedellis.azalea.Azalea
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Providable
import com.nedellis.azalea.Table
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

class HealthClient : Providable {
    override lateinit var parent: Azalea
    private val connections = ConcurrentHashMap<URI, HealthGrpcKt.HealthCoroutineStub>()

    suspend fun update(addr: URI, table: Table) {
        connections.getOrDefault(
            addr,
            Clients.newClient(addr, HealthGrpcKt.HealthCoroutineStub::class.java)
        ).update(table)
    }
}
