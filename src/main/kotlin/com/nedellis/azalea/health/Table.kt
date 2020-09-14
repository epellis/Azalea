package com.nedellis.azalea.health

import com.nedellis.azalea.Table
import kotlin.math.max

/**
 * Update this table with the other's entries.
 * If the entry is in both tables, keep the higher value
 */
fun Table.update(other: Table): Table {
    val ours = this.entriesMap.toMap()
    val theirs = other.entriesMap.toMap()
    val both = (ours.keys + theirs.keys)
        .map { key -> Pair(key, max(ours.getOrDefault(key, 0), theirs.getOrDefault(key, 0))) }
        .toMap()
    return Table.newBuilder().putAllEntries(both).build()
}
