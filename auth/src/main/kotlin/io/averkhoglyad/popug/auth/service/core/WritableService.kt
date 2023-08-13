package io.averkhoglyad.popug.auth.service.core

import java.io.Serializable

interface WritableService<E, I : Serializable> : ReadOnlyService<E, I> {

    fun save(entity: E): E
    fun delete(id: I)

}