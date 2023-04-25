package es.udc.ws.ficrun.client.service;

import es.udc.ws.ficrun.client.service.dto.ClientInscriptionDto;
import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.util.List;

import java.time.LocalDateTime;

public interface ClientRunService {

    public Long addRun(ClientRunDto run) throws InputValidationException;

    public List<ClientRunDto> findRuns(String beforeDate, String city) throws InputValidationException;

    //Pedro
    public int findRun(Long runID) throws InstanceNotFoundException;

    //Pedro
    public void pickDorsal(Long codePickDorsal, String creditCardNumber)
            throws InputValidationException, ClientDorsalPickedException, ClientWrongCreditCardException, InstanceNotFoundException;

    //Adrián
    public List<ClientInscriptionDto> findInscriptions(String runnerEmail) throws InputValidationException, InstanceNotFoundException;

    //Adrián
    public Long registerRun(String emailRunner, Long runId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, ClientInscriptionClosedException, ClientAlreadyRegisterException, ClientNoVacanciesException;
}
