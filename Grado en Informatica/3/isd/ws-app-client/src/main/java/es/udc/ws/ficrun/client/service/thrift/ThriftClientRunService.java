package es.udc.ws.ficrun.client.service.thrift;

import es.udc.ws.ficrun.client.service.ClientRunService;
import es.udc.ws.ficrun.client.service.dto.ClientInscriptionDto;
import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.client.service.exceptions.ClientDorsalPickedException;
import es.udc.ws.ficrun.client.service.exceptions.ClientInscriptionClosedException;
import es.udc.ws.ficrun.client.service.exceptions.ClientWrongCreditCardException;
import es.udc.ws.ficrun.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.List;

public class ThriftClientRunService implements ClientRunService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "ThriftClientRunService.endpointAddress";

    private final static String endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addRun(ClientRunDto run) throws InputValidationException{
        ThriftRunService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return client.addRun(ClientRunDtoToThriftRunDtoConversor.toThriftRunDto(run));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientRunDto> findRuns(String beforeDate, String city) throws InputValidationException {
        ThriftRunService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientRunDtoToThriftRunDtoConversor.toClientRunDtos(client.findRuns(beforeDate, city));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public int findRun(Long runID) throws InstanceNotFoundException {
        ThriftRunService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();
        try  {
            transport.open();
            return ClientRunDtoToThriftRunDtoConversor.toClientRunDto(client.findRun(runID)).getNumAdmissions();
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public void pickDorsal(Long codePickDorsal, String creditCardNumber) throws InputValidationException, ClientDorsalPickedException, ClientWrongCreditCardException, InstanceNotFoundException {
        ThriftRunService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {
            transport.open();
            client.pickDorsal(codePickDorsal,creditCardNumber);

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftDorsalPickedException e) {
            throw new ClientDorsalPickedException(e.getInscriptionId());
        } catch (ThriftWrongCreditCardException e) {
            throw new ClientWrongCreditCardException(e.getInscriptionId(),e.getCreditCardNumber());
        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientInscriptionDto> findInscriptions(String runnerEmail) throws InstanceNotFoundException {
        return null;
    }

    @Override
    public Long registerRun(String emailRunner, Long runId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, ClientInscriptionClosedException {
        return null;
    }

    private ThriftRunService.Client getClient() {
        try {
            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);
            return new ThriftRunService.Client(protocol);
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }
}
