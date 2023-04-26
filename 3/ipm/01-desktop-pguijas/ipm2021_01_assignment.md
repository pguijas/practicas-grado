# ASSIGNMENT: PRÁCTICA 1 - INTERFACES GRÁFICAS DE USUARIA PARA APLICACIONES DE ESCRITORIO

## Presentación del _assignment_

A continuación se describe la misión (_assignment_).

### Objetivo

En esta misión (_assignment_) tendrás que desarrollar una aplicación
gráfica de escritorio. Para alcanzar tu objetivo tendrás que usar los
conocimientos adquiridos sobre el desarrollo de interfaces gráficas de
usuaria.

Para llevar a cabo tu cometido dispondrás del lenguaje de programación
`python`, la librería gráfica `Gtk+`, y el protocolo `At-spi`.


### Plazo

Se espera que hayas finalizado tu misión antes del día **27/10/2020 a
las 24:00**. Cualquier retraso en la finalización de tu cometido será
penalizado de acuerdo con el reglamento de la asignatura.


### Introducción al _assignment_

Nuestro enlace nos ha hecho llegar un informe sobre la existencia de
un servidor con datos útiles para el apoyo al estudio musical y el
_api_ que podemos usar para acceder a él. Tu misión es desarrolla una
aplicación que, a través de una interface gráfica, pertima atacar
dicho api y mostrar la información recuperada a la usuaria.

Como ya has deducido, el sistema sigue una arquitectura
cliente/servidor básica. Como ya hemos dicho, tu misión se centra en
la parte cliente.


### Recursos

El sistema operativo donde se ejecutará la aplicación es linux, en
cualquiera de sus distribuciones habituales.

Antes de comenzar debes procurarte las siguientes herramientas y
recursos:

* `python` versión >= 3.7
* `Gtk+` versión 3 y bindings para python
* `AT-SPI` y bindings para python
* `Git`
* Un repositorio en [github.com](github.com). Puedes ver los detalles
  en el apéndice correspondiente.

El resto de los recursos necesarios se irán presentando con cada
cometido (_task_), a medida que sean necesarios.


### Background

Como parte del informe, nuestro enlace nos ha incluido una pequeña
introducción a la teoría musical usada en el api del servidor.
La primera indicación es que se basa en la música tradicional
occidental, en la que una octava se divide en 12 semitonos iguales.

> Recuerda: Ya sabes lo que es una octava (doble/mitad frecuencia) de
> tus misiones con la teoría de la señal.

Esto da lugar a que, con este sistema, en una octava tenemos doce
notas musicales. Por desgracia, dependiendo del contexto, alguna notas
puede recibir dos nombres distintos, y los nombres no son iguales en
el sistema latino o en el sistema inglés:

  * Sistema latino: do, do♯/re♭, re, re♯/mi♭, mi, fa, fa♯/sol♭, sol,
    sol♯/la♭, la, la♯/si♭, si
  * Sistema inglés: C, C♯/D♭, D, D♯/E♭, E, F, F♯/G♭, G, G♯/A♭, A,
    A♯/B♭, B
  
> Atención: El caracter _sostenido_ "♯" no es el mismo que el numeral
> o hash "#". Tienes más información en el apéndice correspondiente.

> Nota: Si conoces la teoría musical sabrás que a esta explicación no
> es precisa/completa. Sin embargo, es suficiente para entender el
> dominio de aplicación y llevar a cabo la misión.

Tal y como están las escritas las dos listas anteriores:

  * Cada nota en el sistema latino se corresponde la nota en el
    sistema inglés en la misma posición de la lista.
  * Las notas que pueden recibir dos nombres están representadas como
    "G♯/A♭".
  * La _distancia_ entre dos notas consecutivas en la lista es **1
    semitono**
    
Como podras deducir por el nombre, **dos semitonos** equivalen a **un
tono**. Con esta información es posible calcular la _distancia_ entre
dos notas cualesquiera. Por ejemplo: do - fa, 2 tonos + 1 semitono.

