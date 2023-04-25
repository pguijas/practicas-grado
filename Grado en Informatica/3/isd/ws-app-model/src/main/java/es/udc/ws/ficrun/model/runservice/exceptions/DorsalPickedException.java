package es.udc.ws.ficrun.model.runservice.exceptions;

import java.time.LocalDateTime;

public class DorsalPickedException extends Exception{
    private Long inscriptionId;

    public DorsalPickedException(Long inscriptionId) {
        super("Inscription with id=\"" + inscriptionId + " was has already been picked\")");
        this.inscriptionId = inscriptionId;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }
}
