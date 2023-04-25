package es.udc.ws.ficrun.model.inscription;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.util.List;

public interface SqlInscriptionDao {
    public Inscription create(Connection connection, Inscription inscription);
    public List<Inscription> findInscriptions(Connection connection, String runnerEmail) throws InstanceNotFoundException;
    public void update(Connection connection, Inscription inscription) throws InstanceNotFoundException;
    public void remove(Connection connection, Long inscriptionId) throws InstanceNotFoundException;
    public Inscription findInscription(Connection connection, Long runId) throws InstanceNotFoundException;
    public boolean findInscriptionOne(Connection connection, Long runId, String runnerEmail) throws InputValidationException, InstanceNotFoundException;
}
