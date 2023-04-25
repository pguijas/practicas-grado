package es.udc.ws.ficrun.thriftservice;


import es.udc.ws.ficrun.model.run.Run;
import es.udc.ws.ficrun.model.runservice.RunServiceFactory;
import es.udc.ws.ficrun.model.runservice.exceptions.DorsalPickedException;
import es.udc.ws.ficrun.model.runservice.exceptions.WrongCreditCardException;
import es.udc.ws.ficrun.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ThriftRunServiceImpl implements ThriftRunService.Iface{

    @Override
    public long addRun(ThriftRunDto runDto) throws ThriftInputValidationException, TException {
        Run run = RunToThriftRunDtoConversor.toRun(runDto);

        try {
            return RunServiceFactory.getService().addRun(run).getRunID();
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public List<ThriftRunDto> findRuns(String beforeDate, String city) throws ThriftInputValidationException, TException {

        try {
            if (city.isBlank()) throw new ThriftInputValidationException("Invalid Request: " + "Parameters required are beforedate and city.");
            LocalDate beforeD = LocalDate.parse(beforeDate);
            List<Run> runs = RunServiceFactory.getService().findRuns(beforeD, city);
            return RunToThriftRunDtoConversor.toThriftRunDtos(runs);

        } catch (DateTimeParseException e) {
            throw new ThriftInputValidationException("Invalid Request: " + "Invalid date format. Date format must be 'yyyy-MM-dd'");
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public ThriftRunDto findRun(long runId) throws ThriftInstanceNotFoundException{
        try {
            Run run = RunServiceFactory.getService().findRun(runId);
            return RunToThriftRunDtoConversor.toThriftRunDto(run);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public void pickDorsal(long inscriptionId, String creditCardNumber) throws ThriftInputValidationException, ThriftInstanceNotFoundException, ThriftDorsalPickedException, ThriftWrongCreditCardException{
        try {
            RunServiceFactory.getService().pickDorsal(inscriptionId,creditCardNumber);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (DorsalPickedException e) {
            throw new ThriftDorsalPickedException(e.getInscriptionId());
        } catch (WrongCreditCardException e) {
            throw new ThriftWrongCreditCardException(e.getInscriptionId(),e.getCreditCardNumber());
        }
    }
}
