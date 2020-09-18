package com.nedellis.azalea.health

import com.google.protobuf.Empty
import com.nedellis.azalea.*

class HealthServiceImpl(val azw: AzaleaWrapper) : HealthGrpcKt.HealthCoroutineImplBase(), Logging {

    override suspend fun update(request: Table): Empty {
        azw.updateTable(request)
        return Empty.getDefaultInstance()
    }
}
