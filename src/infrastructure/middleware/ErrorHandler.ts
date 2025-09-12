import { Elysia } from 'elysia'
import { DomainException } from '@domain/errors/DomainException.js'
import { ApplicationException } from '@application/errors/ApplicationException.js'

export const errorHandler = new Elysia()
  .error({
    DOMAIN_ERROR: DomainException,
    APPLICATION_ERROR: ApplicationException,
    VALIDATION_ERROR: Error,
    INTERNAL_ERROR: Error,
  })
  .onError({ as: 'global' }, ({ code, error, set }) => {
    console.error('Error occurred:', error)

    switch (code) {
      case 'DOMAIN_ERROR': {
        const domainError = error as DomainException
        set.status = 400
        return {
          success: false,
          error: {
            code: domainError.errorCode,
            message: domainError.message,
            type: 'domain_error',
          },
        }
      }

      case 'APPLICATION_ERROR': {
        const appError = error as ApplicationException
        set.status = 404
        return {
          success: false,
          error: {
            code: appError.errorCode,
            message: appError.message,
            type: 'application_error',
          },
        }
      }

      case 'VALIDATION': {
        set.status = 400
        return {
          success: false,
          error: {
            code: 'VALIDATION_ERROR',
            message: 'Invalid request data',
            details: error.message,
            type: 'validation_error',
          },
        }
      }

      case 'NOT_FOUND': {
        set.status = 404
        return {
          success: false,
          error: {
            code: 'NOT_FOUND',
            message: 'Resource not found',
            type: 'not_found',
          },
        }
      }

      default: {
        set.status = 500
        return {
          success: false,
          error: {
            code: 'INTERNAL_ERROR',
            message: 'An unexpected error occurred',
            type: 'internal_error',
          },
        }
      }
    }
  })
