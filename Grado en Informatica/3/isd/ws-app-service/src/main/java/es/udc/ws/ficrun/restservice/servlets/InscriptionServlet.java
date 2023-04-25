package es.udc.ws.ficrun.restservice.servlets;

import es.udc.ws.ficrun.model.inscription.Inscription;
import es.udc.ws.ficrun.model.runservice.RunServiceFactory;
import es.udc.ws.ficrun.model.runservice.exceptions.*;
import es.udc.ws.ficrun.restservice.json.JsonToExceptionConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.ServletUtils;
import es.udc.ws.ficrun.restservice.dto.RestInscriptionDto;
import es.udc.ws.ficrun.restservice.dto.InscriptionToRestInscriptionDtoConversor;
import es.udc.ws.ficrun.restservice.json.JsonToRestInscriptionDtoConversor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class InscriptionServlet extends HttpServlet {

    private void pickDorsal(HttpServletResponse resp, String inscriptionIdAsString, String creditcard) throws ServletException, IOException {
            Long inscriptionId;
            try {
                inscriptionId = Long.valueOf(inscriptionIdAsString);
            } catch (NumberFormatException ex) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        JsonToExceptionConversor.toInputValidationException(new InputValidationException(
                                "Invalid Request: " + "invalid inscription id '" + inscriptionIdAsString + "'")),
                        null);
                return;
            }
            try {
                RunServiceFactory.getService().pickDorsal(inscriptionId,creditcard);
            } catch (InstanceNotFoundException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        JsonToExceptionConversor.toInstanceNotFoundException(e), null);
            } catch (InputValidationException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        JsonToExceptionConversor.toInputValidationException(e), null);
            } catch (WrongCreditCardException e) {
                //Forbidden dado que su temporalidad es permanente. La petición se entendió pero no se puede realizar
                // dado que la tarjeta de crédito no es correcta
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        JsonToExceptionConversor.toWrongCreditCardException(e), null);
            } catch (DorsalPickedException e) {
                //Forbidden dado que su temporalidad es permanente. La petición se entendió pero no se puede realizar
                // dado que ya se realizó
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                        JsonToExceptionConversor.toDorsalPickedException(e), null);
            }
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
    }

    @Override
    //registerRun + PickDorsal (/id/pickdorsal)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path != null && path.length() > 0) {
            /*
                /id/pickdorsal --(split("/"))--> [] [id] [pickdorsal] (3 elementos min) (con /final serian 4)
                Si entra en el if, relegamos en pickDorsal, si no badrequest
            */
            if (path.split("/").length>=3)
                if (path.split("/")[2].equals("pickdorsal")){
                    pickDorsal(resp,path.split("/")[1],req.getParameter("creditCardNumber"));
                    return;
                }
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: " + "invalid path " + path)),
                    null);
            return;
        }

        String runnerEmail = req.getParameter("runnerEmail");
        if (runnerEmail == null) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: "+ "parameter 'runnerEmail' is mandatory")),
                    null);
            return;
        }
        String creditCardNumber = req.getParameter("creditCardNumber");
        if (creditCardNumber == null) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: "+ "parameter 'creditCardNumber' is mandatory")),
                    null);
            return;
        }
        String runIdParameter = req.getParameter("runId");
        if (runIdParameter == null) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: "+ "parameter 'runId' is mandatory")),
                    null);
            return;
        }
        Long runId;
        try{
            runId = Long.valueOf(runIdParameter);
        }catch (NumberFormatException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: "+ "parameter 'runId' is invalid '" + runIdParameter + "'")),
                    null);
            return;
        }
        Inscription inscription = null;
        try{
            inscription = RunServiceFactory.getService().registerRun(runnerEmail, runId, creditCardNumber);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toInstanceNotFoundException(ex), null);
            return;
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toInputValidationException(ex), null);
            return;
        } catch (InscriptionClosedException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toInscriptionClosedException(ex), null);
            return;
        } catch (AlreadyRegisterException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toAlreadyRegisterException(ex), null);
            return;

        } catch (NoVacanciesException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toNoVacanciesException(ex), null);
            return;
        }
        RestInscriptionDto inscriptionDto = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDto(inscription);

        String inscriptionURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + inscription.getInscriptionId();

        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", inscriptionURL);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestInscriptionDtoConversor.toObjectNode(inscriptionDto), headers);
    }

    @Override
    //FindInscription
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path == null || path.length() == 0) {
            try{
                String runnerEmail = req.getParameter("runnerEmail");
                List<Inscription> inscriptions = RunServiceFactory.getService().findInscriptions(runnerEmail);
                List<RestInscriptionDto> inscriptionDtos = InscriptionToRestInscriptionDtoConversor.toRestInscriptionDtos(inscriptions);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestInscriptionDtoConversor.toArrayNode(inscriptionDtos), null);
            } catch (InputValidationException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        JsonToExceptionConversor.toInputValidationException(e),null);
            } catch (InstanceNotFoundException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        JsonToExceptionConversor.toInstanceNotFoundException(e),null);
            }
        } else {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: " + "invalid path " + path)),
                    null);
        }
    }
}
