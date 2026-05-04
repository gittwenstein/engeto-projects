import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Collections;

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
        Collections.sort(plants, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
    }

    public void sortByLastWateringDate() {
        Collections.sort(plants, (p1, p2) -> {
            int dateComparison = p1.getLastWateringDate().compareTo(p2.getLastWateringDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return p1.getName().compareToIgnoreCase(p2.getName());
        });
    }
}

