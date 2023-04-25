package es.udc.ws.ficrun.model.inscription;

import es.udc.ws.ficrun.model.run.Run;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.validation.PropertyValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractSqlInscriptionDao implements SqlInscriptionDao{

    protected AbstractSqlInscriptionDao() {}

    @Override
    public List<Inscription> findInscriptions(Connection connection, String runnerEmail) throws InstanceNotFoundException{
        String queryString = "SELECT inscriptionId, runId, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked FROM Inscription "
                + "WHERE runnerEmail = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setString(i++, runnerEmail);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Inscription> inscription = new ArrayList<Inscription>();
            while (resultSet.next()){
                i=1;
                long inscriptionId = resultSet.getLong(i++);
                long runId = resultSet.getLong(i++);
                Timestamp inscriptionDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime inscriptionDate = inscriptionDateAsTimestamp.toLocalDateTime();
                String creditCardNumber = resultSet.getString(i++);
                float price = resultSet.getFloat(i++);
                int dorsal = resultSet.getInt(i++);
                boolean dorsalPicked = resultSet.getBoolean(i++);
                inscription.add(new Inscription(inscriptionId, runId, runnerEmail, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked));
            }

            return inscription;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Inscription inscription) throws InstanceNotFoundException{
        String queryString = "UPDATE Inscription SET runnerEmail = ?, inscriptionDate = ?, creditCardNumber = ?, price = ?, dorsal = ?, dorsalPicked = ? WHERE inscriptionID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setString(i++, inscription.getRunnerEmail());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setString(i++, inscription.getCreditCardNumber());
            preparedStatement.setFloat(i++, inscription.getPrice());
            preparedStatement.setInt(i++, inscription.getDorsal());
            preparedStatement.setBoolean(i++, inscription.isDorsalPicked());

            preparedStatement.setFloat(i++, inscription.getInscriptionId());

            int updateRows = preparedStatement.executeUpdate();

            if (updateRows == 0) {
                throw new InstanceNotFoundException(inscription.getInscriptionId(),Inscription.class.getName());
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long inscriptionId) throws InstanceNotFoundException{
        String queryString = "DELETE FROM Inscription WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setLong(i++,inscriptionId);

            int removedRows = preparedStatement.executeUpdate();

            if(removedRows == 0){
                throw new InstanceNotFoundException(inscriptionId, Inscription.class.getName());
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Inscription findInscription(Connection connection, Long inscriptionId) throws InstanceNotFoundException{
        String queryString = "SELECT runId, runnerEmail, inscriptionDate, creditCardNumber," +
                "price, dorsal, dorsalPicked FROM Inscription WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setLong(i++, inscriptionId.longValue());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                throw new InstanceNotFoundException(inscriptionId, Inscription.class.getName());
            }

            //Resultados
            i = 1;
            long runId = resultSet.getLong(i++);
            String runnerEmail = resultSet.getString(i++);
            Timestamp inscriptionDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime inscriptionDate = inscriptionDateAsTimestamp.toLocalDateTime();
            String creditCardNumber = resultSet.getString(i++);
            float price = resultSet.getFloat(i++);
            int dorsal = resultSet.getInt(i++);
            boolean dorsalPicked = resultSet.getBoolean(i++);

            return new Inscription(inscriptionId, runId, runnerEmail, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findInscriptionOne(Connection connection, Long runId, String runnerEmail) throws InputValidationException,InstanceNotFoundException {
        PropertyValidator.validateMandatoryString("email", runnerEmail);
        for (Inscription aux : findInscriptions(connection, runnerEmail)) {
            if ((aux.getRunnerEmail().equals(runnerEmail)) && (aux.getRunId().equals(runId))) {
                return true;
            }
        }
        return false;
    }
}
