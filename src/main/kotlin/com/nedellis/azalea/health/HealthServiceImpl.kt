package com.nedellis.azalea.health

/* ktlint-disable no-wildcard-imports */
import com.google.protobuf.Empty
import com.nedellis.azalea.*
/* ktlint-enable no-wildcard-imports */

class HealthServiceImpl(val azw: AzaleaWrapper) : HealthGrpcKt.HealthCoroutineImplBase(), Logging {

    override suspend fun update(request: Table): Empty {
        azw.updateTable(request)
        return Empty.getDefaultInstance()
    }
}
