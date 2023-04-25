package es.udc.ws.ficrun.client.service.exceptions;

public class ClientInscriptionClosedException extends Exception {
    private Long runId;

    public ClientInscriptionClosedException(Long runId) {
        super("The inscriptions to the race with id=" + runId + " are closed");
        this.runId = runId;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

}
