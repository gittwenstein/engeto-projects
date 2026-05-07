import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PlantManager manager = new PlantManager();

        Path input = resolveInputFile(args);

        if (input != null) {
            try {
                manager.loadFromFile(input);
                System.out.println("Načteno ze souboru: " + input.toString());
            } catch (PlantException e) {
                System.err.println("Chyba při načítání souboru: " + e.getMessage());
                System.err.println("Pokračuji s prázdným seznamem.");
            }
        } else {
            System.out.println("Nebyl nalezen vstupní soubor. Pokračuji s prázdným seznamem.");
        }

        // Print watering info for all plants
        System.out.println("\nInformace o zálivce pro všechny květiny:");
        for (Plant p : manager.getPlantsCopy()) {
            System.out.println(p.getWateringInfo());
        }

        // Add a new plant
        try {
            Plant newPlant = new Plant("Monstera deliciosa", "Velký list", LocalDate.now().minusDays(30), LocalDate.now().minusDays(3), 10);
            manager.addPlant(newPlant);
            System.out.println("\nPřidána nová květina: " + newPlant.getName());
        } catch (PlantException e) {
            System.err.println("Nelze přidat novou květinu: " + e.getMessage());
        }

        // Add 10 tulips
        try {
            for (int i = 1; i <= 10; i++) {
                Plant t = new Plant("Tulipán na prodej " + i, "Tulipán na prodej " + i, LocalDate.now(), LocalDate.now(), 14);
                manager.addPlant(t);
            }
            System.out.println("Přidáno 10 rostlin: Tulipán na prodej 1..10");
        } catch (PlantException e) {
            System.err.println("Chyba při přidávání tulipánů: " + e.getMessage());
        }

        // Remove plant at 3rd position (index 2) if it exists
        if (manager.getPlantsCopy().size() > 2) {
            Plant removed = manager.getPlant(2);
            manager.removePlant(2);
            System.out.println("Odebrána květina na indexu 2: " + removed.getName());
        }

        // Save list to file
        Path out = Paths.get("kvetiny-vystup.txt");
        try {
            manager.saveToFile(out);
            System.out.println("\nSeznam uložen do: " + out.toString());
        } catch (PlantException e) {
            System.err.println("Chyba při ukládání: " + e.getMessage());
        }

        // Reload saved file and print summary info
        try {
            PlantManager reloaded = new PlantManager();
            reloaded.loadFromFile(out);
            List<Plant> originalPlants = manager.getPlantsCopy();
            List<Plant> reloadedPlants = reloaded.getPlantsCopy();

            boolean sizeMatches = originalPlants.size() == reloadedPlants.size();
            boolean contentMatches = sizeMatches;

            if (contentMatches) {
                for (int i = 0; i < originalPlants.size(); i++) {
                    String originalLine = manager.formatForFile(originalPlants.get(i));
                    String reloadedLine = reloaded.formatForFile(reloadedPlants.get(i));
                    if (!originalLine.equals(reloadedLine)) {
                        contentMatches = false;
                        break;
                    }
                }
            }

            System.out.println("\nOpětovné načtení uloženého souboru, počet rostlin: " + reloadedPlants.size());
            System.out.println("Kontrola obsahu uloženého souboru: " + (contentMatches ? "OK" : "NESOUHLASÍ"));
        } catch (PlantException e) {
            System.err.println("Chyba při opětovném načítání uloženého souboru: " + e.getMessage());
        }

        // Test loading known test files with bad data
        String[] badFiles = new String[]{"data/kvetiny-spatne-datum.txt", "data/kvetiny-spatne-frekvence.txt"};
        for (String bf : badFiles) {
            Path p = Paths.get(bf);
            if (!p.toFile().exists()) {
                // Fallback: try parent folder
                Path alt = Paths.get("../" + bf);
                if (alt.toFile().exists()) p = alt;
            }
            if (p.toFile().exists()) {
                try {
                    PlantManager testManager = new PlantManager();
                    testManager.loadFromFile(p);
                    System.out.println("\nTestově načteno: " + p + " -> načteno " + testManager.getPlantsCopy().size() + " záznamů");
                } catch (PlantException e) {
                    System.err.println("\nChyba při načítání testovacího souboru " + p + ": " + e.getMessage());
                }
            } else {
                System.out.println("\nTestovací soubor nebyl nalezen: " + bf);
            }
        }

        // Sorting and printing
        System.out.println("\nSeřazení podle názvu (výchozí):");
        manager.sortByName();
        for (Plant p : manager.getPlantsCopy()) {
            System.out.println("  - " + p.getName());
        }

        System.out.println("\nSeřazení podle data poslední zálivky:");
        manager.sortByLastWateringDate();
        for (Plant p : manager.getPlantsCopy()) {
            System.out.println("  - " + p.getName() + " (poslední zálivka: " + p.getLastWateringDate() + ")");
        }

        // Which plants need watering
        System.out.println("\nRostliny které je třeba zalít:");
        for (Plant p : manager.getPlantsToWater()) {
            System.out.println("  - " + p.getName() + " -> " + p.getWateringInfo());
        }
    }

    private static Path resolveInputFile(String[] args) {
        if (args != null && args.length > 0) {
            Path p = Paths.get(args[0]);
            if (p.toFile().exists()) return p;
        }

        String[] candidates = new String[]{"./data/kvetiny.txt"};
        for (String c : candidates) {
            Path p = Paths.get(c);
            if (p.toFile().exists()) return p;
        }

        return null;
    }
}