> Atención: Las distancias entre notas las abreviaremos con el
> siguiente formato: 2T1ST (T=tono, ST=SemiTono)
    
Cada una de las posibles _distancias_ entre dos notas recibe un
nombre, y todos ellos reciben el nombre genérico de **intervalo**. Los
nombres de los intervalos, sus abreviaturas y distancias son los
siguientes:

  * Segunda menor (2m): 1ST
  * Segunda mayor (2M): 1T
  * Tercera menor (3m): 1T1ST
  * Tercera mayor (3M): 2T
  * Cuarta justa  (4j): 2T1ST
  * Cuarta aumentada (4aum): 3T
  * Quinta justa  (5j): 3T1ST
  * Sexta menor   (6m): 4T
  * Sexta mayor   (6M): 4T1ST
  * Séptima menor (7m): 5T
  * Séptima mayor (7M): 5T1ST
  * Octava        (8a): 6T
  
Así, por ejemplo, si partimos de la nota 'Do' tenemos:
  
  * Do - Fa: Intervalo de Cuarta justa
  * Do - Mi: Intervalo de Tercera mayor
  * Do - La#: Intervalo de Séptima menor
  
Por último, los intervalos tienen otro atributo, según la segunda nota
este a la derecha o a la izquierda en la lista de notas que vimos, el
intervalo será ascendente o descendente. Por ejemplo:

  * Do - Mi: Intervalo de Tercera mayor ascendente
  * Do - Sol♯: Intervalo de Tercera mayor descendente
  
> Nota: Puede resultate de ayuda pensar en la lista de notas como un
> lista circular.

### El veterano

Si es la primera vez que oyes hablar de teoría musical, todo este
background te puede haber resultado lioso, al fin y al cabo es un
_dominio de aplicación_ nuevo. Por suerte un compañero más veterano en
cuestiones de _elicitación_ y _análisis de requisitos_ nos ha dejado
unos apuntes muy valiosos:

  * En la práctica tenemos dos tipos de información. El primero es una
    lista de canciones (título, url y fav) organizada en una lista de
    categorías. El nombre de la categorías es el nombre de los
    intervalos. En resumen, una lista de listas.
    
  * Si pensamos en las notas musicales almacenadas en un _array_, o
    lista en python, en el mismo orden en que nos las presentaron en
    el background, vemos que la nota en la posición `i` esta separada
    por un semitono de la nota en la posición `i+1` y de la nota en la
    posición `i-1`. De esta forma es fácil traducir la distancia en
    tonos y semitonos a la diferencia en los índices en la lista.
    
    > Atención: No olvides tratar la lista como si fuese circular: `i
    > % 12`
  
### El API del servidor

