package com.nedellis.azalea

/* ktlint-disable no-wildcard-imports */
import com.nedellis.azalea.health.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URI
import java.util.ArrayDeque
import kotlin.time.ExperimentalTime

/* ktlint-enable no-wildcard-imports */

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
        // Gossip Loop
        launch {
            while (true) {
                delay(config.heartbeatInterval)
                azalea.table.randomNeighbor(azalea.localAddress)?.let { neighbor ->
                    updateTable(azalea.table.incrementSelf(azalea.localAddress))
                    HealthClient.updateTable(neighbor, azalea.table)
                }
            }
        }

        // Prune Loop
        launch {
            // Keep a FIFO queue of table histories
            val history: ArrayDeque<Table> = ArrayDeque(2)
            history.add(azalea.table)
            delay(config.failInterval)

            while (true) {
                history.add(azalea.table) // Add old table...
                delay(config.failInterval) // Wait until next cycle...
                val oldestTable = history.removeLast()
            }
//            while (true) {
//                oldTable = azalea.table
//                delay(config.failInterval)
//                var pruned = stale
//                stale = azalea.table.stale(oldTable)
//                if (pruned.isNotEmpty()) {
//                    logger().info("Stale: $stale, Old: $oldTable Table: ${azalea.table}")
//                }
//            }
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
