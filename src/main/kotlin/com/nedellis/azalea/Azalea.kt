package com.nedellis.azalea

import com.nedellis.azalea.health.HealthClient
import com.nedellis.azalea.health.HealthServiceImpl
import com.nedellis.azalea.health.update
import com.nedellis.azalea.registration.register
import com.typesafe.config.Config
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.net.URI

interface Providable {
    var parent: Azalea // Should be lateinit in implementations
    fun provide(parent: Azalea) = run { this.parent = parent }
}

data class Children(val healthServer: HealthServiceImpl, val healthClient: HealthClient) : Iterable<Providable> {
    override fun iterator(): Iterator<Providable> {
        return listOf(healthServer, healthClient).iterator()
    }
}

private val log = LoggerFactory.getLogger("Main")

/**
 * Contain state and coordinate interactions between services
 */
class Azalea(val address: URI, services: Children, val conf: Config) {
    private val tableLock = Mutex()
    private var table = Table.newBuilder().putEntries(address.toString(), 0).build()

    init {
        services.forEach { it.provide(this) }
        val members = register(address, URI(conf.getString("azalea.redis-uri")))
        log.info("Other Members: $members")
    }

    suspend fun updateTable(other: Table) = tableLock.withLock {
        table = table.update(other)
    }
}
