# Material Design en nuestro proyecto.
Como ya sabemos, Material Design establece unas pautas de diseño para nuestras aplicaciones. Para seguirlas, ya desde el principio hemos intentado usar los componentes material de Flutter.

## Cámara
Hemos decidido que nuestra aplicación se iniciase con la cámara y que mejor forma de darle importancia que dejar la pantalla libre y limpia a excepción de la unos pequeños controles en la parte inferior y una AppBar en la parte superior.
En cuanto a los colores hemos optado por una máxima transparencia. El color de nuestra aplicación sería el Teal dado que es el que utiliza imagga (De este color se pueden ver pinceladas en el título y en la sección de ayuda).
Buscamos una interfaz lo mas sencilla y agradable, de esta manera usamos elementos material como la AppBar, Iconos Material, AlertDialog... 
El único cambio que haremos será el de meter un degradado para que los iconos tanto de la AppBar como de la parte inferior sean siempre fácilmente visibles.


## Navegación
La navegación la realiza MaterialPageRoute por lo que las animaciones ya estarían implementadas.

## Resultados
En esta ventana hemos añadido el degradado de la misma forma que en la pantalla de cámara.
Los resultados los mostramos con un widget llamado [sliding up panel](https://pub.dev/packages/sliding_up_panel) el cual está basado en material. En cuanto al contenido de este panel, son básicamente ListTile y ExpansionTile los cuales ya implementan animaciones y pautas material.

## Referencias

[Object Detection live camera](https://material.io/design/machine-learning/object-detection-live-camera.html)