package es.udc.redes.webserver;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Response represents a HTTP answer that could be sent.
 *
 * @author pguijas
 */
public class Response {

    private WebServer servidor;
    private final OutputStream out;
    private Date cachedate;
    private boolean cuerpo;
    private String frase, tipo, recurso, dinamico;
    private Date lastmod;
    private long tamano;
    private int code;

    /**
     * Setter of the requested resource
     *
     * @param recurso resource
     */
    private void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    /**
     * Set the state of the response
     *
     * @param code response code
     * @param frase response frase
     */
    private void setEstado(int code, String frase) {
        this.code = code;
        this.frase = frase;
        if (code >= 400) {
            this.tipo = "text/html";
            this.dinamico = "<h1 style=\"text-align: center; margin: auto; margin-top: 250px;\">" + "Error " + code + "</h1>";
            this.dinamico = this.dinamico + "<h4 style=\"text-align: center; margin: auto;\">" + frase + "</h4>";
            this.tamano = this.dinamico.getBytes().length;
        }
    }

    /**
     * Getter of WebServer where we can find the values of the server
     *
     * @return servidor
     */
    private WebServer getServidor() {
        return servidor;
    }

    /**
     * Getter of the response status code
     *
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the requested resource
     *
     * @return resource
     */
    public String getRecurso() {
        return recurso;
    }

    /**
     * Returns if the body is sent
     *
     * @return body flag
     */
    public boolean isCuerpo() {
        return cuerpo;
    }

    /**
     * Retuns the size of the content that we send
     *
     * @return size
     */
    public long getTamano() {
        return tamano;
    }

    /**
     * Return the phrase of the status
     *
     * @return phrase
     */
    public String getFrase() {
        return frase;
    }

    /**
     * Constructor of a normal response
     *
     * @param servidor WebServer
     * @param cuerpo flag of body send
     * @param archivo requested resource
     * @param cachedate cache date
     * @param out where the response is sent
     */
    public Response(WebServer servidor, boolean cuerpo, String archivo, Date cachedate, OutputStream out) {
        this.servidor = servidor;
        this.cuerpo = cuerpo;
        this.out = out;
        setEstado(200, "OK");
        this.recurso = archivo;
        this.cachedate = cachedate;
        this.dinamico = null;
        load_file();
    }

    /**
     * Constructor of a Bad Request response
     *
     * @param out where the response is sent
     */
    public Response(OutputStream out) {
        this.out = out;
        setEstado(400, "Bad Request");
        System.err.println("Bad Request");
    }

    /**
     * Load and process the file.<b>
     * Cases: <b>
     * -File found<b>
     * -File not found <b>
     * -File is a directory
     */
    private void load_file() {
        File open_rec = new File(getServidor().getDirectory() + getRecurso());
        String extension = getRecurso().split("\\.")[getRecurso().split("\\.").length - 1];
        Boolean incache = false;
        //Si existe
        if (open_rec.exists()) {
            //Si es un directorio
            if (open_rec.isDirectory()) {
                esdirectorio();
            } else {
                //Si es un archivo   
                this.lastmod = new Date(open_rec.lastModified());
                if (cachedate != null) {
                    incache = cachedate.compareTo(lastmod) >= 0;
                }
                //Si el clinte no tiene los datos se los cargamos
                if (!incache) {
                    switch (extension) {
                        case "html":
                            this.tipo = "text/html";
                            break;

                        case "txt":
                            this.tipo = "text/plain";
                            break;

                        case "gif":
                            this.tipo = "image/gif";
                            break;

                        case "png":
                            this.tipo = "image/png";
                            break;

                        default:
                            this.tipo = "application/octet-stream";
                    }
                    this.tamano = open_rec.length();
                } else {
                    setEstado(304, "Not Modified");
                    this.cuerpo=false;
                }
            }
        } else {
            //Si no existe el recurso solicitado miramos si es una página dinámica
            if (extension.split("\\?")[0].equals("do")) {
                if (extension.split("\\?").length == 2) {
                    this.tipo = "text/html";
                    Map<String, String> parametros = new HashMap<String, String>();
                    //Leemos los parametros
                    for (String s : extension.split("\\?")[1].split("\\&")) {
                        if (s.split("\\=").length == 2) {
                            parametros.put(s.split("\\=")[0], s.split("\\=")[1]);
                        }
                    }
                    //Cargamos la respuesta dinamica
                    try {
                        this.dinamico = ServerUtils.processDynRequest(
                                "es.udc.redes.webserver."
                                + getRecurso().substring(1, getRecurso().length() - extension.length() - 1),
                                parametros);
                        this.tamano = dinamico.getBytes().length;
                    } catch (Exception e) {
                        System.err.println("Error al cargar la clase dinamica: " + e.getMessage());
                        setEstado(404, "Not Found");
                    }
                } else {
                    setEstado(404, "Not Found");
                }
            } else {
                setEstado(404, "Not Found");
            }
        }
    }

    /**
     * If is directory
     */
    private void esdirectorio() {
        File index = new File(getServidor().getDirectory() + getRecurso() + "/" + getServidor().getDirectory_index());
        File directory;
        String url;
        //Si existe el index
        if (index.exists() && !index.isDirectory()) {
            setRecurso(getRecurso() + "/" + getServidor().getDirectory_index());
            load_file();
        } else {
            //Si no existe y allow -> listamos (lo tratamos como si fuese dinamico)
            if (getServidor().isAllow()) {
                this.tipo = "text/html";
                directory = new File(getServidor().getDirectory() + getRecurso());
                if (getRecurso().endsWith("/")) {
                    url = getRecurso();
                } else {
                    url = getRecurso() + "/";
                }
                this.dinamico = "";
                for (String a : directory.list()) {
                    this.dinamico = this.dinamico + "<a href=\"" + url + a + "\">" + a + "<a></br>";
                }
                this.tamano = dinamico.getBytes().length;
            } else {
                setEstado(403, "Access Forbidden");
            }
        }
    }

    /**
     * Send response to client
     */
    public void Send() {
        PrintWriter out_pw = new PrintWriter(out, true);
        out_pw.println("HTTP/1.0 " + code + " " + frase);
        out_pw.println("Date: " + new Date());
        out_pw.println("Server: Misco/0.9 (Unix)");
        out_pw.println("Content-Type: " + tipo);
        out_pw.println("Content-Length: " + tamano);
        //Dinámico(errores incluidos) o estático(cargamos contenido)
        if (dinamico != null) {
            out_pw.println();
            out_pw.println(dinamico);
        } else {
            out_pw.println("Last-Modified:" + lastmod);
            //Salto de linea y despues cargamos contenido
            out_pw.println();
            if (cuerpo) {
                int a = 0;
                BufferedInputStream buffin = null;
                try {
                    buffin = new BufferedInputStream(new FileInputStream(getServidor().getDirectory() + recurso));
                    while ((a = buffin.read()) != -1) {
                        out.write(a);
                    }
                } catch (IOException ex) {
                    //runtime exception 500
                    System.err.println("Error al enviar archivo");
                } finally {
                    try {
                        out_pw.println();
                        buffin.close();
                    } catch (IOException ex) {
                        System.out.println("Error al cerrar el archivo");
                    }
                }
            }
        }
    }
}
