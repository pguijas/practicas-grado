package es.udc.ws.ficrun.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.ficrun.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {
    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else if (errorType.equals("NoVacanciesException")){
                    return toNoVacanciesException(rootNode);
                } else if (errorType.equals("AlreadyRegisterException")){
                    return toAlreadyRegisterException(rootNode);
                } else if (errorType.equals("InscriptionClosedException")){
                    return toInscriptionClosedException(rootNode);
                } else if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("DorsalPicked")) {
                    return toDorsalPickedException(rootNode);
                } else if (errorType.equals("WrongCreditCard")){
                    return toWrongCreditCardException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    private static ClientWrongCreditCardException toWrongCreditCardException(JsonNode rootNode) {
        Long inscriptionId = rootNode.get("inscriptionId").longValue();
        String creditCardNumber = rootNode.get("creditCardNumber").textValue();
        return new ClientWrongCreditCardException(inscriptionId,creditCardNumber);
    }

    private static ClientDorsalPickedException toDorsalPickedException(JsonNode rootNode) {
        Long inscriptionId = rootNode.get("inscriptionId").longValue();
        return new ClientDorsalPickedException(inscriptionId);
    }

    private static ClientInscriptionClosedException toInscriptionClosedException(JsonNode rootNode){
        Long runId = rootNode.get("runId").longValue();
        return new ClientInscriptionClosedException(runId);
    }

    private static ClientAlreadyRegisterException toAlreadyRegisterException(JsonNode rootNode){
        String runnerEmail = rootNode.get("runnerEmail").textValue();
        return new ClientAlreadyRegisterException(runnerEmail);
    }

    private static ClientNoVacanciesException toNoVacanciesException(JsonNode rootNode){
        int maxRunners = rootNode.get("maxRunners").intValue();
        int numInscriptions = rootNode.get("numInscriptions").intValue();
        return new ClientNoVacanciesException(maxRunners,numInscriptions);
    }
}
