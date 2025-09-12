export class Id {
  private readonly value: string

  constructor(value: string) {
    if (!value || value.trim().length === 0) {
      throw new Error('Id cannot be empty')
    }
    this.value = value.trim()
  }

  getValue(): string {
    return this.value
  }

  equals(other: Id): boolean {
    if (!(other instanceof Id)) {
      return false
    }
    return this.value === other.value
  }

  toString(): string {
    return this.value
  }
}
