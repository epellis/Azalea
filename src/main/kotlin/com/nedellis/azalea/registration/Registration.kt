package com.nedellis.azalea.registration

import redis.clients.jedis.Jedis
import java.net.URI

/**
 * Register this instance to the cluster and return a list of all other members
 */
fun register(address: URI, redisURI: URI): List<URI> {
    val jedis = Jedis(redisURI)
    jedis.sadd("members", address.toString())
    return jedis.smembers("members").map { URI(it) }
}
