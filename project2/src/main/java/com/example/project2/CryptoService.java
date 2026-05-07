package com.example.project2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class CryptoService {
    private final List<Crypto> cryptos = new ArrayList<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public synchronized Crypto addCrypto(Crypto crypto) {
        validateCrypto(crypto);

        Crypto storedCrypto = copyOf(crypto);
        storedCrypto.setId(nextId.getAndIncrement());
        cryptos.add(storedCrypto);
        return copyOf(storedCrypto);
    }

    public synchronized List<Crypto> getAllCryptos(String sort) {
        List<Crypto> result = cryptos.stream()
                .map(this::copyOf)
                .toList();

        if (sort == null || sort.isBlank()) {
            return new ArrayList<>(result);
        }

        Comparator<Crypto> comparator = switch (sort.toLowerCase(Locale.ROOT)) {
            case "name" -> Comparator.comparing(
                    Crypto::getName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
            case "price" -> Comparator.comparing(
                    Crypto::getPrice,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "quantity" -> Comparator.comparing(
                    Crypto::getQuantity,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            default -> throw new IllegalArgumentException("Unsupported sort key: " + sort);
        };

        return result.stream()
                .sorted(comparator)
                .toList();
    }

    public synchronized Optional<Crypto> getCryptoById(Integer id) {
        return cryptos.stream()
                .filter(crypto -> crypto.getId().equals(id))
                .findFirst()
                .map(this::copyOf);
    }

    public synchronized Optional<Crypto> updateCrypto(Integer id, Crypto crypto) {
        validateCrypto(crypto);

        for (int i = 0; i < cryptos.size(); i++) {
            if (cryptos.get(i).getId().equals(id)) {
                Crypto updatedCrypto = copyOf(crypto);
                updatedCrypto.setId(id);
                cryptos.set(i, updatedCrypto);
                return Optional.of(copyOf(updatedCrypto));
            }
        }

        return Optional.empty();
    }

    public synchronized double getPortfolioValue() {
        return cryptos.stream()
                .mapToDouble(crypto -> crypto.getPrice() * crypto.getQuantity())
                .sum();
    }

    private void validateCrypto(Crypto crypto) {
        if (crypto == null) {
            throw new IllegalArgumentException("Crypto payload is required");
        }
        if (crypto.getName() == null || crypto.getName().isBlank()) {
            throw new IllegalArgumentException("Crypto name is required");
        }
        if (crypto.getSymbol() == null || crypto.getSymbol().isBlank()) {
            throw new IllegalArgumentException("Crypto symbol is required");
        }
        if (crypto.getPrice() == null || crypto.getPrice() < 0) {
            throw new IllegalArgumentException("Crypto price must be non-negative");
        }
        if (crypto.getQuantity() == null || crypto.getQuantity() < 0) {
            throw new IllegalArgumentException("Crypto quantity must be non-negative");
        }
    }

    private Crypto copyOf(Crypto source) {
        return new Crypto(
                source.getId(),
                source.getName(),
                source.getSymbol(),
                source.getPrice(),
                source.getQuantity()
        );
    }
}

