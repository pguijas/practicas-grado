# ASSIGNMENT: PRÁCTICA 2 - INTERFACES GRÁFICAS DE USUARIA PARA APLICACIONES MÓVILES

## Presentación del _assignment_

A continuación se describe la misión (_assignment_).

### Objetivo

En esta misión (_assignment_) tendrás que desarrollar una aplicación
para dispositivos móviles. Para alcanzar tu objetivo tendrás que usar los
conocimientos adquiridos sobre el desarrollo de interfaces gráficas de
usuaria.

Para llevar a cabo tu cometido dispondrás del toolkit multiplataforma
`flutter` y el lenguaje de programación asociado `dart`.


### Plazo

Se espera que hayas finalizado tu misión antes del día **24/11/2020 a
las 23:59:59**. Cualquier retraso en la finalización de tu cometido
será penalizado de acuerdo con el reglamento de la asignatura.


### Introducción al _assignment_

Nuestros espías han descubierto la existencia de servicios capaces de
analizar imágenes y obtener de manera automática sus características
más relevantes empleando modernas técnicas de I.A. Tu misión es
desarrollar una aplicación que permita a la usuaria realizar una
fotografía y obtener sus características relevantes.

Como ya hemos dicho, ya existen servicios capaces de analizar de
manera automática las imágenes, así que tu misión se centra en la
parte cliente.


### Recursos

La aplicación se validará en la plataforma android para dispositivos
móviles.

Debes procurarte las siguiente herramientas:

* `android` versión >= 8.0
* `flutter` versión >= 1.22
* `Git`
* Un repositorio en [github.com](github.com). Puedes ver los detalles
  en el apéndice correspondiente.

El resto de los recursos necesarios se irán presentando con cada
cometido (_task_), a medida que sean necesarios.


### Background

Actualmente hemos identificado distintos servicios basados en las
técnicas más actuales de _Machine Learning_ aplicados a distintos
campos de la I.A.. Varios de ellos incluyen el reconocimiento de
imágenes y ofrecen un API remoto para acceder desde nuestras
aplicaciones cliente.

En general estos APIs ofrecen una, o varias llamadas a través de las
cuales nuestra aplicación puede enviar una imagen al servidor y
obtener como respuesta la información extraída. Dicha información
puede incluir datos como los colores dominantes, la localización de
caras dentro de la imagen, etiquetas para los objetos contenidos en la
imagen, etc.

Como es de esperar, la variedad de información recuperada y la calidad
de la misma depende, a su vez, de la calidad del servicio. No obstante,
los criterios para seleccionar el servicio serán los siguientes:

  * API REST sencillo. Queremos usarlo desde nuestro código con un
    simple cliente http, sin necesidad de librerías extra.
  * Plan gratuito. Aunque tenga un límite de peticiones.

### El servidor

