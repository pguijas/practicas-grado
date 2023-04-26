package es.udc.ws.ficrun.restservice.dto;


public class RestInscriptionDto {

    private Long inscriptionId;
    private Long runId;
    private String runnerEmail;
    private String inscriptionDate;
    private String creditCardNumber;
    private float price;
    private int dorsal;
    private boolean dorsalPicked;

    public RestInscriptionDto(Long inscriptionId, Long runId, String runnerEmail, String inscriptionDate, String creditCardNumber, float price, int dorsal, boolean dorsalPicked){
        this.inscriptionId = inscriptionId;
        this.runId = runId;
        this.runnerEmail = runnerEmail;
        this.inscriptionDate = inscriptionDate;
        this.creditCardNumber = creditCardNumber;
        this.price = price;
        this.dorsal = dorsal;
        this.dorsalPicked = dorsalPicked;
    }

    public Long getInscriptionId(){return inscriptionId;}
    public void setInscriptionId(Long inscriptionId){this.inscriptionId=inscriptionId;}
    public Long getRunId(){return runId;}
    public void setRunId(Long runId){this.runId=runId;}
    public String getRunnerEmail(){return runnerEmail;}
    public void setRunnerEmail(String runnerEmail){this.runnerEmail=runnerEmail;}
    public String getInscriptionDate(){return inscriptionDate;}
    public String getCreditCardNumber(){return creditCardNumber;}
    public void setCreditCardNumber(String creditCardNumber){this.creditCardNumber=creditCardNumber;}
    public void setInscriptionDate(String inscriptionDate){this.inscriptionDate=inscriptionDate;}
    public float getPrice(){return price;}
    public void setPrice(float price){this.price=price;}
    public int getDorsal(){return dorsal;}
    public void setDorsal(int dorsal){this.dorsal=dorsal;}
    public boolean isDorsalPicked(){return dorsalPicked;}
    public void setDorsalPicked(boolean dorsalPicked){this.dorsalPicked=dorsalPicked;}
}
