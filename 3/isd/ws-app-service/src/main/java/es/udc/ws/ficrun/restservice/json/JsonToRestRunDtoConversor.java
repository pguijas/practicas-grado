package es.udc.ws.ficrun.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.ficrun.restservice.dto.RestRunDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public class JsonToRestRunDtoConversor {
    public static ObjectNode toObjectNode(RestRunDto run) {
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
                put("numInscriptions", run.getNumInscriptions());

        return runObject;
    }

    public static RestRunDto toServiceRunDto(InputStream jsonRun) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonRun);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode runObject = (ObjectNode) rootNode;

                JsonNode runIdNode = runObject.get("runId");
                Long runId = (runIdNode != null) ? runIdNode.longValue() : null;
                String title = runObject.get("name").textValue().trim();
                String city = runObject.get("city").textValue().trim();
                LocalDateTime startDate = LocalDateTime.parse(runObject.get("startDate").textValue().trim());
                String description = runObject.get("description").textValue().trim();
                float price = runObject.get("price").floatValue();
                int maxRunners =  runObject.get("maxRunners").intValue();
                int numInscriptions =  runObject.get("numInscriptions").intValue();

                return new RestRunDto(runId,title,city,startDate,description,price,maxRunners,numInscriptions);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static ArrayNode toArrayNode(List<RestRunDto> runs) {

        ArrayNode runsNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < runs.size(); i++) {
            RestRunDto runDto = runs.get(i);
            ObjectNode movieObject = toObjectNode(runDto);
            runsNode.add(movieObject);
        }

        return runsNode;
    }

}
