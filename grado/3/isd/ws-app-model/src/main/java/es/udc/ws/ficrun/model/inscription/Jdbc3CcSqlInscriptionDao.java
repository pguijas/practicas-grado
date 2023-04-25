package es.udc.ws.ficrun.model.inscription;

import java.sql.*;

public class Jdbc3CcSqlInscriptionDao extends AbstractSqlInscriptionDao{

    @Override
    public Inscription create(Connection connection, Inscription inscription){
        String queryString = "INSERT INTO Inscription (runId, runnerEmail, inscriptionDate, creditCardNumber, price, dorsal, dorsalPicked) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)){
            int i=1;
            preparedStatement.setLong(i++, inscription.getRunId());
            preparedStatement.setString(i++, inscription.getRunnerEmail());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setString(i++, inscription.getCreditCardNumber());
            preparedStatement.setFloat(i++, inscription.getPrice());
            preparedStatement.setInt(i++, inscription.getDorsal());
            preparedStatement.setBoolean(i++, inscription.isDorsalPicked());

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key.");
            }
            Long inscriptionId = resultSet.getLong(1);
            return new Inscription(inscriptionId, inscription.getRunId(), inscription.getRunnerEmail(), inscription.getInscriptionDate(), inscription.getCreditCardNumber(), inscription.getPrice(), inscription.getDorsal(), inscription.isDorsalPicked());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
