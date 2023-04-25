package es.udc.ws.ficrun.restservice.dto;

import es.udc.ws.ficrun.model.inscription.Inscription;

import java.util.ArrayList;
import java.util.List;

public class InscriptionToRestInscriptionDtoConversor {
    public static RestInscriptionDto toRestInscriptionDto(Inscription inscription){
        return new RestInscriptionDto(inscription.getInscriptionId(), inscription.getRunId(), inscription.getRunnerEmail(),
                inscription.getInscriptionDate().toString(), inscription.getCreditCardNumber(), inscription.getPrice(), inscription.getDorsal(), inscription.isDorsalPicked());
    }

    public static List<RestInscriptionDto> toRestInscriptionDtos(List<Inscription> inscriptions) {
        List<RestInscriptionDto> inscriptionsDtos = new ArrayList<>(inscriptions.size());
        for (int i = 0; i < inscriptions.size(); i++) {
            Inscription inscription = inscriptions.get(i);
            inscriptionsDtos.add(toRestInscriptionDto(inscription));
        }
        return inscriptionsDtos;
    }

}
