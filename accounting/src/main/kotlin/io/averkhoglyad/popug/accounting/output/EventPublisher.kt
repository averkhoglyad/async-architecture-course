package io.averkhoglyad.popug.accounting.output

interface EventPublisher<E> {

    fun emit(event: E)

}