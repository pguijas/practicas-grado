package es.udc.ws.ficrun.client.service.thrift;

import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.thrift.ThriftRunDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientRunDtoToThriftRunDtoConversor {

    public static ClientRunDto toClientRunDto(ThriftRunDto run) {
        return new ClientRunDto(
                run.getRunId(),
                run.getName(),
                run.getCity(),
                LocalDateTime.parse(run.getStartDate()),
                run.getDescription(),
                (float)run.getPrice(),
                run.getMaxRunners(),
                run.getMaxRunners() - run.getNumInscriptions()
        );

    }

    public static ThriftRunDto toThriftRunDto(
            ClientRunDto clientRunDto) {

        Long runId = clientRunDto.getRunID();

        return new ThriftRunDto(
                runId == null ? -1 : runId.longValue(),
                clientRunDto.getName(),
                clientRunDto.getCity(),
                clientRunDto.getStartDate().toString(),
                clientRunDto.getDescription(),
                Double.valueOf(clientRunDto.getPrice()).floatValue(),
                clientRunDto.getMaxRunners(),
                clientRunDto.getMaxRunners()-clientRunDto.getNumAdmissions());

    }

    public static List<ClientRunDto> toClientRunDtos (List<ThriftRunDto> runs) {

        List<ClientRunDto> clientRunDtos = new ArrayList<>(runs.size());

        for (ThriftRunDto run : runs) {
            clientRunDtos.add(toClientRunDto(run));
        }
        return clientRunDtos;

    }
}