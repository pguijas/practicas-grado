package es.udc.ws.ficrun.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.ficrun.client.service.dto.ClientInscriptionDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientInscriptionDtoConversor {

    public static ClientInscriptionDto toClientInscriptionDto(InputStream jsonInscription) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonInscription);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientInscriptionDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientInscriptionDto> toClientInscriptionDtos(InputStream jsonInscriptions) throws ParsingException{
        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonInscriptions);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode inscriptionsArray = (ArrayNode) rootNode;
                List<ClientInscriptionDto> inscriptionDtos = new ArrayList<>(inscriptionsArray.size());
                for (JsonNode inscriptionNode : inscriptionsArray) {
                    inscriptionDtos.add(toClientInscriptionDto(inscriptionNode));
                }

                return inscriptionDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientInscriptionDto toClientInscriptionDto(JsonNode inscriptionNode) throws ParsingException {
        try {
            if (inscriptionNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode inscriptionObject = (ObjectNode) inscriptionNode;

                JsonNode inscriptionIdNode = inscriptionObject.get("inscriptionId");
                Long inscriptionId = (inscriptionIdNode != null) ? inscriptionIdNode.longValue() : null;

                Long runId = inscriptionObject.get("runId").longValue();
                String runnerEmail = inscriptionObject.get("runnerEmail").textValue().trim();
                String inscriptionDate = inscriptionObject.get("inscriptionDate").textValue().trim();
                String creditCardNumber = inscriptionObject.get("creditCardNumber").textValue().trim();
                float price = inscriptionObject.get("price").floatValue();
                int dorsal =  inscriptionObject.get("dorsal").intValue();
                boolean dorsalPicked =  inscriptionObject.get("dorsalPicked").booleanValue();

                return new ClientInscriptionDto(inscriptionId,runId,runnerEmail,inscriptionDate,creditCardNumber,price,dorsal,dorsalPicked);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
