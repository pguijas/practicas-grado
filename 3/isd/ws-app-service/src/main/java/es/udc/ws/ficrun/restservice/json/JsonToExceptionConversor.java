package es.udc.ws.ficrun.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.ficrun.model.runservice.exceptions.DorsalPickedException;
import es.udc.ws.ficrun.model.runservice.exceptions.InscriptionClosedException;
import es.udc.ws.ficrun.model.runservice.exceptions.NoVacanciesException;
import es.udc.ws.ficrun.model.runservice.exceptions.WrongCreditCardException;
import es.udc.ws.ficrun.model.runservice.exceptions.AlreadyRegisterException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class JsonToExceptionConversor {
    public static ObjectNode toInputValidationException(InputValidationException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InputValidation");
        exceptionObject.put("message", ex.getMessage());

        return exceptionObject;
    }

    public static ObjectNode toInstanceNotFoundException(InstanceNotFoundException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        ObjectNode dataObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InstanceNotFound");
        exceptionObject.put("instanceId", (ex.getInstanceId() != null) ?
                ex.getInstanceId().toString() : null);
        exceptionObject.put("instanceType",
                ex.getInstanceType().substring(ex.getInstanceType().lastIndexOf('.') + 1));

        return exceptionObject;
    }

    public static ObjectNode toWrongCreditCardException(WrongCreditCardException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "WrongCreditCard");
        exceptionObject.put("inscriptionId", (ex.getInscriptionId() != null) ? ex.getInscriptionId() : null);
        exceptionObject.put("creditCardNumber", (ex.getCreditCardNumber() != null) ? ex.getCreditCardNumber() : null);

        return exceptionObject;
    }

    public static ObjectNode toDorsalPickedException(DorsalPickedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "DorsalPicked");
        exceptionObject.put("inscriptionId", (ex.getInscriptionId() != null) ? ex.getInscriptionId() : null);

        return exceptionObject;
    }

    public static ObjectNode toInscriptionClosedException(InscriptionClosedException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InscriptionClosedException");
        exceptionObject.put("runId", (ex.getRunId() != null) ? ex.getRunId() : null);

        return exceptionObject;
    }

    public static ObjectNode toAlreadyRegisterException(AlreadyRegisterException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyRegisterException");
        exceptionObject.put("runnerEmail", (ex.getRunnerEmail() != null) ? ex.getRunnerEmail() : null);

        return exceptionObject;
    }

    public static ObjectNode toNoVacanciesException(NoVacanciesException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "NoVacanciesException");
        exceptionObject.put("maxRunners", (ex.getMaxRunners() != 0) ? ex.getMaxRunners() : null);
        exceptionObject.put("numInscriptions", (ex.getNumInscriptions() != 0) ? ex.getNumInscriptions() : null);

        return exceptionObject;
    }

}
