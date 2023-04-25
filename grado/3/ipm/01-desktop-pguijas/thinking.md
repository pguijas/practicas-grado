
# Elementos de Nuestra Interfaz
Cabe destacar que los elementos a los que nos referiremos, hacen a referencia a elementos del [wireframe](https://github.com/ipm-fic/01-desktop-pguijas/blob/master/wireframe.pdf), siendo estes nombrados de izquierda a derecha y de la pantalla de bienvenida a la principal. De esta forma cuando un tipo de elemento se repite, su nombre seguirá esta estructura: TipoNum (Ej: Intput1, Intput2...).

## Pantalla de  Bienvenida

  * Text1: Seleccione el intervalo.
  * Input1: Seleccionar distancia del intervalo.
  *	Input2: Seleccionar dirección del intervalo.
  * Button1: Buscar.

## Pantalla de Principal

  * Button2: Retroceder.
  * Input3: Seleccionar distancia del intervalo.
  * Input4: Seleccionar dirección del intervalo.
  * Button3: Buscar.
  * Text2: Ejemplo de intervalo.
  * Input5: Seleccionar nota de comienzo del ejemplo de intervalo.
  * List: Lista de canciones representativas del intervalo. Dicha lista muestra un corazón si la canción es la favorita.

# Decisiones

  * Solo se mostrará información de un único intervalo a la vez para que sea mas sencillo y fácil de utilizar.
  * Pantalla de bienvenida con sólo tres elementos para facilitar la usabilidad e incrementar la sencillez.
  * Pantalla de principal en la que mostrar resultados de la búsqueda y poder realizar nuevas búsquedas desde la misma.
  * Input5 para proporcionar una parte interactiva, en la que el usuario pueda saber que nota estaría a la distancia del intervalo buscado. El usuario introduciría una nota de partida y se le mostraría la nota a la distancia correspondiente.
  * List para mostrar la lista de canciones, mostraremos todas con un scrollbar. Suponemos que son suficientemente pocas como para cargarlas todas sin ningún tipo de retardo. (si fuesen muchas no podríamos cargar todas por los retardos que podrían ocasionar y tendríamos que ir cargando de pocas en pocas a medida que se baja el scroll por ejemplo).
  * Button2 para dar la posibilidad de retroceder a la pantalla de inicio de tal manera que se evite transmitir la sensación de permanencia constante en la pantalla de resultados.

# Explicación extensa
  
  Al iniciar la aplicación se mostrará una primera pantalla de bienvenida. En dicha pantalla se mostrará un texto pidiéndole a la usuaria que seleccione el intervalo que desee. Para que pueda seleccionar el intervalo, existirán dos inputs; uno para seleccionar la distancia del intervalo (segunda menor, tercera mayor …)  y otro para especificar si es ascendente o descendente. A continuación también aparecerá un botón para que pueda realizar la búsqueda. Esta primera pantalla contendrá sólo estos tres elementos acompañados de un texto aclarativo ya que queremos que se trate de una aplicación sencilla, fácil de usar y que desde el primer momento se intuya su funcionamiento.

  Una vez se realice la búsqueda, aparecerá una segunda pantalla (la pricipal), la cual nos mostrará el resultado de la búsqueda. Primero se mostrarán dos notas, las cuales serán un ejemplo del intervalo que se acaba de buscar. Por defecto la primera nota será un “do”, la primera nota de la escala, ya que consideramos que será una buena referencia para la usuaria. En caso de que quiera que dicho ejemplo de intervalo empiece en otra nota, aparecerá un input donde se pueda elegir la nota de partida. Todo esto irá acompañado de un texto breve y conciso de modo que aclarará a la usuaria su uso.
  A continuación, mostraremos todas las canciones representativas del intervalo pudiendo pinchar en ellas para visualizarlas en el navegador y mostrando un corazón si es la canción favorita del intervalo. 
  En caso de que se desee buscar otro intervalo, se podrá hacer desde esta misma pantalla (en la parte superior). Esto es debido a que la ventana de bienvenida es únicamente de mostrarnos algo simple y sencillo al ejecutar la aplicación. Junto a esto también se pondrá un botón para que se pueda retroceder, por si se quiere acceder de nuevo a la pantalla de bienvenida y realizar otras búsquedas desde ahí (sacando de nuestra ventana los elementos que mostraban información de las búsquedas). 
