package challenge.news_management_system.`interface`.rest

import challenge.news_management_system.`interface`.model.RestApiError
import challenge.news_management_system.application.exception.ResourceAlreadyExistException
import challenge.news_management_system.application.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ResponseEntity<RestApiError> {
        val error = RestApiError(message = exception.message ?: "")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(ResourceAlreadyExistException::class)
    fun handleResourceAlreadyExistException(exception: ResourceAlreadyExistException): ResponseEntity<RestApiError> {
        val error = RestApiError(message = exception.message ?: "")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
}
