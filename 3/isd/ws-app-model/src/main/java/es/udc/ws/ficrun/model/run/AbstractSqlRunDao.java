package es.udc.ws.ficrun.model.run;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;

import es.udc.ws.ficrun.model.inscription.Inscription;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlRunDao implements SqlRunDao{

    protected AbstractSqlRunDao() {}

    @Override
    public Run findRun(Connection connection, Long runId) throws InstanceNotFoundException{
        //Creamos la Query
        String queryString = "SELECT name, city, startDate, creationDate, description, price, maxRunners, numInscriptions FROM Run WHERE runID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setLong(i++, runId.longValue());
            //Ejecutamos la Query
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                throw new InstanceNotFoundException(runId, this.getClass().getName());
            }
            //Obtenemos Resultados
            i = 1;
            String name = resultSet.getString(i++);
            String city = resultSet.getString(i++);
            Timestamp startDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime startDate = startDateAsTimestamp.toLocalDateTime();
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
            String description = resultSet.getString(i++);
            float price = resultSet.getFloat(i++);
            int maxRunners = resultSet.getInt(i++);
            int numInscriptions = resultSet.getInt(i++);
            //Devolvemos Resultados
            return new Run(runId, name, city, startDate, creationDate, description, price, maxRunners, numInscriptions);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Run> findRuns(Connection connection, LocalDateTime currentDate, LocalDateTime beforeDate, String city){

        //Creamos query
        String queryString = "SELECT runId, name, city, startDate, creationDate, description, price, maxRunners, numInscriptions "
                + " FROM Run ";
        queryString += "WHERE DATE(startDate) >= DATE(?) AND DATE(startDate) <= DATE(?)";
        if (city != null)
            queryString += " AND city = ?";


        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i=1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(currentDate));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(beforeDate));
            if (city != null)
                preparedStatement.setString(i++, city);

            //Ejecutamos query
            ResultSet resultSet = preparedStatement.executeQuery();

            //Leémos carreras
            List<Run> runs = new ArrayList<Run>();

            while (resultSet.next()) {

                i = 1;
                Long runId = Long.valueOf(resultSet.getLong(i++));
                String name = resultSet.getString(i++);
                String city_c = resultSet.getString(i++);
                Timestamp startDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime startDate = startDateAsTimestamp.toLocalDateTime();
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                String description = resultSet.getString(i++);
                float price = resultSet.getFloat(i++);
                int maxRunners = resultSet.getInt(i++);
                int numInscriptions = resultSet.getInt(i++);

                runs.add(new Run(runId, name, city_c, startDate, creationDate, description, price, maxRunners, numInscriptions));

            }

            // Devolvemos carreras
            return runs;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long runId) throws InstanceNotFoundException{
        String queryString = "DELETE FROM Run WHERE runId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setLong(i++,runId);

            int removedRows = preparedStatement.executeUpdate();

            if(removedRows == 0){
                throw new InstanceNotFoundException(runId, Run.class.getName());
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Run run) throws InstanceNotFoundException{
        String queryString = "UPDATE Run SET name = ?, city = ?, startDate = ?, description = ?, price = ?, maxRunners = ?, numInscriptions = ? WHERE runId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            int i=1;
            preparedStatement.setString(i++, run.getName());
            preparedStatement.setString(i++, run.getCity());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(run.getStartDate()));
            preparedStatement.setString(i++, run.getDescription());
            preparedStatement.setFloat(i++, run.getPrice());
            preparedStatement.setInt(i++, run.getMaxRunners());
            preparedStatement.setInt(i++, run.getNumInscriptions());

            preparedStatement.setFloat(i++, run.getRunID());

            int updateRows = preparedStatement.executeUpdate();

            if (updateRows == 0) {
                throw new InstanceNotFoundException(run.getRunID(),Run.class.getName());
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
