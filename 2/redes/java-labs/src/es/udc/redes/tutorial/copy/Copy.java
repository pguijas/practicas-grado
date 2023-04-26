package es.udc.redes.tutorial.copy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Copy {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Format: Copy <fichero origen> <fichero destino>");
            System.exit(-1);
        }
        BufferedInputStream buffin = null;
        BufferedOutputStream buffout = null;
        try {
            int a = 0;
            //Abrimos los Streams de salida y entrada y ponemos el buffer
            buffin = new BufferedInputStream(new FileInputStream(args[0]));
            buffout = new BufferedOutputStream(new FileOutputStream(args[1]));
            while ((a = buffin.read())!=-1) {
                buffout.write(a);
            }
            buffout.flush();
        } catch (FileNotFoundException ex) {
            System.out.println("Error, archivo no encontrado");
        } catch (IOException ex) {
            System.out.println("Error, archivo no encontrado");
        } finally {
            try {
                buffin.close();
                buffout.close();
            } catch (IOException ex) {
                System.out.println("Error al cerrar el archivo");
            }
        }
    }
    
}
