package com.nedellis.azalea.health

import com.google.protobuf.Empty
import com.nedellis.azalea.Azalea
import com.nedellis.azalea.AzaleaWrapper
import com.nedellis.azalea.HealthGrpcKt
import com.nedellis.azalea.Table

class HealthServiceImpl(val azw: AzaleaWrapper) : HealthGrpcKt.HealthCoroutineImplBase() {

    override suspend fun update(request: Table): Empty {
        azw.updateTable(request)
        return Empty.getDefaultInstance()
    }
}
