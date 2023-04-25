package es.udc.redes.webserver;

import java.io.*;
import java.net.*;
import java.util.Properties;

/**
 * WebServer implements a webserver HTTP/1.0. It creates logs for access and errors.<p>
 * Default Values:<br>
 * <b>port</b>(of the server): 5000<br>
 * <b>directory_index</b>(name of index file): index.html<br>
 * <b>directory</b>(direcory rut): .<br>
 * <b>allow</b>(when the client asks for a directory and it's true: if directory_index doesn't exist the server 
 * will list the folder, else the server return an error): true<br>
 * <br>
 * To change to your own values, you can create a property file and load the server with "<b>java -jar webserver.jar property_file</b>".
 * @author pguijas
 */
public class WebServer {

    private int port;               
    private String directory_index; 
    private String directory;       
    private boolean allow;          
    private FileOutputStream accesoslog = null, erroreslog = null; 

    /**
     * Getter of the server port
     * @return port port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter of the index file
     * @return directory_index fichero a buscar
     */
    public String getDirectory_index() {
        return directory_index;
    }
    
    /**
     * Getter of the server web code directory
     * @return directory directory
     */
    public String getDirectory() {
        return directory;
    }
    
    /**
     * Getter of the allow flag (will determine if a folder will be listed or it will return a 403 error)
     * @return allow
     */
    public boolean isAllow() {
        return allow;
    }
    
    /**
     * Getter of the FileOutputStream of the log access file
     * @return accesoslog
     */
    public FileOutputStream getAccesosLog() {
        return accesoslog;
    }
    
    /**
     * Getter of the FileOutputStream of the log error file
     * @return erroreslog
     */
    public FileOutputStream getErroresLog() {
        return erroreslog;
    }


    /**
     * It detenmines the values of the WebServer
     * @param port port
     * @param directory_index index directory
     * @param directory directory
     * @param allow allow flag
     */
    private void setConfig(int port, String directory_index, String directory, boolean allow) {
        this.port = port;        
        this.directory_index = directory_index;
        this.directory = directory;        
        this.allow = allow;
    }
    
    /**
     * It determines the FileOutputStream of the log files
     * @param accesos acces log FileOutputStream
     * @param errores error log FileOutputStream
     */
    private void setLog(FileOutputStream accesos, FileOutputStream errores) {
        this.accesoslog = accesos;
        this.erroreslog = errores;
    }
    /**
     * WebServer constructor
     * @param file File of configuration of the server (null == doesn't have)
     */
    public WebServer(String file) {
        setConfig(5000, "index.html", ".", true);
        if (file != null)
            loadConfig(file);
    }
    
    /**
     * Load the values of the configuration of the server
     * @param file Name of the configuration file.
     */
    private void loadConfig(String file) {
        FileInputStream config = null;
        Properties propiedades = null;
        boolean allow;
        try {
            config = new FileInputStream(file);
            propiedades = new Properties();
            propiedades.load(config);
            allow=propiedades.getProperty("allow", "true").equals("true");
            setConfig(
                    Integer.parseInt(propiedades.getProperty("port", "5000")), 
                    propiedades.getProperty("directory_index", "index.html"), 
                    propiedades.getProperty("directory", "."), 
                    allow
            );
       
        } catch (Exception e) {
            System.err.println("No ha sido posible abrir o leer el archivo de configuracion. Cargados valores por defecto");
            //(se quedan los valores por defecto)

        } finally {
            try {
                if (config != null) {
                    config.close();
                }

            } catch (Exception e) {
                System.err.println("Error al cerrar el archivo de cofiguración: " + e.getMessage());
            }
        }
    }

    /**
     * Start the server, it's listening TCP connections and it creates a thread for each one
     */
    public void iniciar() {
        ServerSocket server = null;
        Socket socket = null;
        Conexion_Thread thread = null;
        
        try {
            // Create a server socket
            server = new ServerSocket(getPort());
            // Creamos los archivos del log
            setLog(new FileOutputStream(getDirectory() + "/accesos.log"), new FileOutputStream(getDirectory() + "/errores.log"));
            System.out.println("Escuchando en el puerto: " + getPort());
            System.out.println("Index: " + getDirectory_index());
            System.out.println("Directorio: " + getDirectory());
            System.out.println("Allow: " + isAllow());
            
            while (true) {
                // Wait for connections
                socket = server.accept();
                // Create a ServerThread object, with the new connection as parameter 
                thread = new Conexion_Thread(socket, this);
                // Initiate thread using the start() method
                thread.start();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //Close the socket
            if (server != null) {
                try {
                    server.close();
                    getAccesosLog().close();
                    getErroresLog().close();
                } catch (Exception e) {
                    System.err.println("Error al cerrar el servidor");
                }
            }
        }
    }
    
    /**
     * main of WebServer, it creates and starts the WebServer
     * @param argv configuration file or nothing
     */
    public static void main(String argv[]) {
        WebServer servidor;
        switch(argv.length){
            case 0:
                servidor = new WebServer(null);        
                servidor.iniciar();
                break;
            case 1:                
                servidor = new WebServer(argv[0]);        
                servidor.iniciar();
                break;
            default:
                System.out.println("Formato: [fichero de coficuracion] o nada para iniciarlo con los valores por defecto");
        }
    }

}
