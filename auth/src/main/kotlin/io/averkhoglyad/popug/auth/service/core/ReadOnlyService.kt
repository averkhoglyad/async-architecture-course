package io.averkhoglyad.popug.auth.service.core

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.Serializable

interface ReadOnlyService<E, I : Serializable> {

    fun findAll(): List<E>
    fun findList(pageable: Pageable): Page<E>

    @Throws(EntityNotFoundException::class)
    fun findEntity(id: I): E

}