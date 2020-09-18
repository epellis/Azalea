package com.nedellis.azalea

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.net.URI

class AzaleaTest : StringSpec({
    val address = URI("gproto+http://127.0.0.1:5000/")
    val redisURI = URI("http://redis:6379")
    val config = mockk<Config>()

    every { config.redisURI } returns redisURI


    "Init" {
        val az = AzaleaWrapper(config).init(address)
        az.localAddress shouldBe address
        az.table.entries shouldBe mapOf(address.toString() to 0)
    }
})
