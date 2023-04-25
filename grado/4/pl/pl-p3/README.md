# Interpretación de órdenes para un sistema domótico en lenguaje *pseudo* natural

## Autores

* Hector Padín Torrente (hector.padin@udc.es)
* Pedro Guijas Bravo (p.guijas@udc.es)

## Intérprete

La idea para este proyecto es la de desarrollar un intérprete de lenguaje *pseudo* natural que sea capaz de comunicarse con un *embedded system*, el encargado de realizar acciones como apagar las luces, cerrar la casa, abrir las persianas, etc. La parte del *embedded system* no se implementará, simplemente arrancaremos un servidor en local que sea capaz de recibir las órdenes que nosotros le enviemos desde nuestro intérprete, simulando una ejecución de estas. En un entorno real, este sería un servidor completamente operativo, capaz de realizar ciertas acciones sobre objetos de la casa, e incluso de manera programable.

El intérprete recibirá las instrucciones en lenguaje *pseudo* natural mediante un fichero, que generará el propio servidor, que será el encargado de recibir las órdenes del cliente y ejecutarlas. Este, analizaría el texto de entrada y en caso de que hubiese algún error en las sentencias (apagar puerta) nos lo comunicaría. En caso de que no hubiese ningún error, realizaría una conexión vía TCP con el servidor y enviaría la orden.

Aunque la idea del proyecto es realizar una terminal interactiva, en la que se le vayan dando órdenes, con el fin de servir como base para el desarrollo de una aplicación móvil para controlar los dispositivos de la casa. A continuación se muestran una serie de pequeños ejemplos que podría recibir nuestro intérprete:

## Ejecucion

Ejemplo de ejecución del código:

```bash
~ $ make
~ $ nohup python3 server.py &
~ $ python3 client.py
client > Ejecutar las órdenes aqui
...
client > ^C
~ $ kill %1
```

## Dispositivos y acciones

Actualmente el sistema consta de los siguientes dispositivos:

* Lámpara (mesilla).
* Luz (luz de la sala).
* Termómetro.
* Calefacción.
* Ventana.
* Persiana.
* Televisión.

## Ejemplos de ejecución

```bash
> <acción>
> abre la ventana
> enciende la luz
> cierra la ventana enciende la luz y apaga la estufa
> <acción> cuando <condición>
> enciende la calefaccion a cuando la temperatura sea 18º
> enciende la luz cuando den las 22:30:55
> cierra la ventana cuando la temperatura sea de 18º
> cierra la ventana cuando el termometro marque 18º
> cierra la ventana cuando el termometro ponga 18º
> cierra la ventana cuando el termometro ponga 18º por semana
> cierra la ventana y enciende la luz cuando el termometro ponga 18º por semana
> <acción> <rutina>
> abre la ventana todos los dias a las 12 pm
> enciende la luz por semana a las 7:15 am
> cierra la ventana y enciende la calefaccion los lunes a las 22:00 
> <rutina> <acción>
> cada 12 horas abre la ventana y apaga la calefaccion
> cada 12 horas 2 minutos y 20 segundos enciende la calefaccion
```

## Manejo de errores

A continuación mostramos una serie de errores intencionados para ver cómo reacciona el intérprete ante estos.

```bash
client > cierra el portal
No he encontrado el dispositivo 'portal' en casa.
client > voltea la ventana
No entiendo qué acción quieres que haga, no sé qué es voltea.
client > cierra la luz
El dispositivo luz no cuenta con esa acción.
client > abre la ventana cuando abre la ventana
Perdona... creo que no he entendido muy bien lo que querías decir
client > cada abre la ventana
No te he entendido, ¿cada cuanto tengo que hacer 'abre ventana'?
client > abre la ventana el termometro ponga 18º
No he entendido cuando tengo que hacer 'abre ventana'.
client > el termometro ponga 18º
Vaya, creo que no he entendido muy bien lo que quieres que haga...
client > kajsd kas fkajdsf kajd fkadj fkd
No entiendo qué acción quieres que haga, no sé qué es kajsd.
client > cuando termometro marque 18º
No he entendido muy bien tu frase, problemas de ser un autómata...
```

Como vemos, dependiendo del tipo de error, el sistema se dará cuenta de que la frase no está bien formada y de lo que necesita el usuario para formular bien la frase. En caso de que la frase no tenga ningún sentido, lo más seguro es que nos muestre un error genérico de que no nos ha entendido.

### Peculiaridades

No aceptamos comas como concatenadores de acciones, ya que el sistema está pensado para reconocer órdenes desde texto que se recoge por el habla.
