package com.nedellis.azalea

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.net.URI

class AzaleaTest : StringSpec({
    val address = URI("gproto+http://127.0.0.1:5000/")
    val redisURI = URI("http://redis:6379")
    val defaultTable = Table.newBuilder().putEntries(address.toString(), 0).build()
    val defaultAzalea = Azalea(address, defaultTable)
    val config = mockk<Config>()

    every { config.redisURI } returns redisURI

    "Update Table" {
        defaultAzalea.updateTable(Table.getDefaultInstance()).table.entriesMap shouldBe defaultTable.entriesMap
        defaultAzalea.updateTable(
            Table.newBuilder().putEntries(address.toString(), 1).build()
        ).table.entriesMap shouldBe mapOf(address.toString() to 1)
    }
})
