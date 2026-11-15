package com.gestion_motocicletas.rest.exception;

import com.gestion_motocicletas.shared.exception.MotocicletaInvalidaException;
import com.gestion_motocicletas.shared.exception.MotocicletaNoEncontradaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MotocicletaNoEncontradaException.class)
    public ResponseEntity<ErrorMessage> motocicletaNoEncontrada(
            MotocicletaNoEncontradaException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MotocicletaInvalidaException.class)
    public ResponseEntity<ErrorMessage> motocicletaInvalida(
            MotocicletaInvalidaException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> datosInvalidos(
            MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> cuerpoIlegible(
            HttpMessageNotReadableException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es válido o tiene un formato incorrecto", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> idInvalido(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "El ID debe ser un número entero", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> metodoNoPermitido(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP no permitido", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessage> tipoContenidoNoSoportado(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de contenido no soportado. Use application/json", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessage> recursoNoEncontrado(
            NoResourceFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND,
                "El recurso solicitado no existe", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> errorInterno(Exception ex, WebRequest request) {
        log.error("Error no controlado en la API REST", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor", request);
    }

    private ResponseEntity<ErrorMessage> buildResponse(
            HttpStatus status, String message, WebRequest request) {

        ErrorMessage errorMessage = new ErrorMessage(
                status.value(),
                new Date(),
                message,
                request.getDescription(false));

        return ResponseEntity.status(status).body(errorMessage);
    }
}
