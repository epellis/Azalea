package com.nedellis.azalea.health

import com.nedellis.azalea.Table
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TableTest : StringSpec({
    "Single entry with empty merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().build()
        first.updateTheirTable(second).entries shouldBe mapOf("a" to 1)
    }

    "Empty entry with single merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().build()
        second.updateTheirTable(first).entries shouldBe mapOf("a" to 1)
    }

    "Single entry with Single merge" {
        val first = Table.newBuilder().putEntries("a", 1).build()
        val second = Table.newBuilder().putEntries("a", 2).build()
        first.updateTheirTable(second).entries shouldBe mapOf("a" to 2)
    }
})
