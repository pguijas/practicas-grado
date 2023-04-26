package es.udc.ws.ficrun.model.run;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlRunDao {
    public Run create(Connection connection, Run run);
    public Run findRun(Connection connection, Long runId) throws InstanceNotFoundException;
    public List<Run> findRuns(Connection connection, LocalDateTime currentDate, LocalDateTime beforeDate, String city);
    public void remove(Connection connection, Long runID) throws InstanceNotFoundException;
    public void update(Connection connection, Run run) throws InstanceNotFoundException;
}
