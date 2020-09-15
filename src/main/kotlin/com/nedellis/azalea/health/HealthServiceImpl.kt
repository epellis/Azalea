package com.nedellis.azalea.health

import com.google.protobuf.Empty
import com.nedellis.azalea.Azalea
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Providable
import com.nedellis.azalea.Table
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Main")

class HealthServiceImpl : HealthGrpcKt.HealthCoroutineImplBase(), Providable {
    override lateinit var parent: Azalea

    override suspend fun update(table: Table): Empty {
        parent.updateOurTable(table)
        return Empty.getDefaultInstance()
    }
}
