import java.time.LocalDate;

public class Plant {
    private String name;
    private String notes;
    private LocalDate planted;
    private LocalDate watering;
    private int wateringFrequency;

    public Plant(String name, String notes, LocalDate planted, LocalDate watering, int wateringFrequency) throws PlantException {
        setName(name);
        this.notes = notes;
        this.planted = planted;
        this.watering = watering;
        this.wateringFrequency = wateringFrequency;
    }

    public Plant(String name, int wateringFrequency) throws PlantException {
        this(name, "", LocalDate.now(), LocalDate.now(), wateringFrequency);
    }

    public Plant(String name) throws PlantException {
        this(name, 7);
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDate getPlanted() {
        return planted;
    }

    public LocalDate getWatering() {
        return watering;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }

    public void setName(String name) throws PlantException {
        if (name == null || name.isBlank()) {
            throw new PlantException("Název rostliny nesmí být prázdný.");
        }
        this.name = name.trim();
    }
}


