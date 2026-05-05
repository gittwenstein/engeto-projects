import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantManager {
    private static final int EXPECTED_COLUMNS = 5;
    private static final DateTimeFormatter CZECH_DATE_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy");
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
            if (plant.getLastWateringDate().plusDays(plant.getWateringFrequency()).isBefore(today)) {
                toWater.add(plant);
            }
        }
        return toWater;
    }

    public void sortByName() {
        plants.sort(Comparator.comparing(Plant::getName, this::comparePlantNamesNatural));
    }

    public void sortByLastWateringDate() {
        plants.sort(Comparator.comparing(Plant::getLastWateringDate)
                .thenComparing(Plant::getName, this::comparePlantNamesNatural));
    }

    private int comparePlantNamesNatural(String first, String second) {
        Matcher firstMatcher = TRAILING_NUMBER_PATTERN.matcher(first);
        Matcher secondMatcher = TRAILING_NUMBER_PATTERN.matcher(second);

        if (firstMatcher.matches() && secondMatcher.matches()) {
            String firstPrefix = firstMatcher.group(1);
            String secondPrefix = secondMatcher.group(1);
            int prefixComparison = firstPrefix.compareToIgnoreCase(secondPrefix);
            if (prefixComparison != 0) return prefixComparison;
            long firstNumber = Long.parseLong(firstMatcher.group(2));
            long secondNumber = Long.parseLong(secondMatcher.group(2));
            int numberComparison = Long.compare(firstNumber, secondNumber);
            if (numberComparison != 0) return numberComparison;
        }
        int insensitiveComparison = first.compareToIgnoreCase(second);
        return insensitiveComparison != 0 ? insensitiveComparison : first.compareTo(second);
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
        if (parts.length != EXPECTED_COLUMNS) {
            throw new PlantException("Řádek " + lineNumber + ": očekáváno 5 položek (název, poznámky, frekvence, poslední zálivka, zasazení), nalezeno " + parts.length);
        }

        try {
            return new Plant(
                    parts[0].trim(),
                    parts[1].trim(),
                    parseDate(parts[4].trim()),
                    parseDate(parts[3].trim()),
                    parseFrequency(parts[2].trim())
            );
        } catch (PlantException e) {
            throw new PlantException("Řádek " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String dateStr) throws PlantException {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(dateStr, CZECH_DATE_FORMAT);
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
            for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
                String line = lines.get(lineNumber - 1);
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) loadedPlants.add(parsePlantLine(trimmed, lineNumber));
            }
            plants.clear();
            plants.addAll(loadedPlants);
        } catch (IOException e) {
            throw new PlantException("Chyba při čtení souboru: " + e.getMessage(), e);
        }
    }

    public void saveToFile(Path filePath) throws PlantException {
        try {
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (Plant plant : plants) lines.add(formatForFile(plant));
            Files.write(filePath, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new PlantException("Chyba při zápisu do souboru: " + e.getMessage(), e);
        }
    }
}