El servidor seleccionado es (**imagga**)[https://imagga.com]. La
documentación del API del mismo está disponible en (imagga
api)[https://docs.imagga.com]

Para poder usar los servicios del API de imagga es necesario crear una
cuenta y obtener las claves de uso.

En la documentación del api se ofrecen varios ejemplos que se pueden
probar incluso desde la línea de comandos usando una aplicación como
`curl`.

Igualmente en la documentación puedes observar las distintas
categorías de información que es capaz de proporcionar el servidor:
etiquetas, categorías, caras, colores, texto, ...

## Planning

A continuación de presentan las _tasks_ a realizar. Recuerda que el
éxito de la misión depende en buena parte de que lleves a cabo las
_tasks_ en el orden planificado.


### TASK 1: Recuperar información

Tu primer cometido (_task_) en esta misión es desarrollar una primera
versión de la aplicación. Esta primera versión implementará los
siguientes pasos:

  * Tomar una fotografía.
  * Subir la imagen al _endpoint_ del API seleccionado.
  * Mostrar el resultado.

> Atención: La aplicación usará un único _endpoint_. Estudia los
> _endpoints_ disponibles y seleccionar el que consideres oportuno.

Comienza por seleccionar un _endpoint_ entre los que ofrece el API del
servidor. A continuación diseña la interfaz y crea un _wireframe_ con
las pantallas necesarias. Incluye los wireframes en un fichero
`wireframe.pdf`.

> Atención: La forma óptima para presentar los resultados a la usuaria
> depende de la naturaleza de los mismos, es decir, depende del
> _endpoint_ seleccionado. No es lo mismo presentar la información del
> _endpoint_ `/faces`,  `/colors`

> Atención: La herramienta `flutter create` crea un fichero
> `.gitignore`.  Ajústate a sus reglas.

Una vez realizado el diseño, impleméntalo usando la librería
`flutter`. La aplicación resultante tiene que ser funcional en una
plataforma android (8.0 o posterior).

No olvides realizar las peticiones al servidor de manera concurrente y
gestionar los errores. A mayores de los errores de red, en un
dispositivo móvil puede aparecer otros errores como:
 
  * La aplicación no tiene permiso para usar la red.
  * La aplicación no tiene permiso para usar la cámara.

Ten en cuenta los siguientes recursos durante la
realización de este cometido:

  * Existen dos librerías para usar la cámara del dispositivo:
    (Take a picture using flutter camera)[https://medium.com/@navinkumar0118/take-a-picture-using-flutter-camera-a9c11d282632]
    
  * La sección _cookbooks_ de la documentación de _flutter_ incluye un
    apartado de
    (_networking_)[https://flutter.dev/docs/cookbook#networking],
    donde encontrar ejemplos de peticiones http.
  
  
  
Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_MVP"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 2: Diseño sw

Tu siguiente cometido es aplicar un patrón de diseño software adecuado
al tipo de librería empleada. La tarea incluye crear el diseño
software y modificar la implementación para seguir el diseño.

Comienza la tarea por analizar los distintos modelos disponibles. En
la documentación de _flutter_ la sección dedicada al manejo de estado
(_state managment_) tiene un apartado en el que se describen algunas
(opciones)[https://flutter.dev/docs/development/data-and-backend/state-mgmt/options]
, al igual que en la web (fluttersamples)[https://fluttersamples.com/].

A continuación selecciona un modelo distinto del Model View Controller
(MVC) o sus variantes, realiza el diseño de tu aplicación siguiendo el
modelo que has elegido y expresando el modelo en UML. Incluye vistas
estáticas y dinámicas del diseño.

Una vez realizado el diseño, adapta la implementación de la app al
diseño.


Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_sw_design"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 3: Adaptabilidad

Tras una breve labor de observación, sabemos que las pantallas de los
dispositivos pueden variar su configuración: horizontal o vertical, y
que el tamaño de la pantalla puede variar sustancialmente entre
dispositivos que normalmente categorizamos como móviles o tablets.

Mejora tu aplicación para que detecte distintas configuraciones
(pantalla vertical vs horizontal) y distintos dispositivos (móvil vs
tablet), y que use el diseño de la vista de resultados más adecuada
para cada caso.

Comienza diseñando una vista de resultados adecuada para cada caso y
termina el cometido implementando la detección de dispositivos y
configuraciones y los distintos diseños de la vista.

Recuerda que, una vez más, en la documentación de _flutter_ puedes
encontrar ejemplos relevantes para este cometido.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_response"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 4: Material

Para que tu app se integre en el ecosistema de apps de android es
necesario seguir su mismo sistema de diseño (_design system_).  Este
sistema, creado por google, es (_material_)[https://material.io/].

La librería de _flutter_ `material.dart` ya realiza un gran trabajo
para adaptar la interfaz a las guías de _material_. Sin embargo, a
pesar que hayas usado esta librería, aún puede haber discrepancias.

Repasa las guías de _material_ y cambia aquellos aspectos de tu app
que no se ajustan a lo establecido por el sistema de diseño. Anota en
un fichero `material.md` todos los cambios que realices.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_material"](check:)
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
    de email del profesor que te evalúa.
    
  - Cuando hagas un _push_ a este repositorio, si se detecta alguna
    anomalía que debas rectificar, el profesor abrirá un
    _issue_. Cuando lo considere corregido, el profesor cerrará el
    _issue_.
    
  - Para que la misión se pueda considerar completa, no puede haber
    _issues_ abiertos.
    
    
