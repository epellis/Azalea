package com.nedellis.azalea

import com.typesafe.config.Config
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

class Config(config: Config) {
    val redisURI = URI(config.getString("azalea.redis-uri"))

    @ExperimentalTime
    val heartbeatInterval = config.getLong("azalea.heartbeat-interval-ms").toDuration(TimeUnit.MILLISECONDS)
}
