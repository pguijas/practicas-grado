package es.udc.ws.ficrun.model.run;

import es.udc.ws.ficrun.model.inscription.Inscription;

import java.sql.*;

public class Jdbc3CcSqlRunDao extends AbstractSqlRunDao{

    @Override
    public Run create(Connection connection, Run run){
        String queryString = "INSERT INTO Run (name, city, startDate, creationDate, description, price, maxRunners, numInscriptions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)){
            //Rellenamos Información
            int i=1;
            preparedStatement.setString(i++, run.getName());
            preparedStatement.setString(i++, run.getCity());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(run.getStartDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(run.getCreationDate()));
            preparedStatement.setString(i++, run.getDescription());
            preparedStatement.setFloat(i++, run.getPrice());
            preparedStatement.setInt(i++, run.getMaxRunners());
            preparedStatement.setInt(i++, run.getNumInscriptions());

            preparedStatement.executeUpdate();
            //Recuperamos el id generado
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key.");
            }
            Long runId = resultSet.getLong(1);
            //Devolvemos Run con id
            return new Run(runId, run.getName(), run.getCity(), run.getStartDate(), run.getCreationDate(), run.getDescription(), run.getPrice(), run.getMaxRunners(), run.getNumInscriptions());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
