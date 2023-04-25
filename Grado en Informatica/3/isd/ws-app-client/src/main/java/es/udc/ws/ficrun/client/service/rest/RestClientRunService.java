package es.udc.ws.ficrun.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.ficrun.client.service.ClientRunService;
import es.udc.ws.ficrun.client.service.dto.ClientInscriptionDto;
import es.udc.ws.ficrun.client.service.dto.ClientRunDto;
import es.udc.ws.ficrun.client.service.exceptions.*;
import es.udc.ws.ficrun.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.ficrun.client.service.rest.json.JsonToClientInscriptionDtoConversor;
import es.udc.ws.ficrun.client.service.rest.json.JsonToClientRunDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

public class RestClientRunService implements ClientRunService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientRunService.endpointAddress";
    private String endpointAddress;

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientRunDto run) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, JsonToClientRunDtoConversor.toObjectNode(run));
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            /* Success? */
            if (statusCode == successCode) {
                return;
            }
            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());

                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());

                default:
                    throw new RuntimeException("HTTP error; status code = "
                            + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Long addRun(ClientRunDto run) throws InputValidationException {

        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "run").
                    bodyStream(toInputStream(run), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientRunDtoConversor.toClientRunDto(response.getEntity().getContent()).getRunID();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ClientRunDto> findRuns(String beforeDate, String city) throws InputValidationException{

        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "run?beforedate="
                    + URLEncoder.encode(beforeDate, "UTF-8") + "&city="
                    + URLEncoder.encode(city, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientRunDtoConversor.toClientRunDtos(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }




    //Pedro
    //Devuelve el nº de plazas libres
    @Override
    public int findRun(Long runId) throws InstanceNotFoundException{
        try {
            HttpResponse response = Request.Get(getEndpointAddress() + "run/" + runId).execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientRunDtoConversor.toClientRunDto(response.getEntity().getContent()).getNumAdmissions();

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //Pedro
    //casques:
    //SC_NO_CONTENT -> exito
    @Override
    public void pickDorsal(Long codePickDorsal, String creditCardNumber)
            throws InputValidationException, ClientDorsalPickedException,
            ClientWrongCreditCardException, InstanceNotFoundException
    {

        try {
            HttpResponse response = Request.Post(
                    getEndpointAddress() + "inscription" + "/" + codePickDorsal + "/pickdorsal"
                ).bodyForm(
                    Form.form().add("creditCardNumber", creditCardNumber).build()
                ).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InputValidationException | InstanceNotFoundException | ClientDorsalPickedException | ClientWrongCreditCardException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<ClientInscriptionDto> findInscriptions(String runnerEmail) throws InputValidationException, InstanceNotFoundException{
        try {
            HttpResponse response = Request.Get(getEndpointAddress() + "inscription?runnerEmail="
                    + URLEncoder.encode(runnerEmail, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientInscriptionDtoConversor.toClientInscriptionDtos(response.getEntity()
                    .getContent());

        } catch (InputValidationException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long registerRun(String runnerEmail, Long runId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, ClientInscriptionClosedException, ClientAlreadyRegisterException, ClientNoVacanciesException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "inscription").
                    bodyForm(
                            Form.form().
                                    add("runnerEmail", runnerEmail).
                                    add("runId", Long.toString(runId)).
                                    add("creditCardNumber", creditCardNumber).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);
            return JsonToClientInscriptionDtoConversor.toClientInscriptionDto(
                    response.getEntity().getContent()).getInscriptionId();

        } catch (InputValidationException | InstanceNotFoundException | ClientInscriptionClosedException | ClientAlreadyRegisterException | ClientNoVacanciesException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
