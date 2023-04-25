package es.udc.ws.ficrun.model.runservice;

import es.udc.ws.ficrun.model.inscription.Inscription;
import es.udc.ws.ficrun.model.inscription.SqlInscriptionDao;
import es.udc.ws.ficrun.model.inscription.SqlInscriptionDaoFactory;
import es.udc.ws.ficrun.model.run.Run;
import es.udc.ws.ficrun.model.run.SqlRunDao;
import es.udc.ws.ficrun.model.run.SqlRunDaoFactory;


import es.udc.ws.ficrun.model.runservice.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.*;

import static es.udc.ws.ficrun.model.util.ModelConstants.*;

public class RunServiceImpl implements RunService{

    private final DataSource dataSource;
    private SqlRunDao runDao = null;
    private SqlInscriptionDao inscriptionDao = null;

    public RunServiceImpl(){
        dataSource = DataSourceLocator.getDataSource(RUN_DATA_SOURCE);
        runDao = SqlRunDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }

    private void validateRun(Run run) throws InputValidationException {
        PropertyValidator.validateMandatoryString("name", run.getName());
        PropertyValidator.validateMandatoryString("city", run.getCity());
        PropertyValidator.validateMandatoryString("description", run.getDescription());
        PropertyValidator.validateDouble("price", run.getPrice(), 0, MAX_PRICE);
        PropertyValidator.validateDouble("maxRunners", run.getMaxRunners(), 1, MAX_RUNNERS);
        PropertyValidator.validateDouble("numInscriptions", run.getNumInscriptions(), 0, MAX_INSCRIPTIONS);
        if(run.getNumInscriptions()>=run.getMaxRunners()){
            throw new InputValidationException("Number of inscriptions is not lower than the max number of runners allowed");
        }
    }

    private void validateEmail(String runnerEmail) throws InputValidationException{
        if (runnerEmail.contains("@")){
            if (runnerEmail.indexOf("@")==0 || runnerEmail.indexOf("@")==runnerEmail.length()){
                throw new InputValidationException("The email is not valid");
            }
        } else {
            throw new InputValidationException("The email is not valid");
        }
    }


    @Override
    public Run addRun(Run run) throws InputValidationException{
        validateRun(run);
        LocalDateTime currentDay = LocalDateTime.now().withNano(0);
        LocalDateTime startDate = run.getStartDate();
        if(startDate.isBefore(currentDay.plusDays(1))){
            throw new InputValidationException("Start date: " + startDate + " is before " + currentDay.plusDays(1));
        }
        run.setCreationDate(currentDay);

        try (Connection connection = dataSource.getConnection()) {

            try {

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Run createdRun = runDao.create(connection, run);

                connection.commit();

                return createdRun;

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

    @Override
    //Pedro
    public Run findRun(Long runID) throws InstanceNotFoundException{
        try (Connection connection = this.dataSource.getConnection()){
            //nº de inscritos se encuentra en run (redundante pero merece la pena)
            return runDao.findRun(connection, runID);
        } catch (SQLException e){
            throw new RuntimeException();
        }
    }

    @Override
    public List<Run> findRuns(LocalDate beforeDate, String city) throws InputValidationException {
        if(city!=null) PropertyValidator.validateMandatoryString("city", city);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime beforeD = beforeDate.atTime(23,59,59);
        if (!beforeD.isBefore(currentDate)){
            try (Connection connection = dataSource.getConnection()) {
                return runDao.findRuns(connection, currentDate, beforeD, city);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else throw new InputValidationException("Inserted date: " + beforeDate + " is before current date: " + currentDate);
    }

    @Override
    public Inscription registerRun(String runnerEmail, Long runId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, InscriptionClosedException , AlreadyRegisterException, NoVacanciesException{
        PropertyValidator.validateCreditCard(creditCardNumber);
        validateEmail(runnerEmail);
        try (Connection connection = dataSource.getConnection()){
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Run run = runDao.findRun(connection, runId);
                if (run.getNumInscriptions() == run.getMaxRunners()){
                    throw new NoVacanciesException(run.getMaxRunners(),run.getNumInscriptions());
                }
                if (inscriptionDao.findInscriptionOne(connection, runId, runnerEmail)){
                    throw new AlreadyRegisterException(runnerEmail);
                }
                if (!LocalDateTime.now().plusDays(1).isBefore(run.getStartDate())){
                    throw new InscriptionClosedException(runId);
                }
                run.setNumInscriptions(run.getNumInscriptions()+1);
                runDao.update(connection,run);
                Inscription inscription = inscriptionDao.create(connection, new Inscription(runId, runnerEmail, LocalDateTime.now().withNano(0), creditCardNumber, run.getPrice(), run.getNumInscriptions(), false));
                connection.commit();
                return inscription;
            }catch(InstanceNotFoundException e){
                connection.commit();
                throw e;
            }catch(NoVacanciesException e) {
                connection.commit();
                throw e;
            }catch(AlreadyRegisterException e) {
                connection.commit();
                throw e;
            }catch(InscriptionClosedException e) {
                connection.commit();
                throw e;
            }catch(SQLException e){
                connection.rollback();
                throw new RuntimeException(e);
            }catch (RuntimeException|Error e){
                connection.rollback();
                throw e;
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Inscription> findInscriptions(String runnerEmail) throws InstanceNotFoundException, InputValidationException {
        validateEmail(runnerEmail);
        try (Connection connection = dataSource.getConnection()) {
            return inscriptionDao.findInscriptions(connection, runnerEmail);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    //Pedro
    public void pickDorsal(Long codePickDorsal, String creditCardNumber) throws InputValidationException,
            InstanceNotFoundException, DorsalPickedException, WrongCreditCardException {

        PropertyValidator.validateCreditCard(creditCardNumber);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Inscription inscription = inscriptionDao.findInscription(connection, codePickDorsal);
                //Comprobamos que la información sea correcta
                if (inscription.getCreditCardNumber().equals(creditCardNumber)){
                    //Comprobamos que no lo recogiese nadie
                    if (!inscription.isDorsalPicked()){
                        inscription.setDorsalPicked(true);
                        inscriptionDao.update(connection,inscription);
                    } else
                        throw new DorsalPickedException(codePickDorsal);
                } else {
                    throw new WrongCreditCardException(codePickDorsal, creditCardNumber);
                }
                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (DorsalPickedException e) {
                connection.commit();
                throw e;
            } catch (WrongCreditCardException e) {
                connection.commit();
                throw e;
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
}
