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

    @ExperimentalTime
    val failInterval = config.getLong("azalea.fail-interval-ms").toDuration(TimeUnit.MILLISECONDS)

    @ExperimentalTime
    val updateTableTimeout = config.getLong("azalea.update-table-timeout-ms").toDuration(TimeUnit.MILLISECONDS)

    val isStrict = config.getBoolean("azalea.strict")
}
