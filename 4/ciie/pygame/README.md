# FICGames

## Descripción

The Code es un juego de estilo *Top Down Shooter* en donde el protagonista tendrá que derribar a todos sus rivales para poder recuperar el código. Es un juego de temática militar, donde prima una violencia frenética.
 
El videojuego está estructurado por mapas o niveles, se tendrá que acabar con todos los soldados para avanzar a los siguientes. La dinámica principal del juego es *spawnear* y morir hasta que se logre acabar con todos los soldados, con transiciones muy rápidas para no ralentizar la experiencia. 
 
Se implementaron 5 niveles, el primero (llamado '*Nivel 0*') de introducción para que el usuario entienda la historia y aprenda los controles por medio de diálogos. En los cuatro siguientes niveles habrá enemigos y el número de armas crecerá a medida que se avanza, a excepción del último nivel, que habrá que afrontarlo con las armas conseguidas. Se empieza con una pistola, después se añade al inventario un fusil de asalto y por último una ametralladora ligera. Destacar también la existencia de items de munición y salud por todo el mapa. La combinación de estos elementos nos proporciona un aumento de dificultad escalonada entre niveles a pesar de disponer de más armas.

La interfaz gráfica del juego se diseñó con un afán 'retro' y minimalista, intuitivo para el usuario y manteniendo la estética del juego. Todo el apartado gráfico sigue el mismo estilo, la misma fuente y colores, gracias a esto se obtiene una GUI consistente en cuanto a diseño y visualmente más agradable.

## Manual de usuario
  
El movimiento del jugador se basa en el típico movimiento con las teclas '*W-A-S-D*'. 
* Para que el personaje avance hacia arriba se usa la tecla *W*.
* Para que el personaje avance hacia abajo se usa la tecla *S*.
* Para que el personaje avance hacia la derecha se usa la tecla *D*.
* Para que el personaje avance hacia la izquierda se usa la tecla *A*.

La rotación del personaje (hacia donde apunta con el arma) se hace con el ratón, con respecto a este. Por lo tanto el personaje siempre estará mirando hacia donde tengamos el ratón en la pantalla. Los disparos, como en cualquier *shooter* se realizan pulsando el botón izquierdo del ratón.

Un aspecto a destacar sobre la jugabilidad es que el movimiento del personaje es independiente al de la rotación, lo que a nuestro parecer es la manera más intuitiva  y sobre todo dinámica de jugar.

Primero se presentan todos los niveles bloqueados, excepto el cero, en el cual hay un pequeño tutorial para moverse y se presenta la historia. Una vez acaban los diálogos se puede avanzar al primer nivel, en donde nos explican cómo disparar. El número de armas disponibles irá aumentando hasta el nivel tres, en donde tendremos una pistola, un fusil de asalto y una ametralladora. En cada nivel tendremos unas cajas que nos darán vida o munición dependiendo del tipo de caja. Para superar cada nivel es necesario eliminar a todos los enemigos del mapa.

## Instalación

A continuación se muestra el entorno de desarrollo en el que se ha realizado el juego, las herramientas utilizadas y las librerías que son necesarias:
* El juego ha sido desarrollado con Python3, y pip3:
  ```bash
  $ python3 --version
    Python 3.8.10
  $ pip3 --version                      
    pip 20.0.2 from /usr/lib/python3/dist-packages/pip (python 3.8)
  ```
* Instalación de librerías:
  ```bash
  $ pip3 install pygame pytmx matplotlib
  ```
* Ejecución (incluido un .exe para plataformas Windows):
  ```bash
  $ pyhon3 main.py
  ```
