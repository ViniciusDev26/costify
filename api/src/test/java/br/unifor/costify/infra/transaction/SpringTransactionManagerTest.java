package br.unifor.costify.infra.transaction;

import br.unifor.costify.application.contracts.TransactionalOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpringTransactionManagerTest {

  private PlatformTransactionManager platformTransactionManager;
  private SpringTransactionManager transactionManager;

  @BeforeEach
  void setUp() {
    platformTransactionManager = mock(PlatformTransactionManager.class);
    transactionManager = new SpringTransactionManager(platformTransactionManager);

    // Mock transaction lifecycle
    TransactionStatus mockStatus = new SimpleTransactionStatus();
    when(platformTransactionManager.getTransaction(any())).thenReturn(mockStatus);
  }

  @Test
  void shouldExecuteOperationSuccessfully() {
    // Arrange
    TransactionalOperation<String> operation = () -> "success";

    // Act
    String result = transactionManager.executeInTransaction(operation);

    // Assert
    assertEquals("success", result);
    verify(platformTransactionManager).getTransaction(any());
    verify(platformTransactionManager).commit(any());
  }

  @Test
  void shouldRollbackOnRuntimeException() {
    // Arrange
    TransactionalOperation<String> operation = () -> {
      throw new RuntimeException("Operation failed");
    };

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      transactionManager.executeInTransaction(operation);
    });

    assertEquals("Operation failed", exception.getMessage());
    verify(platformTransactionManager).getTransaction(any());
    verify(platformTransactionManager).rollback(any());
    verify(platformTransactionManager, never()).commit(any());
  }

  @Test
  void shouldWrapCheckedExceptions() {
    // Arrange
    TransactionalOperation<String> operation = () -> {
      throw new Exception("Checked exception");
    };

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      transactionManager.executeInTransaction(operation);
    });

    assertEquals("Transaction operation failed", exception.getMessage());
    assertEquals("Checked exception", exception.getCause().getMessage());
  }

  @Test
  void shouldReturnCorrectValue() {
    // Arrange
    Integer expectedValue = 42;
    TransactionalOperation<Integer> operation = () -> expectedValue;

    // Act
    Integer result = transactionManager.executeInTransaction(operation);

    // Assert
    assertEquals(expectedValue, result);
  }

  @Test
  void shouldHandleNullReturn() {
    // Arrange
    TransactionalOperation<String> operation = () -> null;

    // Act
    String result = transactionManager.executeInTransaction(operation);

    // Assert
    assertNull(result);
    verify(platformTransactionManager).commit(any());
  }
}
