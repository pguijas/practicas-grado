package es.udc.fic.ri.mri_indexer.e3;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class WriteIndex {
	public static void main(String[] args) {
		String usage = "java es.udc.fic.ri.mri_indexer.e3.WriteIndex -index INDEX_PATH -outputfile OUTPUT_FILE_PATH\n\n"
                + "Este programa volcará los campos(fields) del índice en un archivo de texto plano";
		String indexPath = null; 
		String outputfilePath = null;
		Directory dir = null;
		DirectoryReader indexReader = null;
		
		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-outputfile".equals(args[i])) {
				outputfilePath = args[i + 1];
				i++;
			}
		}
		
		if (indexPath==null || outputfilePath==null) {
			System.out.println("Usage: " + usage);
			System.exit(1);
		}
		
		
		//Abrimos
		try {
			dir = FSDirectory.open(Paths.get(indexPath));
			indexReader = DirectoryReader.open(dir);
		} catch (CorruptIndexException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		}
		
		//Esto lo que hace en el fondo es cargar todos los fieldnames de cada segmento
		final FieldInfos fieldinfos = FieldInfos.getMergedFieldInfos(indexReader);
		try {
			FileWriter myWriter = new FileWriter(outputfilePath);
			for (FieldInfo fieldInfo : fieldinfos) {
				myWriter.write(fieldInfo.name+" ");
			}
		    myWriter.close();
		    indexReader.close();
		    dir.close();
		    System.out.println("Done!");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	   
	}
}
