package br.unifor.costify.infra.transaction;

import br.unifor.costify.application.contracts.TransactionManager;
import br.unifor.costify.application.contracts.TransactionalOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Spring-based implementation of TransactionManager.
 * This adapter allows the application layer to use transactions without
 * depending on Spring's @Transactional annotation.
 *
 * This implementation:
 * - Uses Spring's TransactionTemplate for programmatic transaction management
 * - Wraps checked exceptions in RuntimeException
 * - Logs transaction lifecycle events
 * - Follows the same isolation and propagation rules as @Transactional
 */
@Component
public class SpringTransactionManager implements TransactionManager {
  private static final Logger logger = LoggerFactory.getLogger(SpringTransactionManager.class);
  private final TransactionTemplate transactionTemplate;

  public SpringTransactionManager(PlatformTransactionManager platformTransactionManager) {
    this.transactionTemplate = new TransactionTemplate(platformTransactionManager);

    // Configure transaction template with same defaults as @Transactional
    // PROPAGATION_REQUIRED - Join existing transaction or create new one
    // ISOLATION_DEFAULT - Use database default isolation level
    // Read/write transaction (not read-only)
    // Timeout: default (no timeout)
  }

  @Override
  public <T> T executeInTransaction(TransactionalOperation<T> operation) {
    logger.debug("Starting transaction execution");

    try {
      T result = transactionTemplate.execute(status -> {
        try {
          logger.trace("Executing operation within transaction");
          return operation.execute();
        } catch (RuntimeException e) {
          // RuntimeExceptions are thrown as-is (don't wrap)
          logger.error("Operation failed with runtime exception, marking for rollback", e);
          status.setRollbackOnly();
          throw e;
        } catch (Exception e) {
          // Checked exceptions are wrapped
          logger.error("Operation failed with checked exception, marking for rollback", e);
          status.setRollbackOnly();
          throw new RuntimeException("Transaction operation failed", e);
        }
      });

      logger.debug("Transaction completed successfully");
      return result;

    } catch (RuntimeException e) {
      logger.error("Transaction execution failed", e);
      throw e;
    }
  }
}
