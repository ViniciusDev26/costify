import type { IdGenerator } from '@domain/contracts/IdGenerator.js'

export class UuidGenerator implements IdGenerator {
  generate(): string {
    return crypto.randomUUID()
  }
}
