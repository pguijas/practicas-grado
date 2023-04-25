package es.udc.ws.ficrun.restservice.servlets;

import es.udc.ws.ficrun.model.run.Run;
import es.udc.ws.ficrun.model.runservice.RunServiceFactory;
import es.udc.ws.ficrun.restservice.dto.RestRunDto;
import es.udc.ws.ficrun.restservice.dto.RunToRestRunDtoConversor;
import es.udc.ws.ficrun.restservice.json.JsonToExceptionConversor;
import es.udc.ws.ficrun.restservice.json.JsonToRestRunDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.exceptions.ParsingException;
import es.udc.ws.util.servlet.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path != null && path.length() > 0) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(
                            new InputValidationException("Invalid Request: " + "invalid path " + path)),
                    null);
            return;
        }
        RestRunDto runDto;
        try {
            runDto = JsonToRestRunDtoConversor.toServiceRunDto(req.getInputStream());
        } catch (ParsingException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST, JsonToExceptionConversor
                    .toInputValidationException(new InputValidationException(ex.getMessage())), null);
            return;
        }
        Run run = RunToRestRunDtoConversor.toRun(runDto);
        try {
            run = RunServiceFactory.getService().addRun(run);
        } catch (InputValidationException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(ex), null);
            return;
        }
        runDto = RunToRestRunDtoConversor.toRestRunDto(run);

        String runURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + run.getRunID();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", runURL);

        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestRunDtoConversor.toObjectNode(runDto), headers);

    }

    @Override
    //FindRun + FindRuns
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = ServletUtils.normalizePath(req.getPathInfo());
        if (path == null || path.length() == 0) {
            //FindRuns
            String beforeDateStr = req.getParameter("beforedate");
            String city = req.getParameter("city");
            //filtro si hay parametros o no los hay (si los hay es obligatorio que estén los dos: beforeDate y city)
            if((beforeDateStr!=null && city!=null && !city.isBlank()) || (beforeDateStr==null && city==null)){
                try{
                    //fecha máxima en caso de que no se llame a findRuns
                    LocalDate beforeDate = beforeDateStr==null ? LocalDate.of(9999, 12, 30): LocalDate.parse(beforeDateStr);
                    List<Run> runs = RunServiceFactory.getService().findRuns(beforeDate, city);
                    List<RestRunDto> runDtos = RunToRestRunDtoConversor.toRestRunDtos(runs);
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                            JsonToRestRunDtoConversor.toArrayNode(runDtos), null);
                }catch(DateTimeParseException e){
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                            JsonToExceptionConversor.toInputValidationException(new InputValidationException(
                                    "Invalid Request: " + "Invalid date format. Date format must be 'yyyy-MM-dd'")),
                            null);
                }catch (InputValidationException e){
                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                            JsonToExceptionConversor.toInputValidationException(e),null);
                }
            }
            else{
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        JsonToExceptionConversor.toInputValidationException(new InputValidationException(
                                "Invalid Request: " + "Parameters required are beforedate and city.")),
                        null);
            }
            return;
        }
        //FindRun
        String id = path.substring(1); //Borramos la / de /id
        try {
            Run run = RunServiceFactory.getService().findRun(Long.valueOf(id));
            RestRunDto runDto = RunToRestRunDtoConversor.toRestRunDto(run);
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestRunDtoConversor.toObjectNode(runDto), null);
        } catch (NumberFormatException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    JsonToExceptionConversor.toInputValidationException(new InputValidationException(
                            "Invalid Request: " + "invalid run id '" + id + "'")),
                    null);
        } catch (InstanceNotFoundException ex) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                    JsonToExceptionConversor.toInstanceNotFoundException(ex), null);
        }
    }
}

