package br.unifor.costify.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

import br.unifor.costify.domain.errors.money.NegativeMoneyException;

class MoneyTest {

    @Test
    void shouldCreateMoneyFromBigDecimal() {
        BigDecimal amount = new BigDecimal("10.50");
        Money money = Money.of(amount);
        
        assertNotNull(money);
        assertEquals(amount, money.getAmount());
    }

    @Test
    void shouldCreateMoneyFromDouble() {
        double amount = 10.50;
        Money money = Money.of(amount);
        
        assertNotNull(money);
        assertEquals(new BigDecimal("10.50"), money.getAmount());
    }

    @Test
    void shouldCreateZeroMoney() {
        Money zero = Money.zero();
        
        assertNotNull(zero);
        assertEquals(new BigDecimal("0.00"), zero.getAmount());
    }

    @Test
    void shouldAddMoney() {
        Money money1 = Money.of(new BigDecimal("10.50"));
        Money money2 = Money.of(new BigDecimal("5.25"));
        
        Money result = money1.add(money2);
        
        assertEquals(new BigDecimal("15.75"), result.getAmount());
    }

    @Test
    void shouldSubtractMoney() {
        Money money1 = Money.of(new BigDecimal("10.50"));
        Money money2 = Money.of(new BigDecimal("3.25"));
        
        Money result = money1.subtract(money2);
        
        assertEquals(new BigDecimal("7.25"), result.getAmount());
    }

    @Test
    void shouldMultiplyByDouble() {
        Money money = Money.of(new BigDecimal("10.00"));
        
        Money result = money.multiply(2.5);
        
        assertEquals(new BigDecimal("25.00"), result.getAmount());
    }

    @Test
    void shouldMultiplyByBigDecimal() {
        Money money = Money.of(new BigDecimal("10.00"));
        BigDecimal multiplier = new BigDecimal("2.5");
        
        Money result = money.multiply(multiplier);
        
        assertEquals(new BigDecimal("25.00"), result.getAmount());
    }

    @Test
    void shouldDivideByDouble() {
        Money money = Money.of(new BigDecimal("10.00"));
        
        Money result = money.divide(2.0);
        
        assertEquals(new BigDecimal("5.00"), result.getAmount());
    }

    @Test
    void shouldCheckIfGreaterThan() {
        Money money1 = Money.of(new BigDecimal("10.50"));
        Money money2 = Money.of(new BigDecimal("5.25"));
        
        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
    }

    @Test
    void shouldCheckIfLessThan() {
        Money money1 = Money.of(new BigDecimal("5.25"));
        Money money2 = Money.of(new BigDecimal("10.50"));
        
        assertTrue(money1.isLessThan(money2));
        assertFalse(money2.isLessThan(money1));
    }

    @Test
    void shouldCheckIfEqual() {
        Money money1 = Money.of(new BigDecimal("10.50"));
        Money money2 = Money.of(new BigDecimal("10.50"));
        Money money3 = Money.of(new BigDecimal("5.25"));
        
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
    }

    @Test
    void shouldHaveProperHashCode() {
        Money money1 = Money.of(new BigDecimal("10.50"));
        Money money2 = Money.of(new BigDecimal("10.50"));
        
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void shouldConvertToDouble() {
        Money money = Money.of(new BigDecimal("10.50"));
        
        assertEquals(10.50, money.doubleValue(), 0.001);
    }

    @Test
    void shouldFormatToString() {
        Money money = Money.of(new BigDecimal("10.50"));
        
        String formatted = money.toString();
        
        assertTrue(formatted.contains("10.50"));
    }

    @Test
    void shouldThrowExceptionForNullAmount() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Money.of((BigDecimal) null)
        );
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        NegativeMoneyException exception = assertThrows(
            NegativeMoneyException.class,
            () -> Money.of(new BigDecimal("-1.00"))
        );
        
        assertEquals("Money cannot be negative in business context", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddingNull() {
        Money money = Money.of(new BigDecimal("10.00"));
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> money.add(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSubtractingNull() {
        Money money = Money.of(new BigDecimal("10.00"));
        
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> money.subtract(null)
        );
    }

    @Test
    void shouldThrowExceptionForNegativeResult() {
        Money money1 = Money.of(new BigDecimal("5.00"));
        Money money2 = Money.of(new BigDecimal("10.00"));
        
        NegativeMoneyException exception = assertThrows(
            NegativeMoneyException.class,
            () -> money1.subtract(money2)
        );
        
        assertEquals("Money cannot be negative in business context", exception.getMessage());
    }

    @Test
    void shouldHandlePrecisionInOperations() {
        Money money1 = Money.of(new BigDecimal("10.123"));
        Money money2 = Money.of(new BigDecimal("5.456"));
        
        Money result = money1.add(money2);
        
        // Should handle precision properly
        assertEquals(new BigDecimal("15.58"), result.getAmount()); // Rounded to 2 decimal places
    }
}