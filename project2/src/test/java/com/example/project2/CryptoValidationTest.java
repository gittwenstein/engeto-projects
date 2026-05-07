package com.example.project2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CryptoValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

    @Test
    void validCryptoHasNoViolations() {
        Crypto crypto = new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5);

        Set<ConstraintViolation<Crypto>> violations = validator.validate(crypto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankNameAndNegativeNumbersAreRejected() {
        Crypto crypto = new Crypto(null, "", " ", -1.0, -5.0);

        Set<ConstraintViolation<Crypto>> violations = validator.validate(crypto);

        assertEquals(4, violations.size());
    }
}

