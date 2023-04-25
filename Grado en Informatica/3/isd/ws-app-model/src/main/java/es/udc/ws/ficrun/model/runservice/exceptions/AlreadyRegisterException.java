package es.udc.ws.ficrun.model.runservice.exceptions;

public class AlreadyRegisterException extends Exception{
    private String runnerEmail;

    public AlreadyRegisterException(String runnerEmail){
        super("The runner with the email= " +runnerEmail+ "is already signed up");
        this.runnerEmail=runnerEmail;
    }

    public String getRunnerEmail(){ return runnerEmail; }

    public void setRunnerEmail(String runnerEmail) { this.runnerEmail=runnerEmail; }
}
