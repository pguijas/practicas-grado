package es.udc.ws.ficrun.model.runservice.exceptions;

public class InscriptionClosedException extends Exception{
    private Long runId;

    public InscriptionClosedException(Long runId){
        super("Inscriptions for the race with id=" + runId + "are closed");
        this.runId=runId;
    }

    public Long getRunId(){ return runId; }

    public void setRunId(Long runId) { this.runId=runId; }
}
