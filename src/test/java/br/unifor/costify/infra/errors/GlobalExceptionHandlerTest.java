package br.unifor.costify.infra.errors;

import br.unifor.costify.application.errors.ApplicationException;
import br.unifor.costify.application.errors.ApplicationErrorCode;
import br.unifor.costify.application.errors.IngredientAlreadyExistsException;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.application.errors.RecipeAlreadyExistsException;
import br.unifor.costify.domain.errors.DomainException;
import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.ingredient.InvalidIngredientNameException;
import br.unifor.costify.domain.errors.money.NegativeMoneyException;
import br.unifor.costify.domain.errors.recipe.EmptyRecipeException;
import br.unifor.costify.domain.errors.recipe.InvalidQuantityException;
import br.unifor.costify.domain.errors.recipe.InvalidTotalCostException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler to ensure proper error response structure
 * and HTTP status code mapping for all exception types with layer-based error codes.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ====================================
    // Application Layer Exception Tests
    // ====================================

    @Test
    void shouldHandleIngredientNotFoundException() {
        IngredientNotFoundException exception = IngredientNotFoundException.withId("test-id");

        ResponseEntity<ErrorResponse> response = handler.handleIngredientNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("Not Found", body.error());
        assertEquals("APP-100", body.code());
        assertEquals("Ingredient with ID 'test-id' not found", body.message());
        assertEquals("/api/test", body.path());
    }

    @Test
    void shouldHandleIngredientAlreadyExistsException() {
        IngredientAlreadyExistsException exception = IngredientAlreadyExistsException.withName("duplicate");

        ResponseEntity<ErrorResponse> response = handler.handleIngredientAlreadyExists(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Conflict", body.error());
        assertEquals("APP-101", body.code());
        assertEquals("Ingredient with name 'duplicate' already exists", body.message());
    }

    @Test
    void shouldHandleRecipeNotFoundException() {
        RecipeNotFoundException exception = RecipeNotFoundException.withId("recipe-id");

        ResponseEntity<ErrorResponse> response = handler.handleRecipeNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("APP-200", body.code());
        assertEquals("Recipe with ID 'recipe-id' not found", body.message());
    }

    @Test
    void shouldHandleRecipeAlreadyExistsException() {
        RecipeAlreadyExistsException exception = RecipeAlreadyExistsException.withName("duplicate-recipe");

        ResponseEntity<ErrorResponse> response = handler.handleRecipeAlreadyExists(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("APP-201", body.code());
    }

    @Test
    void shouldHandleGenericApplicationException() {
        // Use a concrete ApplicationException subclass
        IngredientNotFoundException exception = new IngredientNotFoundException("Test error");

        ResponseEntity<ErrorResponse> response = handler.handleApplicationException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("APP-100", body.code());
        assertEquals("Test error", body.message());
    }

    // ====================================
    // Domain Layer Exception Tests
    // ====================================

    @Test
    void shouldHandleInvalidIngredientNameException() {
        InvalidIngredientNameException exception = new InvalidIngredientNameException("Invalid name");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidIngredientName(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("DOMAIN-100", body.code());
        assertEquals("Invalid name", body.message());
    }

    @Test
    void shouldHandleNegativeMoneyException() {
        NegativeMoneyException exception = new NegativeMoneyException("Negative amount");

        ResponseEntity<ErrorResponse> response = handler.handleNegativeMoney(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("DOMAIN-200", body.code());
    }

    @Test
    void shouldHandleEmptyRecipeException() {
        EmptyRecipeException exception = new EmptyRecipeException("Recipe is empty");

        ResponseEntity<ErrorResponse> response = handler.handleEmptyRecipe(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("DOMAIN-300", body.code());
    }

    @Test
    void shouldHandleInvalidQuantityException() {
        InvalidQuantityException exception = new InvalidQuantityException("Invalid quantity");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidQuantity(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("DOMAIN-301", body.code());
    }

    @Test
    void shouldHandleInvalidTotalCostException() {
        InvalidTotalCostException exception = new InvalidTotalCostException("Invalid cost");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidTotalCost(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("DOMAIN-302", body.code());
    }

    @Test
    void shouldHandleGenericDomainException() {
        // Use a concrete DomainException subclass  
        InvalidIngredientNameException exception = new InvalidIngredientNameException("Domain error");

        ResponseEntity<ErrorResponse> response = handler.handleDomainException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("DOMAIN-100", body.code());
        assertEquals("Domain error", body.message());
    }

    // ====================================
    // Validation Exception Tests
    // ====================================

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("test", "field", "rejected", false, null, null, "validation failed");
        when(exception.getBindingResult()).thenReturn(mock(org.springframework.validation.BindingResult.class));
        when(exception.getBindingResult().getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INFRA-001", body.code());
        assertEquals("Request validation failed", body.message());
        assertNotNull(body.validationErrors());
        assertEquals(1, body.validationErrors().size());
    }

    @Test
    void shouldHandleBindException() {
        BindException exception = mock(BindException.class);
        FieldError fieldError = new FieldError("test", "field", "value", false, null, null, "binding failed");
        when(exception.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleBindException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INFRA-001", body.code());
        assertEquals("Request binding failed", body.message());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("field");
        when(violation.getInvalidValue()).thenReturn("invalid");
        when(violation.getMessage()).thenReturn("constraint violated");

        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", violations);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INFRA-001", body.code());
        assertEquals("Constraint validation failed", body.message());
    }

    // ====================================
    // HTTP-level Exception Tests
    // ====================================

    @Test
    void shouldHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("JSON parse error");

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INFRA-001", body.code());
        assertEquals("Invalid JSON format", body.message());
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("invalid-value");
        when(exception.getName()).thenReturn("param");

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatch(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("INFRA-001", body.code());
        assertEquals("Invalid value 'invalid-value' for parameter 'param'", body.message());
    }

    @Test
    void shouldHandleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(exception, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(405, body.status());
        assertEquals("INFRA-001", body.code());
        assertEquals("Method 'POST' not supported for this endpoint", body.message());
    }

    @Test
    void shouldHandleNoHandlerFoundException() {
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/api/unknown", null);

        ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.status());
        assertEquals("INFRA-001", body.code());
        assertEquals("Endpoint 'GET /api/unknown' not found", body.message());
    }

    // ====================================
    // Generic Exception Handler Tests
    // ====================================

    @Test
    void shouldHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.status());
        assertEquals("INFRA-000", body.code());
        assertEquals("An unexpected error occurred. Please try again later.", body.message());
    }
}