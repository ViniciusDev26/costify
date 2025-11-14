package br.unifor.costify.application.contracts;

/**
 * Represents a transactional operation that can be executed within a transaction boundary.
 * This is a functional interface that enables lambda expressions for transaction execution.
 *
 * @param <T> The type of result returned by the operation
 */
@FunctionalInterface
public interface TransactionalOperation<T> {
  /**
   * Executes the transactional operation.
   *
   * @return The result of the operation
   * @throws Exception if the operation fails
   */
  T execute() throws Exception;
}
