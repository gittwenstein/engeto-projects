package com.example.project2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CryptoControllerTest {
    private CryptoController createController() {
        return new CryptoController(new CryptoService());
    }

    @Test
    void addCryptoReturnsCreatedResource() {
        CryptoController cryptoController = createController();
        var response = cryptoController.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));
        Crypto body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertEquals(1, body.getId());
        assertEquals("Bitcoin", body.getName());
    }

    @Test
    void getCryptosReturnsSortedList() {
        CryptoController cryptoController = createController();
        cryptoController.addCrypto(new Crypto(null, "Ethereum", "ETH", 3200.0, 2.0));
        cryptoController.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));
        cryptoController.addCrypto(new Crypto(null, "Solana", "SOL", 180.0, 15.0));

        List<Crypto> cryptosByName = cryptoController.getCryptos("name");

        assertEquals(List.of("Bitcoin", "Ethereum", "Solana"), cryptosByName.stream().map(Crypto::getName).toList());
    }

    @Test
    void getCryptoByIdReturnsDetail() {
        CryptoController cryptoController = createController();
        cryptoController.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));

        Crypto crypto = cryptoController.getCryptoById(1);

        assertEquals("BTC", crypto.getSymbol());
    }

    @Test
    void putCryptoUpdatesResource() {
        CryptoController cryptoController = createController();
        cryptoController.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));

        Crypto updated = cryptoController.updateCrypto(1, new Crypto(null, "Bitcoin", "BTC", 70000.0, 2.0));

        assertEquals(70000.0, updated.getPrice());
        assertEquals(2.0, updated.getQuantity());
    }

    @Test
    void getPortfolioValueReturnsTotal() {
        CryptoController cryptoController = createController();
        cryptoController.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));
        cryptoController.addCrypto(new Crypto(null, "Ethereum", "ETH", 3200.0, 2.0));

        assertEquals(103900.0, cryptoController.getPortfolioValue());
    }

    @Test
    void invalidSortThrowsBadRequest() {
        CryptoController cryptoController = createController();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cryptoController.getCryptos("unknown"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void missingCryptoThrowsNotFound() {
        CryptoController cryptoController = createController();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cryptoController.getCryptoById(99));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void missingCryptoOnUpdateThrowsNotFound() {
        CryptoController cryptoController = createController();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> cryptoController.updateCrypto(99, new Crypto(null, "Bitcoin", "BTC", 70000.0, 2.0)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}




