package com.nedellis.azalea

import com.nedellis.azalea.health.HealthClient
import com.nedellis.azalea.health.HealthServiceImpl
import com.nedellis.azalea.health.incrementSelf
import com.nedellis.azalea.health.updateOurTable
import com.nedellis.azalea.registration.register
import com.typesafe.config.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
class Azalea(val address: URI, private val services: Children, val config: Config) {
    private val tableLock = Mutex()
    private var table = Table.newBuilder().putEntries(address.toString(), 0).build()

    init {
        log.info("Starting Azalea Member at $address")

        services.forEach { it.provide(this) }
        val members = register(address, URI(config.getString("azalea.redis-uri")))
        log.info("Other Members: $members")
        runBlocking {
            members.forEach { member ->
                try {
                    services.healthClient.updateTheirTable(member, table)
                } catch (e: Throwable) {
                    log.error("Could not connect to $member, $e")
                }
            }
        }

        GlobalScope.launch { heartbeat() }
    }

    /**
     * Every n milliseconds, increment heartbeat and broadcast to a random neighbor
     */
    private suspend fun heartbeat() {
        val tickerChannel = ticker(delayMillis = config.getInt("azalea.heartbeat-interval-ms").toLong())
        while (true) {
            tickerChannel.receive()
            tableLock.withLock {
                table = table.incrementSelf(address.toString())
            }
            val neighbor = table.entriesMap.keys.filter { it != address.toString() }.random()
            services.healthClient.updateTheirTable(URI(neighbor), table)
        }
    }

    suspend fun updateOurTable(other: Table) = tableLock.withLock {
        table = table.updateOurTable(other)
    }
}
