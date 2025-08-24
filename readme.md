# Costify

Costify is a Java platform to calculate product costs from recipes. Each ingredient has quantity, unit, and price, allowing the real cost of the recipe to be computed. Built with Clean Architecture and Spring Boot.

---

## Features

* Domain modeling with Value Objects (`Id`, `Unit`) and entities (`Ingredient`)
* Abstract ID generation via `IdGenerator`
* Data validation in the domain
* Cost calculation per unit
* Unit tested using JUnit 5 and Java asserts

---

## Technology Stack

* Java 24
* Spring Boot 3
* Maven
* Clean Architecture / Hexagonal Architecture
* JUnit 5

---

## Getting Started

### Prerequisites

* Java 24
* Maven 4.x or higher

### Running the Project

Clone the repository:

```
git clone https://github.com/your-username/costify.git
cd costify
```

Build and run:

```
./mvnw clean install
./mvnw spring-boot:run
```

### Running Tests

```
./mvnw test -DargLine="-ea"
```

This enables Java assertions for domain validation tests.

---

## Project Structure

```
src/
 ├─ main/
 │   ├─ java/
 │   │   └─ br.unifor.costify.domain/       # Domain entities and value objects
 │   │   └─ br.unifor.costify.application/  # Application layer
 │   │   └─ br.unifor.costify.infrastructure/ # Infrastructure (ID generator, persistence)
 └─ test/
     └─ java/
         └─ br.unifor.costify.domain/       # Unit tests for domain classes
```

---

## License

MIT License © 2025
