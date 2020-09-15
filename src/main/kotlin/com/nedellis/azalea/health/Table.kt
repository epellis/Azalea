package com.nedellis.azalea.health

import com.nedellis.azalea.Table
import kotlin.math.max

/**
 * Update this table with the other's entries.
 * If the entry is in both tables, keep the higher value
 */
fun Table.updateOurTable(other: Table): Table {
    val ours = this.entriesMap.toMap()
    val theirs = other.entriesMap.toMap()
    val both = (ours.keys + theirs.keys)
        .map { key -> Pair(key, max(ours.getOrDefault(key, 0), theirs.getOrDefault(key, 0))) }
        .toMap()
    return Table.newBuilder().putAllEntries(both).build()
}

fun Table.incrementSelf(localAddress: String): Table {
    val ours = this.entriesMap.toMutableMap()
    ours[localAddress] = ours[localAddress]?.inc()
    return Table.newBuilder().putAllEntries(ours).build()
}
