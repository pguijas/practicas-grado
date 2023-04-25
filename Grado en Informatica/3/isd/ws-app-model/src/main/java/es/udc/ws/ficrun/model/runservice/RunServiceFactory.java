package es.udc.ws.ficrun.model.runservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class RunServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "RunServiceFactory.className";
    private static RunService service = null;

    private RunServiceFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static RunService getInstance() {
        try {
            String serviceClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (RunService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static RunService getService() {

        if (service == null) {
            service = getInstance();
        }
        return service;

    }
}
