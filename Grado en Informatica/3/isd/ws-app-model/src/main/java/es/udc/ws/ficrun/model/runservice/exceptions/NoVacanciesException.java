package es.udc.ws.ficrun.model.runservice.exceptions;

public class NoVacanciesException extends Exception{
    private int maxRunners;
    private int numInscriptions;

    public NoVacanciesException(int maxRunners, int numInscriptions) {
        super("The maximun number of runners ("+maxRunners+") has been reached");
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
