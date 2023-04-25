package es.udc.fic.ri.mri_indexer.e3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexFiles {

	private IndexFiles() {
	}

	//Para el threadpool (Indexa un doc)
	public static class indexDoc implements Runnable {

		private final IndexWriter writer;
		private final Path file;
		private final float sizeKb;
		private final FileTime lastModified;
		private final FileTime creationTime;
		private final FileTime lastAccessTime;
		private final int onlyTopLines;
		private final int onlyBottomLines;
		private final boolean update;
		private final String hostname;
		
		
		public indexDoc(IndexWriter writer, Path file, BasicFileAttributes attrs, String hostname, int onlyTopLines, int onlyBottomLines, boolean update) {
			this.writer = writer;
			this.file = file;
			this.lastModified = attrs.lastModifiedTime();
			this.sizeKb= (float)attrs.size()/1024;
			this.creationTime = attrs.creationTime();
			this.lastAccessTime = attrs.lastAccessTime();
			this.onlyTopLines = onlyTopLines;
			this.onlyBottomLines = onlyBottomLines;
			this.update = update;
			this.hostname = hostname;
		}
		
		/* Indexed, tokenized, Not stored. */
		public static final FieldType TYPE_CONTENT = new FieldType();

		static {
			TYPE_CONTENT.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			TYPE_CONTENT.setTokenized(true);
			TYPE_CONTENT.setStored(false);
			TYPE_CONTENT.setStoreTermVectors(true);
			TYPE_CONTENT.setStoreTermVectorPositions(true);
			TYPE_CONTENT.freeze();
		}
		
		//Indexa un único documento
		void indexFile() throws IOException {
			try (InputStream stream = Files.newInputStream(this.file)) {
				// make a new, empty document
				Document doc = new Document();

				//Path: no toquenizado
				Field pathField = new StringField("path", this.file.toString(), Field.Store.YES);
				doc.add(pathField);

				//Modified: indexado
				doc.add(new LongPoint("modified", this.lastModified.toMillis()));
				
				//Thread
				doc.add(new StringField("thread",Thread.currentThread().getName().toString(),Field.Store.YES));
				
				//HostName
				doc.add(new StringField("hostname", this.hostname,Field.Store.YES));
				
				//sizeKb
				doc.add(new FloatPoint("sizeKb", this.sizeKb));
				
				//creationTime
				doc.add(new StringField("creationTime", this.creationTime.toString(), Field.Store.YES));
				
				//lastAccessTime
				doc.add(new StringField("lastAccessTime", this.lastAccessTime.toString(), Field.Store.YES));
				
				//lastModifiedTime
				doc.add(new StringField("lastModifiedTime", this.lastModified.toString(), Field.Store.YES));
				
				//Ahora, lo mismo pero en un formato que lucene acepta
				Date creationTimeDate = new Date(this.creationTime.toMillis());
				Date lastAccessTimeLucene = new Date(this.lastAccessTime.toMillis());
				Date lastModifiedTimeLucene = new Date(this.lastModified.toMillis());
				
				//creationTimeLucene
				doc.add(new StringField("creationTimeLucene", DateTools.dateToString(creationTimeDate, DateTools.Resolution.MINUTE), Field.Store.YES));
				
				//lastAccessTimeLucene
				doc.add(new StringField("lastAccessTimeLucene", DateTools.dateToString(lastAccessTimeLucene, DateTools.Resolution.MINUTE), Field.Store.YES));
				
				//lastModifiedTimeLucene
				doc.add(new StringField("lastModifiedTimeLucene", DateTools.dateToString(lastModifiedTimeLucene, DateTools.Resolution.MINUTE), Field.Store.YES));
				
				//Contents: tokenized and indexed, but not stored
				BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				
				
				if (onlyTopLines<0 && onlyBottomLines<0) {
					doc.add(new Field("contents",buffer,TYPE_CONTENT));
					
				} else {
					//Cargamos las lineas en una lista
					ArrayList<String> lines = new ArrayList<String>();
					String line = buffer.readLine();
					String final_lines="";
					while (line != null) {
						lines.add(line);
						line = buffer.readLine();
					}
					//Para que no se solapen
					if ((onlyTopLines+onlyBottomLines)>lines.size()) {
						final_lines=String.join("\n",lines);
					} else { //Introducimos las lineas deseadas
						if (onlyTopLines>0) {
							final_lines = final_lines + String.join("\n",lines.subList(0, onlyTopLines)) + "\n"; //salto de linea necesario para que no se mezclen
						}
						if (onlyBottomLines>0) {
							final_lines = final_lines + String.join("\n",lines.subList(lines.size()-onlyBottomLines, lines.size()));	
						}
					}
					//Finalmente añadimos contenido
					doc.add(new Field("contents",final_lines, TYPE_CONTENT));
				}
		
				// O crea o modifica
				// Téngase en cuenta que si el indice no existe no acualizará nada.
				if (!update) {
					// New index, so we just add the document (no old document can be there):
					System.out.println("adding " + this.file);
					this.writer.addDocument(doc);
				} else {
					//Se requiere update pero se crea un nuevo índice, no se acutaliza nada
					if (this.writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("adding " + this.file);
						this.writer.addDocument(doc);
					} else {
						System.out.println("updating " + this.file);
						this.writer.updateDocument(new Term("path", this.file.toString()), doc);
					}
					
				}
			}
		}

		//Lo que el thread va a ejecutar
		@Override
		public void run() {
			try {
				this.indexFile();
			} catch (Exception e) {
				System.err.println(e.toString());
				System.out.println("Error al indexar el archivo "+this.file);
			}
			
		}
	}
	


	/** Index all text files under a directory. */
	public static void main(String[] args) {
		String usage = "java org.apache.lucene.demo.IndexFiles [-index INDEX_PATH] [-update] "
				+ "[-openmode append|create|create_or_append] [-numThreads NUM_THREADS]\n\n"
				+ "Otros argumentos como docs, partialIndexes, onlyFiles, onlyTopLines y onlyBottomLines serán especificados en config.properties"
				+ " alojado en src/main/resources (Siendo doc el único obligatorio)\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index in INDEX_PATH that can be searched with SearchFiles\n"
		        + "Destacar por último que si se quiere acutalizar un índice el openmode no puede ser create. Y que si no se indica un donde alojar el "
		        + "índice se creará una carpeta index en la raiz del proyecto";
		String indexPath = "index";
		OpenMode openmode = OpenMode.CREATE; 							//por defecto
	    boolean update = false;											//por defecto
		int numThreads = Runtime.getRuntime().availableProcessors();	//por defecto
		int onlyTopLines = -1;											//por defecto
		int onlyBottomLines = -1;										//por defecto
		
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
			System.err.println("Necesarias ruta/s de documentos para la ejecución");
			System.err.println("");
			System.err.println("Usage: " + usage);
			System.exit(1);
		} 

		//Docs
		ArrayList<Path> docsDir = new ArrayList<Path>();
		for (String path : prop.getProperty("docs").split(" ")) {
			Path docDir = Paths.get(path);
			//comprobamos que sea una ruta válida
			if (!Files.isReadable(docDir)) {
				System.out.println("Document directory '" + docDir.toAbsolutePath()
						+ "' does not exist or is not readable, please check the path");
				System.exit(1);
			}
			docsDir.add(docDir);	//la añadimos a la lista
		}
		
		//Partial Indexes
		ArrayList<Path> partialIndexesdirs = null;
		if (prop.getProperty("partialIndexes") != null) {
			//Inicializamos
			partialIndexesdirs = new ArrayList<Path>();
			//Rellenamos
			for (String path : prop.getProperty("partialIndexes").split(" ")) {
				Path indexDir = Paths.get(path);
				//comprobamos que sea una ruta válida
				if (!Files.isDirectory(indexDir)) {
					System.out.println("Partial Index '" + indexDir.toAbsolutePath()
							+ "' is not a Directory, please check the path");
					System.exit(1);
				}
				partialIndexesdirs.add(indexDir);	//la añadimos a la lista
			}
		} 
		
		//Si se quieren usar indices parciales y el nº de indices no coincide con el nº de rutas -> fallo
		if (partialIndexesdirs!=null) {
			if (docsDir.size()!=partialIndexesdirs.size()) {
				System.out.println("If you want to create partial indexes, the number of Docs must be the same that te number of PartialIndexes");
				System.exit(1);
			}
		}
	
		//Creating the list of allowed extensions
		ArrayList<String> onlyFiles = null;	
		if (prop.getProperty("onlyFiles")!=null) {
			onlyFiles = new ArrayList<String>(Arrays.asList(prop.getProperty("onlyFiles").split(" ")));
		}

		//OnlyLines
		if (prop.getProperty("onlyTopLines")!=null) {
			onlyTopLines = Integer.parseInt(prop.getProperty("onlyTopLines"));
			if (onlyTopLines<0) {
				System.out.println("onlyTopLines can't be negative");
				System.exit(1);
			}
		}
		if (prop.getProperty("onlyBottomLines")!=null) {
			onlyBottomLines = Integer.parseInt(prop.getProperty("onlyBottomLines"));
			if (onlyBottomLines<0) {
				System.out.println("onlyBottomLines can't be negative");
				System.exit(1);
			}
		}

		// Pilla args
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-update".equals(args[i])) {
				update=true;
				
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
			} else if ("-numThreads".equals(args[i])) {
				numThreads = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		//Creamos el threadpool
		final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		
		// Abre dir, crea analyzer e index
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			//Inficamos el modo de apertura
			iwc.setOpenMode(openmode);
			
			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			// Creamos índices con la config
			IndexWriter writer = new IndexWriter(dir, iwc);
			ArrayList<IndexWriter> partialWriters = null;
			
			if (partialIndexesdirs!=null) {
				partialWriters = new ArrayList<IndexWriter>();
				for (Path path : partialIndexesdirs) {
					Directory partialIndex_dir = FSDirectory.open(path);
					IndexWriterConfig iwc_p = new IndexWriterConfig(analyzer);
					iwc_p.setOpenMode(openmode);
					partialWriters.add( new IndexWriter(partialIndex_dir, iwc_p));
				}
			}
			
			//Indexamos docs
			indexDocs(writer, docsDir, partialWriters, onlyFiles, executor, onlyTopLines, onlyBottomLines, update);
			
			//Cerramos el pull (no admitimos +)
			executor.shutdown();

			//Esperamos como max hasta 1h
			try {
				executor.awaitTermination(1, TimeUnit.HOURS);
			} catch (final InterruptedException e) {
				e.printStackTrace();
				System.exit(-2);
			}

			//Merge
			if (partialWriters!=null) {
				//Cerramos los Writers antes de abrirlos
				for (IndexWriter p_writer : partialWriters) {
					p_writer.close();
				}
				//Los abrimos
				Directory readers[] = new Directory[partialIndexesdirs.size()];
				for (int i = 0; i < readers.length; i++) {
					readers[i]=FSDirectory.open(partialIndexesdirs.get(i));
				}
				//Los añadimos al indice principal
				writer.addIndexes(readers);
			}
			
			
			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			// Nos vamos
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	// Pilla archivo o archivos y delega en indexDoc
	static void indexDocs(final IndexWriter writer_principal, ArrayList<Path> paths, ArrayList<IndexWriter> partialWriters,
			ArrayList<String> onlyFiles, ExecutorService executor, int onlyTopLines, int onlyBottomLines, boolean update) throws IOException {
		
		Path path;
		String hostname = InetAddress.getLocalHost().getHostName();	 //para no hacer peticiones de resolucion de dns inversa innecesarias
		
		//Recorremos los diversos paths de docs
		for (int i = 0; i < paths.size(); i++) {
			path=paths.get(i);
			final IndexWriter writer = (partialWriters==null)?writer_principal:partialWriters.get(i);
			// Dir or File
			if (Files.isDirectory(path)) {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {	
						//en caso de no tener extension se selecciona toda la cadena(y obviamente el if dará falso)
						String extension = "." + file.toString().split("\\.")[file.toString().split("\\.").length-1];
						//Solo indexamos si tiene la extension deseada
						if (onlyFiles==null) {
							final Runnable worker = new indexDoc(writer, file, attrs, hostname, onlyTopLines, onlyBottomLines, update);;
							executor.execute(worker);
						} else if (onlyFiles.contains(extension)) {
							//Añadimos Archivo al pool
							final Runnable worker = new indexDoc(writer, file, attrs, hostname, onlyTopLines, onlyBottomLines, update);;
							executor.execute(worker);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} else {
				//Añadimos Archivo al pool (si tiene la extension adecuada)
				//revisar este if, que no comprobe que funcionase bien
				String extension = "." + path.toString().split("\\.")[path.toString().split("\\.").length-1];
				if (onlyFiles.contains(extension)) {
					final Runnable worker = new indexDoc(writer, path, Files.readAttributes(path, BasicFileAttributes.class), hostname, onlyTopLines, onlyBottomLines, update);
					executor.execute(worker);
				}
			}
		}
	}
}











