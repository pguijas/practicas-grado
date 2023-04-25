package es.udc.ws.ficrun.test.model.runservice;


import es.udc.ws.ficrun.model.inscription.Inscription;
import es.udc.ws.ficrun.model.inscription.SqlInscriptionDao;
import es.udc.ws.ficrun.model.inscription.SqlInscriptionDaoFactory;
import es.udc.ws.ficrun.model.run.Run;
import es.udc.ws.ficrun.model.run.SqlRunDao;
import es.udc.ws.ficrun.model.run.SqlRunDaoFactory;
import es.udc.ws.ficrun.model.runservice.RunService;
import es.udc.ws.ficrun.model.runservice.RunServiceFactory;
import es.udc.ws.ficrun.model.runservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static es.udc.ws.ficrun.model.util.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.LinkedList;
import java.util.List;

public class RunServiceTest {
    private final long NON_EXISTENT_RUN_ID = -1;
    private final long NON_EXISTENT_INSCRIPTION_ID = -1;

    private final String VALID_CREDIT_CARD_NUMBER = "1234567890123456";
    private final String VALID_CREDIT_CARD_NUMBER_2 = "1234567890123459";
    private final String INVALID_CREDIT_CARD_NUMBER = "";


    private final String USER_EMAIL = "julio@gmail.com";
    private final String INVALID_USER_EMAIL = "";

    private final LocalDate INVALID_BEFORE_DATE = LocalDate.now().minusDays(1);


    private static RunService runService = null;

    private static SqlInscriptionDao inscriptionDao = null;
    private static SqlRunDao runDao = null;

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(RUN_DATA_SOURCE, dataSource);