El servidor ofrece un _api_ de tipo _restful_ con las respuestas en
formato _json_. El _api_ tiene dos _endpoints_:

  * `/intervals`
  
    Devuelve un objeto json dónde las claves son la abreviaturas de
    cada uno de los intervalos y el valor asociado la duración del
    mismo.
    
    Ejemplo:
    
    ```
    $ curl -v 127.0.0.1:5000/intervals
    *   Trying 127.0.0.1:5000...
    * Connected to 127.0.0.1 (127.0.0.1) port 5000 (#0)
    > GET /intervals HTTP/1.1
    > Host: 127.0.0.1:5000
    > User-Agent: curl/7.72.0
    > Accept: */*
    > 
    * Mark bundle as not supporting multiuse
    * HTTP 1.0, assume close after body
    < HTTP/1.0 200 OK
    < Content-Type: application/json
    < Content-Length: 149
    < Server: Werkzeug/1.0.1 Python/3.8.5
    < Date: Wed, 23 Sep 2020 14:26:48 GMT
    < 
    {"data":{"2M":"1T","2m":"1ST","3M":"2T","3m":"1T1ST","4aum":"3T","4j":"2T1ST","5j":"3T1ST","6M":"4T    1ST","6m":"4T","7M":"5T1ST","7m":"5T","8j":"6T"}}
    * Closing connection 0
    ```
    
    
  * `/songs/<interval>/<asc_des>`
  
    Donde `<interval>` tiene que ser la abreviatura de uno de los
    intervalos y `<asc_des>` el string "asc" o "des", según el
    intervalo sea ascendente o descendente. P.e.: `/songs/3M/des`
  
    Devuelve una lista de canciones representativas del intervalo.
    Cada item de la lista tiene el formato: `título: string`, `url:
    string`, `fav: string`. `título` es el título de la canción, `url`
    es una url donde se puede escuchar la canción y `fav` es "YES" o
    "NO" según la canción sea la favorita de la lista o no.
    
    Ejemplo:
     
    ```
    $ curl -v 127.0.0.1:5000/songs/2m/asc
    *   Trying 127.0.0.1:5000...
    * Connected to 127.0.0.1 (127.0.0.1) port 5000 (#0)
    > GET /songs/2m/asc HTTP/1.1
    > Host: 127.0.0.1:5000
    > User-Agent: curl/7.72.0
    > Accept: */*
    > 
    * Mark bundle as not supporting multiuse
    * HTTP 1.0, assume close after body
    < HTTP/1.0 200 OK
    < Content-Type: application/json
    < Content-Length: 76
    < Server: Werkzeug/1.0.1 Python/3.8.5
    < Date: Wed, 23 Sep 2020 14:25:01 GMT
    < 
    {"data":[["Tibur\u00f3n","https://www.youtube.com/watch?v=hNrwSDwHc54","NO"],["Pink Panther","https    ://www.youtube.com/watch?v=niA3u5-GF9I","NO"],["Isnt't she lovely (Steve Wonder)","https://www.yout    ube.com/watch?v=IVvkjuEAwgU","NO"]]}
    * Closing connection 0
    ```

### Funcionalidad de la app

La funcionalidad principal del cliente que tienes que desarrollar es
recuperar la información del servidor y mostrársela a la usuaria. Por
cada intervalo debe mostrar:

  * Ejemplo de dos notas cuya distancia sea la del intervalo.
  * Canciones representativas del intervalo.

Sobre el terreno tendrás que decidir cual es la operativa de la
interface: se muestran todos los intervalos a la vez, o bien la
usuaria selecciona uno y se muestra ese, se muestran todas la
canciones o sólo la principal con la opción de ver el resto, etc.

[Opcional] Para las canciones que tienen una url asociada: abrir la
url en el navegador.




## Planning

A continuación de presentan las _tasks_ a realizar. Recuerda que el
éxito de la misión depende en buena parte de que lleves a cabo las
_tasks_ en el orden planificado.


### TASK 1: Diseño de la interface

Tu primer cometido (_task_) en esta misión es diseñar la interface
gráfica. Comienza por cubrir el fichero `thinking.md` indicando:

  * Las partes que conformaran la interface.
  * Las decisiones tomadas en el desarrollo del diseño de la interface.
  
Una vez definida la estructura de la interface, dibuja un _wireframe_
de cada una de las ventanas, o la ventana si sólo hay una. Incluye los
wireframes en un fichero `wireframe.pdf`.

