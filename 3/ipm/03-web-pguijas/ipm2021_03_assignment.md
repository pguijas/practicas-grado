# ASSIGNMENT: PRÁCTICA 3 - INTERFACES GRÁFICAS DE USUARIA PARA APLICACIONES WEB

## Presentación del _assignment_

A continuación se describe la misión (_assignment_).

### Objetivo

En esta misión (_assignment_) tendrás que desarrollar una aplicación
web. Para alcanzar tu objetivo tendrás que usar los conocimientos
adquiridos sobre el desarrollo de interfaces gráficas de usuaria.

Para llevar a cabo tu cometido dispondrás de los lenguajes `html5`,
`css3` y `javascript`.


### Plazo

La misión comienza el día **25/11/20 a las 00:00:00** y se espera
que hayas finalizado tu misión antes del día **22/12/20 a las
23:59:59**. Cualquier retraso en la finalización de tu cometido será
penalizado de acuerdo con el reglamento de la asignatura.

La planificación queda como sigue:

  * Primera semana: 25/11/20 - 01/12/20
  * Segunda semana: 02/12/20 - 08/12/20
  * Tercera semana: 09/12/20 - 15/12/20
  * Cuarta semana:  16/12/20 - 22/12/20
  

### Introducción al _assignment_

La expansión de los _pokémon_ es cada vez más notoria, y queremos
controlar toda la información disponible sobre ellos. Tu misión es
desarrollar una aplicación que permita a la usuaria consultar el
catálogo de pokémons existente.

La información pertinente se encuentra almacenada en el servidor
**PokéApi**, y tu misión se centra en desarrollar un cliente
web.


### Recursos

La aplicación es una aplicación web que debe ajustarse a los
siguientes estándares:

  * html5
  * css3
  * javascript

Debes procurarte las siguiente herramientas:

* Navegador/es web.
* `Git`
* Un repositorio en [github.com](github.com). Puedes ver los detalles
  en el apéndice correspondiente.

El resto de los recursos necesarios se irán presentando con cada
cometido (_task_), a medida que sean necesarios.


### El servidor

