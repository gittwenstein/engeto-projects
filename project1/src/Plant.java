import java.time.LocalDate;

public class Plant {
    private String name;
    private String notes;
    private LocalDate planted;
    private LocalDate watering;
    private int wateringFrequency;

    public Plant(String name, String notes, LocalDate planted, LocalDate watering, int wateringFrequency) throws PlantException {
        setName(name);
        setNotes(notes);
        setPlanted(planted);
        setWatering(watering);
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

    public void setNotes(String notes) {
        this.notes = (notes == null) ? "" : notes;
    }

    public void setPlanted(LocalDate planted) throws PlantException {
        if (planted == null) {
            throw new PlantException("Datum zasazení nesmí být null.");
        }
        if (this.watering != null && this.watering.isBefore(planted)) {
            throw new PlantException("Datum poslední zálivky nesmí být starší než datum zasazení.");
        }
        this.planted = planted;
    }

    public void setWatering(LocalDate watering) throws PlantException {
        if (watering == null) {
            throw new PlantException("Datum poslední zálivky nesmí být null.");
        }
        if (this.planted != null && watering.isBefore(this.planted)) {
            throw new PlantException("Datum poslední zálivky nesmí být starší než datum zasazení.");
        }
        this.watering = watering;
    }

    public void setWateringFrequency(int wateringFrequency) throws PlantException {
        if (wateringFrequency <= 0) {
            throw new PlantException("Frekvence zálivky musí být větší než 0.");
        }
        this.wateringFrequency = wateringFrequency;
    }

    public String getWateringInfo() {
        LocalDate nextWatering = watering.plusDays(wateringFrequency);
        return String.format("Rostlina: %s, poslední zálivka: %s, příští zálivka: %s",
                name, watering, nextWatering);
    }

    public void doWateringNow() throws PlantException {
        setWatering(LocalDate.now());
    }
}


