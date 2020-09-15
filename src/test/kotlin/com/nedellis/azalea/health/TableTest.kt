package com.nedellis.azalea.health

import com.nedellis.azalea.Table
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TableTest : StringSpec({
    "Single entry with empty merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().build()
        first.updateOurTable(second).entries shouldBe mapOf("a" to 1)
    }

    "Empty entry with single merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().build()
        second.updateOurTable(first).entries shouldBe mapOf("a" to 1)
    }

    "Single entry with Single merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().putEntries("a", 2).build()
        first.updateOurTable(second).entries shouldBe mapOf("a" to 2)
    }

    "Increment Self" {
        val table = Table.newBuilder().putEntries("a", 1).build()
        table.incrementSelf("a").entries shouldBe mapOf("a" to 2)
    }

    "Prune with nonexistent old value keeps existent" {
        val table = Table.newBuilder().putEntries("a", 0).build()
        val old = Table.getDefaultInstance()
        table.pruneStale(old).entries shouldBe mapOf("a" to 0)
    }

    "Prune with equivalent old value removes" {
        val table = Table.newBuilder().putEntries("a", 0).build()
        val old = Table.newBuilder().putEntries("a", 0).build()
        table.pruneStale(old).entries shouldBe mapOf()
    }

    "Prune with older old value keeps existent" {
        val table = Table.newBuilder().putEntries("a", 1).build()
        val old = Table.newBuilder().putEntries("a", 0).build()
        table.pruneStale(old).entries shouldBe mapOf("a" to 1)
    }
})
