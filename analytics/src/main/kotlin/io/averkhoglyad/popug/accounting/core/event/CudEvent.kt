package io.averkhoglyad.popug.accounting.core.event

enum class CudEvent {
    CREATED,
    UPDATED,
    DELETED,
    UNKNOWN;

    companion object {
        fun parse(value: String): CudEvent {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                UNKNOWN
            }
        }
    }
}
