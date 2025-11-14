package br.unifor.costify.application.contracts;

/**
 * Manages transaction boundaries for application use cases.
 * This abstraction allows the application layer to remain independent of
 * any specific transaction management framework (Spring, JTA, etc.).
 *
 * Implementations should:
 * - Start a transaction before executing the operation
 * - Commit the transaction if the operation succeeds
 * - Rollback the transaction if the operation fails
 * - Propagate any exceptions thrown by the operation
 */
public interface TransactionManager {
  /**
   * Executes an operation within a transaction boundary.
   * If the operation completes successfully, the transaction is committed.
   * If the operation throws an exception, the transaction is rolled back.
   *
   * @param operation The operation to execute within the transaction
   * @param <T> The type of result returned by the operation
   * @return The result of the operation
   * @throws RuntimeException if the operation fails (wraps checked exceptions)
   */
  <T> T executeInTransaction(TransactionalOperation<T> operation);
}
