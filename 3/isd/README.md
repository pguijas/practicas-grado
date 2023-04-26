# Running the project example
---------------------------------------------------------------------

## Running the ws-app service with Maven/Jetty.

	cd ws-app/ws-app-service
	mvn jetty:es.udc.ficrun.model.run

## Running the client application

- Configure `ws-app/ws-app-client/src/main/resources/ConfigurationParameters.properties`
  for specifying the client project service implementation (Rest or Thrift) and 
  the port number of the web server in the endpoint address (9090 for Jetty, 8080
  for Tomcat)