El servidor **PokéApi** está accesible en la dirección
[https://pokeapi.co/](https://pokeapi.co/). Ofrece un API REST
sencillo, que podemos usar sin necesidad de librerías extra y con el
que podemos experimentar desde la línea de comandos con herramientas
como `curl`, o desde la propia web del servidor.


### Requisitos no funcionales

  * Todo el código se debe ajustar a los estándares correspondientes,
    en todas las tareas de la misión.
	
    En especial, el código `html5` y `css` tiene que ser examinado por
	el validador del **W3C**
	[https://validator.w3.org/](https://validator.w3.org/) sin emitir
	errores.
	
  * No se puede usar ninguna librería de terceros, ni para la parte de
    `css`, ni para la parte de `javascript`, en ninguna de las tareas
	de la misión.
	
  * Dentro del diseño gráfico de la interface tienes que ajustarte a
    la paleta de colores y el emparejamiento de fuentes que te
    proporciona la web: [uicoach.io/](https://uicoach.io/) en la
    opción _Generate challenge_. Esta información se genera de forma
    aleatoria, así que puedes buscar la combinación que prefieras.
  

## Planning

A continuación de presentan las _tasks_ a realizar. Recuerda que el
éxito de la misión depende en buena parte de que lleves a cabo las
_tasks_ en el orden planificado.

### TASK 1: Mobile first

Tu primer cometido (_task_) en esta misión es desarrollar una primera
versión de la aplicación, centrándote exclusivamente en los
dispositivos móviles: _smartphones_.

La aplicación debe permitir la consulta del catálogo de _pokémons_.
Como parte del proceso de análisis y diseño, tienes que decidir la
estructura lógica que ofrecerá tu aplicación a la usuaria: por
_pokédex_, por criterios de búsqueda, por categorías, etc. Describe la
estructura en un fichero `arquitectura_informacion.pdf`.

En base a tu análisis, diseña la interfaz y crea un _wireframe_ con
las páginas necesarias. Incluye los wireframes en un fichero
`wireframe.pdf`.

Una vez realizado el diseño, impleméntalo. No olvides realizar las
peticiones al servidor de manera concurrente y gestionar los errores.

  
Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero 'arquitectura_informacion.pdf'](check:)
  * [Existe un fichero 'wireframe.pdf'](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_mobile_first"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 2: Accesibilidad

Tu siguiente cometido es asegurar que tu aplicación cumple los mayores
estándares de accesibilidad posibles.

Crea un listado de todos los elementos, tanto estáticos como
dinámicos, que forman parte de la interface de tu aplicación. Para
cada uno de ellos:

  * Anota los aspectos de accesibilidad relacionados con el elemento.
  
  * Si presenta algún problema, fallo o carencia en lo se refiere a la
    accesibilidad, corrígelo y anota la corrección correspondiente.
  

Incluye el listado con todas las anotaciones en un fichero `a11y.pdf`.

> Una vez más la referencia son las normas publicadas por el _W3C_.
> Tanto para la accesibilidad web en general [WCAG
> 2](https://www.w3.org/WAI/standards-guidelines/wcag/), como para la
> accesibilidad de aplicaciones web
> [WAI-ARIA](https://www.w3.org/WAI/standards-guidelines/aria/).



Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero 'a11y.pdf'](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_a11y"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 3: Diseño adaptativo

Para esta tarea tienes que aplicar las técnicas de **RWD** (Responsive
Web Development) y mejorar tu aplicación de manera que se adapte a
distintos tipos de dispositivos: smartphone, tablet y desktop.

Actualiza el diseño e incluye los cambios en el fichero
`wireframe.pdf`. A continuación, implementa los cambios necesarios
para que la interface de tu aplicación se adapte al diseño definido
para cada uno de los dispositivos.


Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [El fichero 'wireframe.pdf' está actualizado](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_responsive"](check:)
  * [El repositorio remote está actualizado](check:)


### Task 4: Cross-browsing

Para este cometido, tomaremos como referencia los navegadores
_Firefox_, y _Chrome_ en sus versiones desde dos años antes de la
fecha de comienzo de la misión.

Crea un listado con todas las propiedades y selectores empleados en
las hojas de estilo (`css`) de la aplicación. Para cada ítem de la
lista:

  * Anota las versiones de Firefox y Chrome a partir de las cuales es
    compatible.
	
  * En caso de que no sea compatible con alguna versión desde dos años
    atrás, deberás llevar a cabo alguna de las siguientes acciones:
	
	- Eliminar dicha propiedad o elemento, y usar en su lugar otro
      compatible.
	  
    - Usar técnicas de [detección de
      características](https://developer.mozilla.org/en-US/docs/Learn/Tools_and_testing/Cross_browser_testing/Feature_detection)
      para implementar una alternativa adecuada.
  
    - Añadir algún truco o
	  [_polyfill_](https://developer.mozilla.org/es/docs/Glossary/Polyfill)
	  para que la aplicación funcione correctamente en las versiones
	  implicadas.
	  
	  > _IMPORTANTE_: A los _polyfills_, no se les aplica el requisito
	  > no funcional "No se puede usar ninguna librería de terceros".

A continuación, emplea un servicio como el ofrecido por
[LambdaTest](https://www.lambdatest.com/) para obtener screenshots de
las páginas de tu aplicación en las versiones relevantes de los
navegadores implicados: _Firefox_ y _Chrome_, en distintos
dispositivos: smartphones, tablets y desktops. Guarda los screenshots
en un directorio `screenshots`.

> NOTA: No olvides que con tu cuenta de estudiante de github tienes
> gratis el plan 'Live' de LambdaTest durante el curso.

Antes de que este cometido se pueda considerar completo debes
asegurarte de haber alcanzado las siguientes condiciones
(_requirements_):

  * [Existe un fichero 'cross_browsing.pdf'](check:)
  * [Existe un directorio 'screenshots' y no está vacío](check:)
  * [No hay ficheros pendientes de commit](check:)
  * [El último commit tiene la etiqueta "task_cross_browsing"](check:)
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
    
    
