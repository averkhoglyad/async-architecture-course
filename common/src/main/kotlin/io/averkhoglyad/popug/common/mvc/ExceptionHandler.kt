package io.averkhoglyad.popug.common.mvc

import io.averkhoglyad.popug.common.log4j
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.reflect.full.findAnnotation

@RestControllerAdvice
class ExceptionHandler {

    private val logger by log4j()

    @ExceptionHandler
    fun handleException(e: AccessDeniedException): ProblemDetail {
        logger.info("Access denied: {}", e.message)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.FORBIDDEN, "access.denied")
    }

    @ExceptionHandler
    fun handleException(e: MissingServletRequestParameterException): ProblemDetail {
        logger.info("Param {} is required", e.parameterName)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "param.required")
            .also { it.setProperty("parameter", e.parameterName) }
    }

    @ExceptionHandler
    fun handleException(e: HttpMediaTypeNotSupportedException): ProblemDetail {
        logger.info("Invalid content type {}", e.contentType)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "content.type.invalid")
            .also { it.setProperty("contentType", e.contentType) }
    }

    @ExceptionHandler
    fun handleException(e: HttpMessageNotReadableException): ProblemDetail {
        logger.info("Invalid body: {}", e.message)
        logger.debug(e)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "invalid.json.body")
    }


    @ExceptionHandler
    fun handleException(e: HttpRequestMethodNotSupportedException): ProblemDetail {
        logger.info("HttpStatus.METHOD_NOT_ALLOWED: {}", e.message)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "method.not.allowed")
    }

    @ExceptionHandler
    fun handleValidationException(e: IllegalArgumentException): ProblemDetail {
        logger.info("Illegal argument {}", e.message)
        logger.debug(e)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "illegal.argument")
    }

    @ExceptionHandler
    fun handleNotFoundException(e: EntityNotFoundException): ProblemDetail {
        logger.info("Entity not found: {}", e.message)
        logger.debug(e)
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.NOT_FOUND, "entity.not.found")
    }

    @ExceptionHandler(Throwable::class)
    fun handleAnyUncaughtException(e: Exception): ProblemDetail {
        logger.error("Generic exception caught:", e)
        val responseStatus = e::class.findAnnotation<ResponseStatus>()
        return createErrorResponseEntity(e, responseStatus)
    }

    private fun createErrorResponseEntity(exception: Exception, responseStatus: ResponseStatus?): ProblemDetail {
        if (responseStatus == null) {
            val message = exception.message ?: "internal.server.error"
            return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, message)
        }

        val status: HttpStatus = responseStatus.code
            .takeUnless { it == HttpStatus.INTERNAL_SERVER_ERROR }
            ?: responseStatus.value

        val message = (exception.message ?: responseStatus.reason)
            .takeUnless { it.isBlank() }
            ?: "internal.server.error"

        return ProblemDetail.forStatusAndDetail(status, message)
    }
}
