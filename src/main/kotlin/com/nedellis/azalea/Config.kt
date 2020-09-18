package com.nedellis.azalea

import com.typesafe.config.Config
import java.net.URI

class Config(config: Config) {
    val redisURI = URI(config.getString(""))
}
