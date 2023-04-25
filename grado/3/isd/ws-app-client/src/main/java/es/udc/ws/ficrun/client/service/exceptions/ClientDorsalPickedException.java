package es.udc.ws.ficrun.client.service.exceptions;

public class ClientDorsalPickedException extends Exception{
    private Long inscriptionId;

    public ClientDorsalPickedException(Long inscriptionId) {
        super("Inscription with id=" + inscriptionId + " was has already been picked");
        this.inscriptionId = inscriptionId;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }
}
