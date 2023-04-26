package es.udc.ws.ficrun.restservice.dto;

import es.udc.ws.ficrun.model.run.Run;

import java.util.ArrayList;
import java.util.List;

public class RunToRestRunDtoConversor {

    public static List<RestRunDto> toRestRunDtos(List<Run> runs) {
        List<RestRunDto> runsDtos = new ArrayList<>(runs.size());
        for (int i = 0; i < runs.size(); i++) {
            Run run = runs.get(i);
            runsDtos.add(toRestRunDto(run));
        }
        return runsDtos;
    }


    public static RestRunDto toRestRunDto(Run run){
        return new RestRunDto(run.getRunID(), run.getName(), run.getCity(), run.getStartDate(), run.getDescription(),
                run.getPrice(), run.getMaxRunners(), run.getNumInscriptions());
    }

    public static Run toRun(RestRunDto runDto){
        return new Run(runDto.getName(), runDto.getCity(), runDto.getStartDate(), runDto.getDescription(),
                runDto.getPrice(), runDto.getMaxRunners(), runDto.getNumInscriptions());

    }
}