        runService = RunServiceFactory.getService();
        runDao = SqlRunDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }
    
    private Run getValidRun(String name, LocalDateTime startDate, String city){
        return new Run(name, city, startDate,
                "description", 33, 200, 0);
        
    }

    private Run getValidRun() {
        return getValidRun("Carrera", LocalDateTime.of(2030,1,1,1,1), "city");
    }

    private Run createRun(Run run) {
        Run addedRun = null;
        try {
            addedRun = runService.addRun(run);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedRun;
    }

    private void removeRun(Long runId) {
        //Como no lo tenemos en Service, tenemos que acceder directamente al dao
        DataSource dataSource = DataSourceLocator.getDataSource(RUN_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                runDao.remove(connection, runId);
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeInscription(Long inscriptionId) {
        //Como no lo tenemos en Service, tenemos que acceder directamente al dao
        DataSource dataSource = DataSourceLocator.getDataSource(RUN_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                inscriptionDao.remove(connection, inscriptionId);
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Este no se pa que lo vamos a querer, si no se usa se borra
    private void updateRun(Run run) {
        DataSource dataSource = DataSourceLocator.getDataSource(RUN_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                runDao.update(connection, run);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //findRun
    @Test
    public void testAddRunAndFindRun() throws InputValidationException, InstanceNotFoundException{
        Run run = getValidRun();
        Run addedRun = null;

        try {

            // Create Movie
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedRun = runService.addRun(run);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            // Find Movie
            Run foundRun = runService.findRun(addedRun.getRunID());

            assertEquals(addedRun, foundRun);
            assertEquals(foundRun.getName(),run.getName());
            assertEquals(foundRun.getCity(),run.getCity());
            assertEquals(foundRun.getStartDate(),run.getStartDate());
            assertEquals(foundRun.getDescription(),run.getDescription());
            assertEquals(foundRun.getPrice(),run.getPrice());
            assertEquals(foundRun.getMaxRunners(),run.getMaxRunners());
            assertEquals(foundRun.getNumInscriptions(),run.getNumInscriptions());
            assertEquals(foundRun.getPrice(),run.getPrice());
            assertTrue((foundRun.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundRun.getCreationDate().compareTo(afterCreationDate) <= 0));

        } finally {
            // Clear Database
            if (addedRun!=null) {
                removeRun(addedRun.getRunID());
            }
        }
    }

    @Test
    public void testAddInvalidRun() {

        // Check título no nulo
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setName(null);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check título no vacío
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setName("");
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check city no nula
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setCity(null);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check city no vacía
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setCity("");
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        //Check fecha de carrera no sea inferior al día actual + 1 día
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setStartDate(LocalDateTime.now());
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check descripción no nulo
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setDescription(null);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check descripción no vacío
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setDescription("");
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check precio >= 0
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setPrice((short) -1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check precio <= MAX_PRICE
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setPrice((short) MAX_PRICE+1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check maxímo número de corredores > 0
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setMaxRunners(0);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check máximo número de corredores <= MAX_RUNNERS
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setMaxRunners(MAX_RUNNERS+1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check número inscripciones >= 0
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setNumInscriptions(-1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check número inscripciones <= MAX_INSCRIPTIONS
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setNumInscriptions(MAX_INSCRIPTIONS+1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

        // Check número inscripciones <= num of max runners
        assertThrows(InputValidationException.class, () -> {
            Run run = getValidRun();
            run.setNumInscriptions(run.getMaxRunners()+1);
            Run runAdded = runService.addRun(run);
            removeRun(runAdded.getRunID());
        });

    }

    @Test
    public void testFindNonExistentRun() {
        assertThrows(InstanceNotFoundException.class, () -> runService.findRun(NON_EXISTENT_RUN_ID));
    }

    @Test
    public void testFindRuns() throws InputValidationException {

        // Add movies
        List<Run> runs = new LinkedList<Run>();
        LocalDateTime currentDay = LocalDateTime.now().withNano(0);
        LocalDate currentD = LocalDate.now();
        Run run1 = createRun(getValidRun("Carrera1", currentDay.plusMonths(1), "city1"));
        runs.add(run1);
        Run run2 = createRun(getValidRun("Carrera2", currentDay.plusMonths(2), "city1"));
        runs.add(run2);
        Run run3 = createRun(getValidRun("Carrera3", currentDay.plusMonths(3), "city2"));
        runs.add(run3);

        try {

            List<Run> foundRuns = runService.findRuns(currentD.plusMonths(3), null);
            assertEquals(runs, foundRuns);

            foundRuns = runService.findRuns(currentD.plusMonths(2), null);
            assertEquals(2, foundRuns.size());
            assertEquals(runs.get(0), foundRuns.get(0));
            assertEquals(runs.get(1), foundRuns.get(1));

            foundRuns = runService.findRuns(currentD.plusMonths(1), null);
            assertEquals(1, foundRuns.size());
            assertEquals(runs.get(0), foundRuns.get(0));

            foundRuns = runService.findRuns(currentD.plusDays(10), null);
            assertEquals(0, foundRuns.size());

            foundRuns = runService.findRuns(currentD.plusMonths(3), "city1");
            assertEquals(2, foundRuns.size());
            assertEquals(runs.get(0), foundRuns.get(0));
            assertEquals(runs.get(1), foundRuns.get(1));

            foundRuns = runService.findRuns(currentD.plusMonths(3), "city2");
            assertEquals(1, foundRuns.size());
            assertEquals(runs.get(2), foundRuns.get(0));

            foundRuns = runService.findRuns(currentD.plusMonths(3), "city3");
            assertEquals(0, foundRuns.size());

        } finally {
            // Clear Database
            for (Run run : runs) {
                removeRun(run.getRunID());
            }
        }

    }

    @Test
    public void testFindRunsInputValidationException() {
        assertThrows(InputValidationException.class, () -> runService.findRuns(INVALID_BEFORE_DATE, null));
        assertThrows(InputValidationException.class, () -> runService.findRuns(LocalDate.now(), " "));
    }

    @Test
    public void testRegisterRunAndFindInscriptions() throws InstanceNotFoundException, InputValidationException, InscriptionClosedException, AlreadyRegisterException, NoVacanciesException {
        Run run = createRun(getValidRun());
        Inscription inscription = null;
        int numi=run.getNumInscriptions()+1;
        try{
            LocalDateTime beforeRegister = LocalDateTime.now().withNano(0);
            inscription = runService.registerRun(USER_EMAIL, run.getRunID(), VALID_CREDIT_CARD_NUMBER);
            LocalDateTime afterRegister = LocalDateTime.now().withNano(0);

            List<Inscription> foundInscription = runService.findInscriptions(USER_EMAIL);
            assertEquals(inscription, foundInscription.get(0));
            assertEquals(VALID_CREDIT_CARD_NUMBER, foundInscription.get(0).getCreditCardNumber());
            assertEquals(USER_EMAIL, foundInscription.get(0).getRunnerEmail());
            assertEquals(run.getRunID(), foundInscription.get(0).getRunId());
            assertTrue(numi==foundInscription.get(0).getDorsal());
            assertTrue(run.getPrice()==foundInscription.get(0).getPrice());
            assertTrue((foundInscription.get(0).getInscriptionDate().compareTo(beforeRegister)>=0)&&(foundInscription.get(0).getInscriptionDate().compareTo(afterRegister)<=0));
        }finally{
            if (inscription!=null){
                removeInscription(inscription.getInscriptionId());
            }
            removeRun(run.getRunID());
        }
    }


    @Test
    public void testregisterwithinvalidcreditcard(){
        Run run = createRun(getValidRun());
        try{
            assertThrows(InputValidationException.class, () -> {
                Inscription inscription = runService.registerRun(USER_EMAIL, run.getRunID(), INVALID_CREDIT_CARD_NUMBER);
                removeInscription(inscription.getInscriptionId());
            });
        }finally{
            removeRun(run.getRunID());
        }
    }

    @Test
    public void testregisternonexistentRun(){
        assertThrows(InstanceNotFoundException.class, () -> {
            Inscription inscription = runService.registerRun(USER_EMAIL, NON_EXISTENT_RUN_ID, VALID_CREDIT_CARD_NUMBER);
            removeInscription(inscription.getInscriptionId());
        });
    }

    @Test
    public void testregisternonexistentemail(){
        Run run = createRun(getValidRun());
        try{
            assertThrows(InputValidationException.class, () -> {
                Inscription inscription = runService.registerRun(INVALID_USER_EMAIL, run.getRunID(), VALID_CREDIT_CARD_NUMBER);
                removeInscription(inscription.getInscriptionId());
            });
        }finally{
            removeRun(run.getRunID());
        }
    }



    //pickDorsal
    @Test
    public void testPickDorsalWith() throws InputValidationException, InstanceNotFoundException, DorsalPickedException, WrongCreditCardException, InscriptionClosedException, AlreadyRegisterException, NoVacanciesException {

        Run run = createRun(getValidRun());
        Inscription inscription = null;
        try {

            inscription = runService.registerRun("paco@gmail.com",run.getRunID(),VALID_CREDIT_CARD_NUMBER);
            Inscription finalInscription = inscription;

            assertThrows(InputValidationException.class, () ->
                    runService.pickDorsal(finalInscription.getInscriptionId(), INVALID_CREDIT_CARD_NUMBER)
            );

            assertThrows(InstanceNotFoundException.class, () ->
                    runService.pickDorsal(NON_EXISTENT_INSCRIPTION_ID, VALID_CREDIT_CARD_NUMBER)
            );

            //Comprobamos que no se recogio
            assertEquals(runService.findInscriptions("paco@gmail.com").get(0).isDorsalPicked(),false);

            runService.pickDorsal(finalInscription.getInscriptionId(), VALID_CREDIT_CARD_NUMBER);

            //Comprobamos que no se recogio
            assertEquals(runService.findInscriptions("paco@gmail.com").get(0).isDorsalPicked(),true);

            assertThrows(DorsalPickedException.class, () ->
                    runService.pickDorsal(finalInscription.getInscriptionId(), VALID_CREDIT_CARD_NUMBER)
            );

            assertThrows(WrongCreditCardException.class, () ->
                    runService.pickDorsal(finalInscription.getInscriptionId(), VALID_CREDIT_CARD_NUMBER_2)
            );

        } finally {

            if (inscription!=null){
                removeInscription(inscription.getInscriptionId());
            }
            removeRun(run.getRunID());

        }
    }

}
