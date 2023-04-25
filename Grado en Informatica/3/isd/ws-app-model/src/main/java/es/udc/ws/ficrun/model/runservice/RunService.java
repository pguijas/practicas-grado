package es.udc.ws.ficrun.model.runservice;

import es.udc.ws.ficrun.model.inscription.Inscription;
import es.udc.ws.ficrun.model.run.Run;

import es.udc.ws.ficrun.model.runservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RunService {

    public Run addRun (Run run) throws InputValidationException;

    public Run findRun (Long runID) throws InstanceNotFoundException;

    public List<Run> findRuns(LocalDate beforeDate, String city) throws InputValidationException;

    public Inscription registerRun(String emailRunner, Long runId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, InscriptionClosedException, AlreadyRegisterException, NoVacanciesException;

    public List<Inscription> findInscriptions(String runnerEmail) throws InstanceNotFoundException, InputValidationException;

    public void pickDorsal(Long codePickDorsal, String creditCardNumber) throws InputValidationException, InstanceNotFoundException, DorsalPickedException, WrongCreditCardException;

}
