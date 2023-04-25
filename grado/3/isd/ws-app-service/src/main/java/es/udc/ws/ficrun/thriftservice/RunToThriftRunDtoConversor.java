package es.udc.ws.ficrun.thriftservice;

import es.udc.ws.ficrun.model.run.Run;
import es.udc.ws.ficrun.thrift.ThriftRunDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RunToThriftRunDtoConversor {

    //Otros métodos interesantes para el proyecto (Inscripciones) no los implemento dado que no los necesito para mi parte.

    public static ThriftRunDto toThriftRunDto(Run run) {
        return new ThriftRunDto(run.getRunID(),run.getName(), run.getCity(), run.getStartDate().toString(),run.getDescription(),
                run.getPrice(), run.getMaxRunners(), run.getNumInscriptions());
    }

    public static Run toRun(ThriftRunDto runDto) {
        return new Run(runDto.getName(), runDto.getCity(), LocalDateTime.parse(runDto.getStartDate()), runDto.getDescription(),
                Double.valueOf(runDto.getPrice()).floatValue(), runDto.getMaxRunners(), runDto.getNumInscriptions());
    }

    public static List<ThriftRunDto> toThriftRunDtos(List<Run> runs) {

        List<ThriftRunDto> dtos = new ArrayList<>(runs.size());

        for (Run run : runs) {
            dtos.add(toThriftRunDto(run));
        }
        return dtos;

    }
}
