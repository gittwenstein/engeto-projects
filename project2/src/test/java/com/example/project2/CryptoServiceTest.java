package com.example.project2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CryptoServiceTest {
	private CryptoService cryptoService;

	@BeforeEach
	void setUp() {
		cryptoService = new CryptoService();
	}

	@Test
	void addCryptoAssignsIdAndStoresCrypto() {
		Crypto created = cryptoService.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));

		assertEquals(1, created.getId());
		assertEquals("Bitcoin", created.getName());
		assertTrue(cryptoService.getCryptoById(1).isPresent());
	}

	@Test
	void getAllCryptosSortsByNamePriceAndQuantity() {
		cryptoService.addCrypto(new Crypto(null, "Ethereum", "ETH", 3200.0, 2.0));
		cryptoService.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));
		cryptoService.addCrypto(new Crypto(null, "Solana", "SOL", 180.0, 15.0));

		List<Crypto> byName = cryptoService.getAllCryptos("name");
		assertEquals(List.of("Bitcoin", "Ethereum", "Solana"), byName.stream().map(Crypto::getName).toList());

		List<Crypto> byPrice = cryptoService.getAllCryptos("price");
		assertEquals(List.of("Solana", "Ethereum", "Bitcoin"), byPrice.stream().map(Crypto::getName).toList());

		List<Crypto> byQuantity = cryptoService.getAllCryptos("quantity");
		assertEquals(List.of("Bitcoin", "Ethereum", "Solana"), byQuantity.stream().map(Crypto::getName).toList());
	}

	@Test
	void updateCryptoReplacesExistingValues() {
		cryptoService.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));

		var updated = cryptoService.updateCrypto(1, new Crypto(null, "Bitcoin", "BTC", 70000.0, 2.0));

		assertTrue(updated.isPresent());
		assertEquals(70000.0, updated.get().getPrice());
		assertEquals(2.0, updated.get().getQuantity());
	}

	@Test
	void portfolioValueSumsPriceTimesQuantity() {
		cryptoService.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));
		cryptoService.addCrypto(new Crypto(null, "Ethereum", "ETH", 3200.0, 2.0));

		assertEquals(103900.0, cryptoService.getPortfolioValue());
		assertFalse(cryptoService.getAllCryptos(null).isEmpty());
	}

	@Test
	void returnedCryptosAreDefensiveCopies() {
		cryptoService.addCrypto(new Crypto(null, "Bitcoin", "BTC", 65000.0, 1.5));

		Crypto returned = cryptoService.getCryptoById(1).orElseThrow();
		returned.setName("Changed");
		returned.setPrice(1.0);

		Crypto stored = cryptoService.getCryptoById(1).orElseThrow();

		assertEquals("Bitcoin", stored.getName());
		assertEquals(65000.0, stored.getPrice());
	}
}

