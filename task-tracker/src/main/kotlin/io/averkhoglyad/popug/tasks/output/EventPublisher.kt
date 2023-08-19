package io.averkhoglyad.popug.tasks.output

interface EventPublisher<E> {

    fun emit(event: E)

}