> Atención: A la hora de definir el wireframe, ten en cuenta que los
> nombres de los intervalos pueden ser muy cortos (3M asc) o muy
> largos (Tercera mayor ascendente). En el segundo caso no es correcto
> truncar el texto. Sin embargo los títulos de las canciones son muy
> largos (p.e. Suicide is painless (M.A.S.H. Theme), pero en función
> del diseño sí que puede ser una opción válida usar _ellipsis_
> (p.e. Suicide is painless ...)


Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero "thinking.md"](check:)
  * [El fichero "thinking.md" está actualizado](check:)
  * [Existe un fichero "wireframe.pdf"](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_design"](check:)
  * [El repositorio remote está actualizado](check:)

### Task 2: Implementación

Tu siguiente cometido es implementar el cliente con la interface que
diseñaste anteriormente.

El nombre del fichero ejecutable tiene que ser `ipm-p1.py`.

> Nota: La librería estándard incluye módulos para hacer peticiones
> por http (Ver, por ejemplo,
> https://docs.python.org/3/howto/urllib2.html) y _parsear_ json. Son
> suficientes para hacer peticiones al servidor.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero "ipm-p1.py" y es ejecutable](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_implementation"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 3: Testing

Para llevar a cabo este cometido tienes que escribir una prueba _e2e_
(_end to end_) para el escenario en que se muestra un intervalo de
tercera mayor ascendente (3M asc).

El escenario concreto varía en función del diseño de la interface. Por
ejemplo si se muestran todos los intervalos a un tiempo, el escenario
podría ser:

> Dado que  la aplicación se está ejecutando
> Entonces  la vista muestra el texto "Tercera mayor ascendente"

O, si por el contrario la usuaria debe selecionar el intervalo a
mostrar:

> Dado que  la aplicación se está ejecutando
> Cuando    la usuara selecciona el intervalo "Tercera mayor ascendente"
> Entonces  la vista del intervalo muestra el texto "Tercera mayor ascendente"

Ten en cuenta que las cadenas de texto que aparecen en los ejemplos no
tienen por que corresponderse con las que use tu aplicación. Tendrás
que adaptarlas si es necesario.

Para implementar la prueba tienes que usar el api de _AT-SPI_. Puedes
usar el api directamente o usar el módulo que se incluye con el
equipamiento de la misión.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero "test-p1.py" y es ejecutable](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_testing"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 4: Concurrencia y gestión de errores

A estas alturas has avanzado mucho en la misión, pero ahora viene la
parte que marca la diferencia.

En tu implementación habrá varias partes que realizan operaciones de
entrada/salida, principalmente las comunicaciones con el
servidor. Desconocemos a priori la duración de dichas operaciones,
pero sí sabemos que el tiempo puede ser superior a un segundo y que,
incluso, la operación puede fallar de diversas formas.

Tu siguiente cometido es mejorar la aplicación para:

  * Realizar las consultas al servidor de manera concurrente
    asegurando que la interface no se quede bloqueada.

  * Detectar y gestionar los errores en la comunicación con el
    servidor.
    
Crea un diagrama de secuencia (UML) donde se muestre la solución que
implementas. Incluye el diagrama en un fichero `concurrencia.pdf`.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero "concurrencia.pdf"](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_concurrency"](check:)
  * [El repositorio remote está actualizado](check:)



### Task 5: Diseño sw

Debido a un descuido imperdonable has llegado hasta aquí dejando atrás
un cometido imprescindible: el diseño software. Por culpa de este
error es posible que necesites realizar una reingeniería del código.

Comienza por estudiar el diseño software de la implementación
actual. A continuación adapta el diseño siguiendo las directrices del
patrón _MVC_ (_Model View Controller_) y de cualquier otro patrón
específico para interfaces gráficas que no sea el MVC.

Especifica ambos diseños empleando diagramas UML e incluye los
diagramas en los ficheros `mvc-static.pdf`, `mvc-dynamic.pdf` y
`alt-static.pdf` y `alt-dynamic.pdf`. Donde `alt` es el patrón
distinto de MVC, los ficheros `*-static.pdf` contienen los diagramas
estáticos y `*-dynamic.pdf` los dinámicos.

Considerar cual de los dos diseños se adapta mejor a tu implementación
y modifícala para que siga ese diseño.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero "mvc-static.pdf"](check:)
  * [Existe un fichero "mvc-dynamic.pdf"](check:)
  * [Existe un fichero "alt-static.pdf"](check:)
  * [Existe un fichero "alt-dynamic.pdf"](check:)
  * [Los ficheros "*.py" están actualizados"](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_sw_design"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 6: i18n

Tu último cometido es internacionalizar la aplicación y
_"localizarla"_ a los siguientes _locales_:
 
  * es_ES.utf8
  * en_US.utf8

No te olvides que además de los nombres de los intervalos y otros
textos que puedas haber incluido en la interface, el nombre de las
notas también cambia de un locale a otro.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Después de ejecutar "ipm-p1.py" con el locale "es_ES.utf8" los nombres de las notas siguen la notación latina"](check:)
  * [Después de ejecutar "ipm-p1.py" con el locale "en_US.utf8" los nombres de las notas siguen la notación inglesa"](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_i18n"](check:)
  * [El repositorio remote está actualizado](check:)


## Apéndices

### Apéndice: GITHUB

El repositorio oficial para la evaluación del assignment se alojará en
Github. El repositorio se creó automáticamente a partir del enlace de
Github Classroom que usaste y es el mismo que contiene este documento.

Recuerda que para que se pueda evaluar tu rendimiento en esta misión
(_assignment_):

  - Una vez creado el repositorio en la sección _Settings_ >
    _Notifications_ debes cubrir el campo _Address_ con la dirección
    de email del profesor que te evalua.
    
  - Cuando hagas un _push_ a este repositorio, si se detecta alguna
    anomalía que debas rectificar, el profesor abrirá un
    _issue_. Cuando lo considere corregido, el profesor cerrará el
    _issue_.
    
  - Para que la misión se pueda considerar completa, no puede haber
    _issues_ abiertos.
    
    
### Apéndice: símbolos utf-8

Los caracteres sostenido (_sharp_) y bemol (_flat_) se corresponden
con los _endpoints_ U+266f y U+266d, respectivamente. Es decir, en
python:

```python
sharp = u"\u266f"
flat  = u"\u266D"
```

> Recuerda: Como en python 3 el juego de caracteres por defecto es
> utf-8, puedes incluir directamente estos caracteres en tu código.

```python
>>> s = "♯♭"
>>> print(s)
♯♭
```

### Apéndice: requirements.txt, pip y venv

El fichero `requirements.txt` contine la lista de librerías y paquetes
necesarios para llevar a cabo la misión.

Puedes usar `pip` para instalarlos. La instalación puede ser:

  * global. Seguramente necesitarás permisos de superusuario.

  * user. Quedarán instalado únicamente en tu cuenta de usuario (en tu
    HOME).
  
  * entorno virtual (virtual enviroment). Quedarán instalados
    únicamente en el entorno virtual (directorio).
  
Recuerda que, para probar tu código, puedes crear un virtual
environment y activarlo,

```
$ python3 -m venv p1-check
$ . p1-check/bin/activate
(p1-check)$
```

Clonar el repositorio de github,
```
(p1-check)$ git clone _url_p1_repo_
(p1-check)$ cd _p1_repo_
```

Instalar las librerías en el virtual environment,
```
(p1-check)$ pip install requirements.txt
```

Y ejecutar tu código con una configuración lo más parecida posible a
la del profesor.


## Apéndice: El servidor y las librerías

El código del servidor está en el repositorio:
[https://github.com/cabrero/ipm2021_01_server](https://github.com/cabrero/ipm2021_01_server)

Puedes instalarlo a través del fichero `requirements.txt`, mediante
`pip` o cualquier medio que consideres.

Por ejemplo, puedes instalarlo en tu cuenta de usuario:

```
$ pip install --user https://github.com/cabrero/ipm2021_01_server/archive/master.zip
```

> Atención: No olvides incluir en el PATH el directorio donde se
> instalan los ejecutables
> [https://pip.pypa.io/en/stable/user_guide/#user-installs](https://pip.pypa.io/en/stable/user_guide/#user-installs)

El mismo razonamiento es válido para las librerías adicionales. Las urls serían:

  * [https://github.com/cabrero/p1libs.git](https://github.com/cabrero/p1libs.git)
  * https://github.com/cabrero/p1libs/archive/master.zip
