package es.udc.ws.ficrun.restservice.dto;

import java.time.LocalDateTime;

//no incluimos la fecha de alta de la carrera
public class RestRunDto {
    private Long runId;
    private String name;
    private String city;
    private LocalDateTime startDate;
    private String description;
    private float price;
    private int maxRunners;
    private int numInscriptions;

    public RestRunDto(Long runId, String name, String city, LocalDateTime startDate, String description, float price, int maxRunners, int numInscriptions) {
        this.runId = runId;
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.description = description;
        this.price = price;
        this.maxRunners = maxRunners;
        this.numInscriptions = numInscriptions;
    }

    //Getters
    public Long getRunID() {
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

}
