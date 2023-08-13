package io.averkhoglyad.popug.auth.endpoint

import io.averkhoglyad.popug.auth.data.ExceptionResponse
import io.averkhoglyad.popug.auth.util.log4j
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: MissingServletRequestParameterException): ExceptionResponse {
        logger.warn("Param ${e.parameterName} is required", e)
        return ExceptionResponse("param.required", e.parameterName)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: HttpMediaTypeNotSupportedException): ExceptionResponse {
        logger.warn("Invalid content type ${e.contentType}", e)
        return ExceptionResponse("content.type.invalid", e.contentType!!)
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(e: HttpMessageNotReadableException): ExceptionResponse {
        logger.warn("Invalid json", e)
        return ExceptionResponse("invalid.json.body")
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleException(e: HttpRequestMethodNotSupportedException): ExceptionResponse {
        logger.warn("HttpStatus.METHOD_NOT_ALLOWED: ", e)
        return ExceptionResponse("method.not.allowed")
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: IllegalArgumentException): ExceptionResponse {
        logger.warn("Illegal argument", e)
        return ExceptionResponse("illegal.argument")
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: EntityNotFoundException): ExceptionResponse {
        logger.warn("Entity not found: ", e)
        return ExceptionResponse("entity.not.found")
    }

    @ExceptionHandler(Throwable::class)
    fun handleAnyUncaughtException(e: Exception): ResponseEntity<ExceptionResponse> {
        logger.error("Generic exception caught:", e)
        val responseStatus = e::class.findAnnotation<ResponseStatus>()
        return createErrorResponseEntity(e, responseStatus)
    }

    private fun createErrorResponseEntity(exception: Exception,
                                          responseStatus: ResponseStatus?): ResponseEntity<ExceptionResponse> {
        if (responseStatus == null) {
            return ResponseEntity(
                ExceptionResponse(exception.message ?: "internal.server.error"),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
        val status: HttpStatus = responseStatus.code
            .takeUnless { it == HttpStatus.INTERNAL_SERVER_ERROR }
            ?: responseStatus.value

        val message = (exception.message ?: responseStatus.reason)
            .takeUnless { it.isBlank() }
            ?: "internal.server.error"
        return ResponseEntity(ExceptionResponse(message), status)
    }
}
