package com.nedellis.azalea

import com.nedellis.azalea.health.copy
import kotlinx.coroutines.sync.Mutex
import java.net.URI

class AzaleaWrapper(private val config: Config) {
    private lateinit var azalea: Azalea
    private val lock = Mutex()

    /**
     * Initialize an Azalea instance and allow it to be used
     */
    fun init(localAddress: URI): Azalea {
        azalea = Azalea(localAddress, Table.newBuilder().putEntries(localAddress.toString(), 0).build())
        return azalea.copy()
    }
}

data class Azalea(val localAddress: URI, val table: Table) {
    fun copy(): Azalea {
        return Azalea(localAddress, table.copy())
    }
}
