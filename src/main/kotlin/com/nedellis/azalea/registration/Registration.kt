package com.nedellis.azalea.registration

import redis.clients.jedis.Jedis
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.URI

/**
 * Register this instance to the cluster and return a list of all other members
 */
fun register(address: URI, redisURI: URI): List<URI> {
    val jedis = Jedis(redisURI)
    jedis.sadd("members", address.toString())
    return jedis.smembers("members").map { URI(it) }.filter { it != address }
}

/**
 * Return the externally facing local address
 */
fun localAddress(): String {
    val socket = DatagramSocket()
    socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
    return socket.localAddress.hostAddress
}
