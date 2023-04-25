package es.udc.ws.ficrun.client.service.exceptions;

public class ClientWrongCreditCardException extends Exception{
    private Long inscriptionId;
    private String creditCardNumber;

    public ClientWrongCreditCardException(Long inscriptionId, String creditCardNumber) {
        super("Inscription with id=" + inscriptionId + " was not registered with that credit card (" + creditCardNumber + "))");
        this.inscriptionId = inscriptionId;
        this.creditCardNumber = creditCardNumber;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
