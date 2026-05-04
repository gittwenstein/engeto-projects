import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PlantManager {
    private final List<Plant> plants = new ArrayList<>();

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
        plants.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
    }

    public void sortByLastWateringDate() {
        plants.sort((p1, p2) -> {
            int dateComparison = p1.getLastWateringDate().compareTo(p2.getLastWateringDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return p1.getName().compareToIgnoreCase(p2.getName());
        });
    }

    public String formatForFile(Plant plant) {
        return String.format("%s\t%s\t%s\t%d\t%s",
                plant.getName(),
                plant.getPlantedDate().toString(),
                plant.getLastWateringDate().toString(),
                plant.getWateringFrequency(),
                plant.getNotes());
    }

    public Plant parsePlantLine(String line, int lineNumber) throws PlantException {
        String[] parts = line.split("\t");
        if (parts.length != 5) {
            throw new PlantException("Řádek " + lineNumber + ": očekáváno 5 položek (název, zasazeno, zality, frekvence, poznámky), nalezeno " + parts.length);
        }

        String name = parts[0].trim();
        String plantedStr = parts[1].trim();
        String wateringStr = parts[2].trim();
        String frequencyStr = parts[3].trim();
        String notes = parts[4].trim();

        try {
            LocalDate planted = parseDate(plantedStr);
            LocalDate watering = parseDate(wateringStr);
            int frequency = parseFrequency(frequencyStr);

            return new Plant(name, notes, planted, watering, frequency);
        } catch (PlantException e) {
            throw new PlantException("Řádek " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String dateStr) throws PlantException {
        try {
            // Try ISO format first (YYYY-MM-DD)
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e1) {
            try {
                // Try Czech format (d.M.yyyy)
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
}

