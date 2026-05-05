import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantManager {
    private final List<Plant> plants = new ArrayList<>();
    private static final Pattern TRAILING_NUMBER_PATTERN = Pattern.compile("^(.*?)(\\d+)$");

    public void addPlant(Plant plant) {
        plants.add(plant);
    }

    public Plant getPlant(int index) {
        return plants.get(index);
    }

    public void removePlant(int index) {
        plants.remove(index);
    }

    public List<Plant> getPlantsCopy() {
        return new ArrayList<>(plants);
    }

    public List<Plant> getPlantsToWater() {
        List<Plant> toWater = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Plant plant : plants) {
            LocalDate nextWateringDate = plant.getLastWateringDate().plusDays(plant.getWateringFrequency());
            if (nextWateringDate.isBefore(today)) {
                toWater.add(plant);
            }
        }

        return toWater;
    }

    public void sortByName() {
        plants.sort((p1, p2) -> comparePlantNamesNatural(p1.getName(), p2.getName()));
    }

    public void sortByLastWateringDate() {
        plants.sort((p1, p2) -> {
            int dateComparison = p1.getLastWateringDate().compareTo(p2.getLastWateringDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return comparePlantNamesNatural(p1.getName(), p2.getName());
        });
    }

    private int comparePlantNamesNatural(String first, String second) {
        Matcher firstMatcher = TRAILING_NUMBER_PATTERN.matcher(first);
        Matcher secondMatcher = TRAILING_NUMBER_PATTERN.matcher(second);

        boolean firstHasTrailingNumber = firstMatcher.matches();
        boolean secondHasTrailingNumber = secondMatcher.matches();

        if (firstHasTrailingNumber && secondHasTrailingNumber) {
            String firstPrefix = firstMatcher.group(1);
            String secondPrefix = secondMatcher.group(1);

            int prefixComparison = firstPrefix.compareToIgnoreCase(secondPrefix);
            if (prefixComparison != 0) {
                return prefixComparison;
            }

            long firstNumber = Long.parseLong(firstMatcher.group(2));
            long secondNumber = Long.parseLong(secondMatcher.group(2));
            int numberComparison = Long.compare(firstNumber, secondNumber);
            if (numberComparison != 0) {
                return numberComparison;
            }
        }

        int insensitiveComparison = first.compareToIgnoreCase(second);
        if (insensitiveComparison != 0) {
            return insensitiveComparison;
        }
        return first.compareTo(second);
    }

    public String formatForFile(Plant plant) {
        // File format: name \t notes \t frequency \t last watering \t planting
        return String.format("%s\t%s\t%d\t%s\t%s",
                plant.getName(),
                plant.getNotes(),
                plant.getWateringFrequency(),
                plant.getLastWateringDate().toString(),
                plant.getPlantedDate().toString());
    }

    public Plant parsePlantLine(String line, int lineNumber) throws PlantException {
        String[] parts = line.split("\t", -1);
        if (parts.length != 5) {
            throw new PlantException("Řádek " + lineNumber + ": očekáváno 5 položek (název, poznámky, frekvence, poslední zálivka, zasazení), nalezeno " + parts.length);
        }

        // File columns (observed): name, notes, frequency, last watering, planting
        String name = parts[0].trim();
        String notes = parts[1].trim();
        String frequencyStr = parts[2].trim();
        String wateringStr = parts[3].trim();
        String plantedStr = parts[4].trim();

        try {
            int frequency = parseFrequency(frequencyStr);
            LocalDate planted = parseDate(plantedStr);
            LocalDate watering = parseDate(wateringStr);

            return new Plant(name, notes, planted, watering, frequency);
        } catch (PlantException e) {
            throw new PlantException("Řádek " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String dateStr) throws PlantException {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d.M.yyyy"));
            } catch (DateTimeParseException e2) {
                throw new PlantException("Neplatné datum '" + dateStr + "' (očekáváno formát YYYY-MM-DD nebo d.M.yyyy)");
            }
        }
    }

    private int parseFrequency(String frequencyStr) throws PlantException {
        try {
            return Integer.parseInt(frequencyStr);
        } catch (NumberFormatException e) {
            throw new PlantException("Neplatná frekvence '" + frequencyStr + "' (očekáváno celé číslo)");
        }
    }

    public void loadFromFile(Path filePath) throws PlantException {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Plant> loadedPlants = new ArrayList<>();

            int lineNumber = 0;
            for (String line : lines) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                Plant plant = parsePlantLine(trimmed, lineNumber);
                loadedPlants.add(plant);
            }

            // Only replace internal list if all lines parsed successfully
            this.plants.clear();
            this.plants.addAll(loadedPlants);

        } catch (IOException e) {
            throw new PlantException("Chyba při čtení souboru: " + e.getMessage(), e);
        }
    }

    public void saveToFile(Path filePath) throws PlantException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Plant plant : plants) {
                lines.add(formatForFile(plant));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new PlantException("Chyba při zápisu do souboru: " + e.getMessage(), e);
        }
    }
}

