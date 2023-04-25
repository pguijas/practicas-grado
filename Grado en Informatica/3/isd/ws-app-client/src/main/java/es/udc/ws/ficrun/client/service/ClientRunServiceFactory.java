package es.udc.ws.ficrun.client.service;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

import java.lang.reflect.InvocationTargetException;

public class ClientRunServiceFactory {

    private final static String CLASS_NAME_PARAMETER = "ClientRunServiceFactory.className";
    private static Class<ClientRunService> serviceClass = null;

    private ClientRunServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<ClientRunService> getServiceClass() {
        if (serviceClass == null) {
            try {
                String serviceClassName = ConfigurationParametersManager
                        .getParameter(CLASS_NAME_PARAMETER);
                serviceClass = (Class<ClientRunService>) Class.forName(serviceClassName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return serviceClass;
    }

    public static ClientRunService getService() {
        try {
            return (ClientRunService) getServiceClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
