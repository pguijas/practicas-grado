package es.udc.ws.ficrun.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.ficrun.restservice.dto.RestInscriptionDto;
import java.util.List;

public class JsonToRestInscriptionDtoConversor {
    public static ObjectNode toObjectNode(RestInscriptionDto inscription) {
        ObjectNode inscriptionObject = JsonNodeFactory.instance.objectNode();
        if (inscription.getInscriptionId() != null) {
            inscriptionObject.put("inscriptionId", inscription.getInscriptionId());
        }
        inscriptionObject.put("runId", inscription.getRunId()).
                put("inscriptionDate", inscription.getInscriptionDate()).
                put("runnerEmail", inscription.getRunnerEmail()).
                put("creditCardNumber", inscription.getCreditCardNumber()).
                put("price", inscription.getPrice()).
                put("dorsal", inscription.getDorsal()).
                put("dorsalPicked", inscription.isDorsalPicked());

        return inscriptionObject;
    }

    public static ArrayNode toArrayNode(List<RestInscriptionDto> inscriptions) {

        ArrayNode inscriptionsNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < inscriptions.size(); i++) {
            RestInscriptionDto inscriptionDto = inscriptions.get(i);
            ObjectNode inscriptionObject = toObjectNode(inscriptionDto);
            inscriptionsNode.add(inscriptionObject);
        }

        return inscriptionsNode;
    }
}
