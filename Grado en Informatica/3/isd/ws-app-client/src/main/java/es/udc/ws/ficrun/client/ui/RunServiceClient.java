package es.udc.ws.ficrun.client.ui;

import es.udc.ws.ficrun.client.service.ClientRunService;
import es.udc.ws.ficrun.client.service.ClientRunServiceFactory;
import es.udc.ws.ficrun.client.service.dto.ClientInscriptionDto;
import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.client.service.exceptions.*;
import es.udc.ws.ficrun.client.service.rest.RestClientRunService;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;


//Somos conscientes de que "run" es incorrecto y debería ser "race" :(
public class RunServiceClient {

    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }

        ClientRunService clientRunService = ClientRunServiceFactory.getService();

        if ("-findRace".equalsIgnoreCase(args[0])) {
            //[findRace] RunServiceClient -findRace <id>
            validateArgs(args, 2, new int[] {1});
            try {
                int numAdmissions = clientRunService.findRun(Long.parseLong(args[1]));
                System.out.println("The number of admissions for the career " + args[1] + " are " + numAdmissions);
            } catch (NumberFormatException | InstanceNotFoundException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-deliverNumber".equalsIgnoreCase(args[0])) {
            //[deliverNumber] RunServiceClient -deliverNumber <inscriptionId> <creditCardNumber>
            validateArgs(args, 3, new int[] {1});
            try {
                clientRunService.pickDorsal(Long.parseLong(args[1]),args[2]);
                //destacar que el nº de dorsal != nº de inscripción
                System.out.println("The dorsal associated to the inscription id " + args[1] + " was picked ");
            } catch (NumberFormatException | InstanceNotFoundException |
                    InputValidationException | ClientDorsalPickedException | ClientWrongCreditCardException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-register".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, new int[]{2});
            Long inscriptionId;
            try {
                inscriptionId = clientRunService.registerRun(args[1], Long.parseLong(args[2]), args[3]);
                System.out.println("Inscription with Id="+inscriptionId+"created");
            } catch (NumberFormatException | InstanceNotFoundException |
                    InputValidationException | ClientInscriptionClosedException |
                    ClientAlreadyRegisterException | ClientNoVacanciesException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-findRegisters".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{});
            try {
                List<ClientInscriptionDto> inscriptions = clientRunService.findInscriptions(args[1]);
                System.out.println("Found " + inscriptions.size() +
                        " inscriptions of the runner with email '" + args[1] + "'");
                for (int i = 0; i < inscriptions.size(); i++) {
                    ClientInscriptionDto inscriptionDto = inscriptions.get(i);
                    System.out.println("Inscription with id '" + inscriptionDto.getInscriptionId() +
                            "' has assigned the dorsal=" + inscriptionDto.getDorsal() +
                            " in the race with id '" + inscriptionDto.getRunId() + "'");
                }
            } catch (InstanceNotFoundException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-addRace".equalsIgnoreCase(args[0])) {
            validateArgs(args, 7, new int[] {5, 6});
            // [add] RunServiceClient -addRace <name> <city> <startDate> <description> <price> <maxRunners>

            try {
                Long runId = clientRunService.addRun(new ClientRunDto(null,
                        args[1], args[2], LocalDateTime.parse(args[3]),
                        args[4], Float.valueOf(args[5]), Integer.parseInt(args[6]),
                        Integer.parseInt(args[6])));

                System.out.println("Race with id: " + runId + " created sucessfully");

            } catch (NumberFormatException | DateTimeParseException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        } else if ("-findRaces".equalsIgnoreCase(args[0])){
            validateArgs(args, 3, new int[]{});
            // [findRaces] RunServiceClient -findRaces <beforeDate> <city>

            try {

                List<ClientRunDto> runs = clientRunService.findRuns(args[1], args[2]);
                System.out.println("Found " + runs.size() +
                        " race(s) that are going to celebrate before '" + args[1] + "' "
                        + "in '" + args[2] + "':");
                for (int i = 0; i < runs.size(); i++) {
                    ClientRunDto runDto = runs.get(i);
                    System.out.println("The number of admissions for the race with id '" + runDto.getRunID() +
                            "' that is going to celebrate in '" + runDto.getCity() + "' at '" + runDto.getStartDate()
                            + "' is " + runDto.getNumAdmissions());
                }

            } catch (DateTimeParseException | InputValidationException e){
                e.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else printUsageAndExit();
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [addRace] RunServiceClient -addRace <name> <city> <startDate> <description> <price> <maxRunners>\n" +
                "    [findRace] RunServiceClient -findRace <id>\n" +
                "    [findRaces] RunServiceClient -findRaces <beforeDate> <city>\n" +
                "    [deliverNumber] RunServiceClient -deliverNumber <inscriptionId> <creditCardNumber>\n" +
                "    [register] RunServiceClient -register <userEmail> <raceId> <creditCardNumber>" +
                "    [findRegisters] RunServiceClient -findRegisters <userEmail>"
        );
    }


    public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
        if(expectedArgs != args.length) {
            printUsageAndExit();
        }
        for(int i = 0 ; i< numericArguments.length ; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch(NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }
}

