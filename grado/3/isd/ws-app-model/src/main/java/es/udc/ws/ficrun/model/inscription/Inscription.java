package es.udc.ws.ficrun.model.inscription;

import java.time.LocalDateTime;
import java.util.Objects;

public class Inscription {
    private Long inscriptionId;
    private Long runId;
    private String runnerEmail;
    private LocalDateTime inscriptionDate;
    private String creditCardNumber;
    private float price;                      //podría modificarse después de que alguien pagase (por eso lo almacenamos)
    private int dorsal;
    private boolean dorsalPicked;

    public Inscription(Long runId, String runnerEmail, LocalDateTime inscriptionDate, String creditCardNumber, float price, int dorsal, boolean dorsalPicked){
        this.runId = runId;
        this.runnerEmail = runnerEmail;
        this.inscriptionDate = inscriptionDate;
        this.creditCardNumber = creditCardNumber;
        this.price = price;
        this.dorsal = dorsal;
        this.dorsalPicked = dorsalPicked;
    }

    public Inscription(Long inscriptionId, Long runId, String runnerEmail, LocalDateTime inscriptionDate, String creditCardNumber,
                       float price, int dorsal,  boolean dorsalPicked) {
        this(runId, runnerEmail, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked);
        this.inscriptionId = inscriptionId;

    }

    //Setters
    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public void setRunnerEmail(String runnerEmail) {
        this.runnerEmail = runnerEmail;
    }

    public void setInscriptionDate(LocalDateTime inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public void setDorsalPicked(boolean dorsalPicked) {
        this.dorsalPicked = dorsalPicked;
    }

    //Getters
    public Long getInscriptionId() {
        return inscriptionId;
    }

    public Long getRunId() {
        return runId;
    }

    public String getRunnerEmail() {
        return runnerEmail;
    }

    public LocalDateTime getInscriptionDate() {
        return inscriptionDate;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public float getPrice() {
        return price;
    }

    public int getDorsal() {
        return dorsal;
    }

    public boolean isDorsalPicked() {
        return dorsalPicked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscription that = (Inscription) o;
        return inscriptionId == that.inscriptionId &&
                runId == that.runId &&
                Float.compare(that.price, price) == 0 &&
                dorsal == that.dorsal &&
                dorsalPicked == that.dorsalPicked &&
                Objects.equals(runnerEmail, that.runnerEmail) &&
                Objects.equals(inscriptionDate, that.inscriptionDate) &&
                Objects.equals(creditCardNumber, that.creditCardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inscriptionId, runId, runnerEmail, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked);
    }
}
