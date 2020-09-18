package com.nedellis.azalea

import com.nedellis.azalea.health.HealthClient
import com.nedellis.azalea.health.incrementSelf
import com.nedellis.azalea.health.merge
import com.nedellis.azalea.health.randomNeighbor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URI
import kotlin.time.ExperimentalTime

class AzaleaWrapper(private val config: Config) : Logging {
    private lateinit var azalea: Azalea
    private val lock = Mutex()

    /**
     * Initialize an Azalea instance and allow it to be used
     */
    fun init(localAddress: URI) {
        azalea = Azalea(localAddress, Table.newBuilder().putEntries(localAddress.toString(), 0).build())
    }

    @ExperimentalTime
    suspend fun run() = coroutineScope {
        launch {
            while (true) {
                delay(config.heartbeatInterval)
                azalea.table.randomNeighbor(azalea.localAddress)?.let { neighbor ->
                    updateTable(azalea.table.incrementSelf(azalea.localAddress))
                    logger().info("Broadcasting table to $neighbor")
                    HealthClient.updateTable(neighbor, azalea.table)
                }
            }
        }
    }

    suspend fun updateTable(other: Table) = lock.withLock {
        azalea = azalea.updateTable(other)
    }

    fun table(): Table = azalea.table
}

/**
 * Immutable, all actions return a new azalea instance.
 */
data class Azalea(val localAddress: URI, val table: Table) {
    fun updateTable(other: Table) = this.copy(table = table.merge(other))
}
