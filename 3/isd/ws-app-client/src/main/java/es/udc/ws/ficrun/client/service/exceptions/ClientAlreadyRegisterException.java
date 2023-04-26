package es.udc.ws.ficrun.client.service.exceptions;

public class ClientAlreadyRegisterException extends Exception{
    private String runnerEmail;

    public ClientAlreadyRegisterException(String runnerEmail) {
        super("The user with the email=" + runnerEmail + " is signed up for this race");
        this.runnerEmail = runnerEmail;
    }

    public String getRunnerEmail() {
        return runnerEmail;
    }

    public void setRunnerEmail(String runnerEmail) {
        this.runnerEmail = runnerEmail;
    }
}
