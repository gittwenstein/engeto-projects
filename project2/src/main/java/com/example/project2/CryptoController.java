package com.example.project2;

import java.util.List;
import java.util.Locale;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CryptoController {
    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/cryptos")
    public ResponseEntity<Crypto> addCrypto(@Valid @RequestBody Crypto crypto) {
        Crypto createdCrypto = cryptoService.addCrypto(crypto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCrypto);
    }

    @GetMapping("/cryptos")
    public List<Crypto> getCryptos(@RequestParam(required = false) String sort) {
        if (sort != null && !sort.isBlank()) {
            String normalizedSort = sort.toLowerCase(Locale.ROOT);
            if (!normalizedSort.equals("name") && !normalizedSort.equals("price") && !normalizedSort.equals("quantity")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported sort value: " + sort);
            }
        }

        return cryptoService.getAllCryptos(sort);
    }

    @GetMapping("/cryptos/{id}")
    public Crypto getCryptoById(@PathVariable Integer id) {
        return cryptoService.getCryptoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crypto not found with id " + id));
    }

    @PutMapping("/cryptos/{id}")
    public Crypto updateCrypto(@PathVariable Integer id, @Valid @RequestBody Crypto crypto) {
        return cryptoService.updateCrypto(id, crypto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crypto not found with id " + id));
    }

    @GetMapping("/portfolio-value")
    public Double getPortfolioValue() {
        return cryptoService.getPortfolioValue();
    }
}

