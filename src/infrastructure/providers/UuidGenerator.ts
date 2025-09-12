import { v4 as uuidv4 } from 'uuid'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'

export class UuidGenerator implements IdGenerator {
  generate(): string {
    return uuidv4()
  }
}