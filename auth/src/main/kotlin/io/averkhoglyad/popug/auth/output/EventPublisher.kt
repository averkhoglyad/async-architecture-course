package io.averkhoglyad.popug.auth.output

interface EventPublisher<E> {

    fun emit(event: E)

}