package es.udc.fic.ri.mri_searcher;


import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class IndexNPL {

	public static void main(String[] args) {

		//--------------------------
		//	Obtención de Parámetros
		//--------------------------
		
		if (args.length!=2 && args.length!=4) {
			System.out.println("Usage: java es.udc.fic.ri.mri_searcher.IndexNPL -openmode append|create|create_or_append -index pathname \n TFXIDF SIMILARIY POR DEFECTO");
			return;
		}
		
		String indexPath = null;
		String docs = null;
		Similarity similarity = new ClassicSimilarity();  //por defecto TFxIDF
		OpenMode openmode = OpenMode.CREATE;			  //por derecto Create

		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-openmode".equals(args[i])) {
				if ("append".equals(args[i + 1])){
					openmode = OpenMode.APPEND;
				} else if ("create".equals(args[i + 1])){
					openmode = OpenMode.CREATE;
				} else if ("create_or_append".equals(args[i + 1])){
					openmode = OpenMode.CREATE_OR_APPEND;
				} else {
					System.err.print("Open Mode no válido.");
					System.exit(1);
				}
				i++;
			} 
		}
		
		
		// Properties
		Properties prop = new Properties();
		//Opening file...
		try {
			FileReader reader=new FileReader("src/main/resources/config.properties");
			prop.load(reader);
		} catch (IOException e1) {
			System.err.println("Error al abrir el fichero properties.");
			e1.printStackTrace();
		} 
		
		// Si no tiene docspath, exit
		if (prop.getProperty("docs") == null) {
			//Dejar los errores mas bonitos
			System.err.println("Necesarias ruta de documentos para la ejecución");
			System.err.println("");
			System.exit(1);
		} 
		docs=prop.getProperty("docs");
		
		// Pillamos el indexingmodel
		String indexingmodel = prop.getProperty("indexingmodel");
		if (indexingmodel != null) {
			if (indexingmodel.contains("jm")){		//LMJelinekMercerSimilarity
				similarity = new LMJelinekMercerSimilarity(Float.parseFloat(indexingmodel.split(" ")[1]));
			} else if (indexingmodel.contains("dir")){ //LMDirichletSimilarity
				similarity = new LMDirichletSimilarity(Float.parseFloat(indexingmodel.split(" ")[1]));
			} else if ("tfidf".equals(indexingmodel)){	//TFIDFSimilarity
				similarity = new ClassicSimilarity();
			} else {
				System.err.print("indexingmodel no válido.");
				System.exit(1);
			}
		} 
		
		//--------------------------
		//	Obtención de Documentos
		//--------------------------
		
		List<String> nplDocs = new LoadNPLinfo(docs + "/doc-text").get();
			
		//-------------
		//	Indexación
		//-------------
		
		System.out.println("Indexing at " + indexPath + " ...");

		//Inicializamos y cargamos IndexWriter
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setSimilarity(similarity);
		config.setOpenMode(openmode);
		IndexWriter writer = null;

		//revisar excepciones
		try {
			writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), config);
		} catch (CorruptIndexException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		} catch (LockObtainFailedException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Graceful message: exception " + e1);
			e1.printStackTrace();
		}

		for (int i = 0; i < nplDocs.size(); i++) {
			Document doc = new Document();
			
			doc.add(new StringField("DocIDNPL", Integer.toString(i+1), Field.Store.YES));
			doc.add(new TextField("Contents", nplDocs.get(i).replace("\n", ""), Field.Store.YES));

			//Añadimos doc al indice
			try {
				writer.addDocument(doc);
			} catch (CorruptIndexException e) {
				System.out.println("Graceful message: exception " + e);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Graceful message: exception " + e);
				e.printStackTrace();
			}
		}
		
		System.out.println("Done! :)");

		try {
			writer.commit();
			writer.close();
		} catch (CorruptIndexException e) {
			System.out.println("Graceful message: exception " + e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Graceful message: exception " + e);
			e.printStackTrace();
		}

	}
}