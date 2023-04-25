# Arquitectura do Software

## Práctica Final (curso 2021/2022)

## P2Pibes

El objetivo de esta práctica es el de elaborar un **sistema p2p** destinado a la compartición distribuida de ficheros. Cada peer tendrá una serie de vecinos a los cuales podrá delegar la necesidad de información. Así mismo, todo peer tendrá una serie de ficheros que estará compartiendo con la red.

El caso principal de uso sería el de buscar un archivo (por ejemplo "ejemplo.txt"), difundir esta búsqueda y elaborar una lista de resultados (nombre, tamaño, hash). Una vez elaborada la lista, el usuario podrá solicitar el archivo directamente al peer poseedor. Así, las funciones disponibles en el sistema son: añadir nuevos nodos (`up_node/1` y `up_node/2`) y bajarlos (`down_node/1`), manejar los vecinos (`add_neighbor/2` , `add_neighbor/2` y `get_neighbors/1`), manejar archivos (`add_file/2` , `rm_file/2` y `get_files/1`), buscar archivos (`search/2` y `get_results/2`) y descargar archivos (`download_file/2`).

El requisito no funcional que se nos asignó para que nos centrásemos en él es el rendimiento. Para optimizar aplicamos principalmente tres tácticas: Uso de TTL para cortar peticiones que viven demasiado tiempo en la red; guardado de datos en caché para dar respuesta más rápido a datos que hayan sido consultados recientemente; y un registro de nodos por los que circula una petición, a fin de evitar ciclos.

Un ejemplo de mejora del sistema que podríamos implementar en próximas versiones es un ranking de archivos (ordenándolos por puntuación o velocidad), enfocar el sistema a un despliegue no local sobre TCP/IP etc...

### Configuración y funcionamiento

Este proyecto fue creado como un proyecto Phoenix mediante `mix phx.new NOMBRE --no-ecto`. Una vez descargado, se configuran las dependencias con `mix deps.get` y se ejecutan las pruebas con `mix test`. El sistema se inicia ejecutando el comando `mix phx.server` en el directorio raíz del proyecto. La documentación en formato HTML se puede obtener con `mix docs`.

Este proyecto tiene soporte para [Credo](https://github.com/rrrene/credo).
