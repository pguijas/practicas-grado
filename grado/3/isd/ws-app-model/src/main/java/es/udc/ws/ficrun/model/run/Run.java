package es.udc.ws.ficrun.model.run;

import java.time.LocalDateTime;
import java.util.Objects;

public class Run {
    private long runId;
    private String name;
    private String city;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;
    private String description;
    private float price;
    private int maxRunners;
    private int numInscriptions;

    public Run(String name, String city, LocalDateTime startDate,
               String description, float price, int maxRunners, int numInscriptions) {
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.description = description;
        this.price = price;
        this.maxRunners = maxRunners;
        this.numInscriptions = numInscriptions;
    }

    public Run(String name, String city, LocalDateTime startDate, LocalDateTime creationDate,
               String description, float price, int maxRunners, int numInscriptions) {
        this(name, city, startDate, description, price, maxRunners, numInscriptions);
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }


    public Run(long runId, String name, String city, LocalDateTime startDate,
               LocalDateTime creationDate, String description, float price, int maxRunners, int numInscriptions) {
        this(name, city, startDate, creationDate, description, price, maxRunners, numInscriptions);
        this.runId = runId;
    }



    //Getters
    public long getRunID() {
        return runId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public int getMaxRunners() {
        return maxRunners;
    }

    public int getNumInscriptions() {
        return numInscriptions;
    }

    //Setters
    public void setRunID(long runId) {
        this.runId = runId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setMaxRunners(int maxRunners) {
        this.maxRunners = maxRunners;
    }

    public void setNumInscriptions(int numInscriptions) {
        this.numInscriptions = numInscriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Run run = (Run) o;
        return runId == run.runId &&
                Float.compare(run.price, price) == 0 &&
                maxRunners == run.maxRunners &&
                numInscriptions == run.numInscriptions &&
                name.equals(run.name) &&
                city.equals(run.city) &&
                startDate.equals(run.startDate) &&
                creationDate.equals(run.creationDate) &&
                description.equals(run.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runId, name, city, startDate, creationDate, description, price, maxRunners, numInscriptions);
    }

    //para debuguear
    @Override
    public String toString() {
        return "Run{" +
                "runId=" + runId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", startDate=" + startDate +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", maxRunners=" + maxRunners +
                ", numInscriptions=" + numInscriptions +
                '}';
    }
}
