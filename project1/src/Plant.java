import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Plant {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private String name;
    private String notes;
    private LocalDate plantedDate;
    private LocalDate lastWateringDate;
    private int wateringFrequency;

    public Plant(String name, String notes, LocalDate plantedDate, LocalDate lastWateringDate, int wateringFrequency) throws PlantException {
        setName(name);
        setNotes(notes);
        setPlantedDate(plantedDate);
        setLastWateringDate(lastWateringDate);
        setWateringFrequency(wateringFrequency);
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

    public LocalDate getPlantedDate() {
        return plantedDate;
    }

    public LocalDate getLastWateringDate() {
        return lastWateringDate;
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

    public void setNotes(String notes) {
        this.notes = (notes == null) ? "" : notes;
    }

    public void setPlantedDate(LocalDate plantedDate) throws PlantException {
        if (plantedDate == null) {
            throw new PlantException("Datum zasazení nesmí být null.");
        }
        if (this.lastWateringDate != null && this.lastWateringDate.isBefore(plantedDate)) {
            throw new PlantException("Datum poslední zálivky nesmí být starší než datum zasazení.");
        }
        this.plantedDate = plantedDate;
    }

    public void setLastWateringDate(LocalDate lastWateringDate) throws PlantException {
        if (lastWateringDate == null) {
            throw new PlantException("Datum poslední zálivky nesmí být null.");
        }
        if (this.plantedDate != null && lastWateringDate.isBefore(this.plantedDate)) {
            throw new PlantException("Datum poslední zálivky nesmí být starší než datum zasazení.");
        }
        this.lastWateringDate = lastWateringDate;
    }

    public void setWateringFrequency(int wateringFrequency) throws PlantException {
        if (wateringFrequency <= 0) {
            throw new PlantException("Frekvence zálivky musí být větší než 0.");
        }
        this.wateringFrequency = wateringFrequency;
    }

    public String getWateringInfo() {
        LocalDate nextWatering = lastWateringDate.plusDays(wateringFrequency);
        return String.format("Rostlina: %s, poslední zálivka: %s, příští zálivka: %s",
                name, lastWateringDate, nextWatering);
    }

    public void doWateringNow() throws PlantException {
        setLastWateringDate(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Plant{name='%s', notes='%s', planted=%s, watering=%s, wateringFrequency=%d}",
                name, notes, DATE_FORMATTER.format(plantedDate), DATE_FORMATTER.format(lastWateringDate), wateringFrequency);
    }
}


