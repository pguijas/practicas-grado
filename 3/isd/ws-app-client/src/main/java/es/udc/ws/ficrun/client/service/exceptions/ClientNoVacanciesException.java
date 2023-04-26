package es.udc.ws.ficrun.client.service.exceptions;

public class ClientNoVacanciesException extends Exception{
    private int maxRunners;
    private int numInscriptions;

    public ClientNoVacanciesException(int maxRunners, int numInscriptions) {
        super("The inscriptions has reached the maximum number of runners (" + maxRunners + ")");
        this.maxRunners = maxRunners;
        this.numInscriptions = numInscriptions;
    }

    public int getMaxRunners() {
        return maxRunners;
    }

    public void setMaxRunners(int maxRunners) {
        this.maxRunners = maxRunners;
    }

    public int getNumInscriptions() {
        return numInscriptions;
    }

    public void setNumInscriptions(int numInscriptions) {
        this.numInscriptions = numInscriptions;
    }
}
