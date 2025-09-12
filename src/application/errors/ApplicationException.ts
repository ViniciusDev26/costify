export abstract class ApplicationException extends Error {
  abstract readonly errorCode: string

  constructor(message: string, cause?: Error) {
    super(message)
    this.name = this.constructor.name
    this.cause = cause

    // Maintains proper stack trace for where error was thrown (Node.js only)
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, this.constructor)
    }
  }
}
