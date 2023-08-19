package io.averkhoglyad.popug.auth.service.publicid

interface PublicIdGenerator<E> {

    fun generate(entity: E): String

}