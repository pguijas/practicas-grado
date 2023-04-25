package es.udc.ws.ficrun.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.thrift.ThriftRunService;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientRunDtoConversor {
    public static ObjectNode toObjectNode(ClientRunDto run) {
        ObjectNode runObject = JsonNodeFactory.instance.objectNode();
        if (run.getRunID() != null) {
            runObject.put("runId", run.getRunID());
        }
        runObject.put("name", run.getName()).
                put("city", run.getCity()).
                put("startDate", run.getStartDate().toString()).
                put("description", run.getDescription()).
                put("price", run.getPrice()).
                put("maxRunners", run.getMaxRunners()).
                put("numInscriptions", run.getMaxRunners() - run.getNumAdmissions());

        return runObject;
    }

    public static ClientRunDto toClientRunDto(InputStream jsonRun) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRun);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientRunDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }


    public static ClientRunDto toClientRunDto(JsonNode runNode) throws ParsingException {
        try {
            if (runNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode runObject = (ObjectNode) runNode;

                JsonNode runIdNode = runObject.get("runId");
                Long runId = (runIdNode != null) ? runIdNode.longValue() : null;

                String title = runObject.get("name").textValue().trim();
                String city = runObject.get("city").textValue().trim();
                LocalDateTime startDate = LocalDateTime.parse(runObject.get("startDate").textValue().trim());
                String description = runObject.get("description").textValue().trim();
                float price = runObject.get("price").floatValue();
                int maxRunners = runObject.get("maxRunners").intValue();
                int numInscriptions = runObject.get("numInscriptions").intValue();

                return new ClientRunDto(runId, title, city, startDate, description, price, maxRunners, maxRunners - numInscriptions);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientRunDto> toClientRunDtos(InputStream jsonRuns) {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRuns);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode runsArray = (ArrayNode) rootNode;
                List<ClientRunDto> runDtos = new ArrayList<>(runsArray.size());
                for (JsonNode runNode : runsArray) {
                    runDtos.add(toClientRunDto(runNode));
                }

                return runDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
