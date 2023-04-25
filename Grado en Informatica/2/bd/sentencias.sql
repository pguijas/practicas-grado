--1
select cp.comunidad, cm.nombre COMUNIDAD, cp.proveedor, p.nombre PROVEEDOR, fecha from compra cp 
	join comunidad cm on cm.id = cp.comunidad 
	join proveedor p on cp.proveedor = p.id;
--2
select t.nombre, cantidad from item i join tipo_producto t on i.tipo=t.id where compra=1;
--3
select count(DISTINCT entrega) Num_Entregas from contenido_entrega where compra=1;
--4
select c.entrega, tp.nombre, c.cantidad, e.fecha_estimada from contenido_entrega c 
	join entrega e on c.entrega=e.id
	join item i on c.item=i.num AND c.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	where c.compra=1
	order by c.entrega;
--5
select c.entrega, tp.nombre, c.cantidad "c entrega", i.cantidad "c comprada", c.cantidad/i.cantidad "entreg/comprado", e.fecha_estimada from contenido_entrega c 
	join entrega e on c.entrega=e.id
	join item i on c.item=i.num AND c.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	where c.compra=1
	order by c.entrega;
--6
select v.id, v.procedencia, c.nombre destino, to_char(fecha_salida,'DD/MM/YYYY HH24:MI') salida, to_char(fecha_llegada,'DD/MM/YYYY HH24:MI') llegada from vuelo v 
	join comunidad c on v.destino=c.id 
	where to_char(fecha_salida,'DD/MM/YYYY')='01/05/2020';
--7 (Group por si lleva mismo tipo de productos de distintas compras -> para que no se repitan)
select cmd.nombre Comunidad, tp.nombre, sum(c.cantidad) Cantidad from contenido_entrega c 
	join entrega e on c.entrega=e.id
	join item i on c.item=i.num AND c.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	join compra on c.compra=compra.id
	join comunidad cmd on cmd.id=compra.comunidad
	where e.vuelo=2
	group by tp.nombre, cmd.nombre;
--8 (Group by -> distintas compras al mismo proveedor)
select tp.nombre, sum(c.cantidad) Cantidad, compra.proveedor, compra.comunidad from contenido_entrega c 
	join entrega e on c.entrega=e.id
	join item i on c.item=i.num AND c.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	join compra on c.compra=compra.id
	where e.vuelo=2
	group by tp.nombre, compra.proveedor, compra.comunidad;
--9
select c.id centro, c.nombre, comn.nombre comunidad, count(semana) "Num Solicitudes" from centro_sanitario c
	left join (select centro, semana from necesidad where fecha<to_date('01/05/2020 00:00', 'DD/MM/YYYY HH24:MI')) n on c.id=n.centro
	join comunidad comn on c.comunidad=comn.id
	group by c.id, c.nombre, comn.nombre
	order by c.id;
--10
select pn.semana, n.fecha, tp.nombre producto, pn.cantidad from productos_necesitados pn 
	join tipo_producto tp on tp.id=pn.producto 
	join necesidad n on n.centro=1 AND n.semana=pn.semana
	where pn.centro=1;
--11 (Group By -> Agrupar tipos de productos iguales de distintas compras)
select tp.nombre producto, cm.nombre comunidad, cs.nombre centro , sum(pd.cantidad) Cantidad from productos_destinados pd
	join centro_sanitario cs on pd.centro=cs.id
	join compra c on c.id=pd.compra 
	join comunidad cm on c.comunidad=cm.id 
	join item i on pd.compra=i.compra AND pd.item=i.num
	join tipo_producto tp on i.tipo=tp.id
	where vuelo=2
	group by tp.nombre, cm.nombre, cs.nombre;
--12
select ru.nombre "Nonmbre Ruta", re.orden, c.nombre centro, com.nombre comunidad from recorridos re 
	join ruta ru on re.ruta=ru.num
	join centro_distribucion c on c.id=re.centro
	join comunidad com on c.comunidad=com.id
	order by ru.nombre, re.orden;
--13
select c.id "Id Centro", c.nombre "Nombre Centro", com.nombre Comunidad, count(r.orden) from recorridos r
	right join centro_distribucion c on r.centro=c.id
	join comunidad com on c.comunidad=com.id
	group by c.id, c.nombre, com.nombre
	order by c.id;
--14 (añadimos fecha_hora porque puede haber varios en un dia)
select conductor, camion, to_char(fecha_hora, 'DD/MM/YYYY HH24:MI') Fecha_Hora from viaje where ruta=1 AND to_char(fecha_hora, 'DD/MM/YYYY')='01/05/2020';
--15 
select tp.nombre producto, t.vuelo from transporta t
	join item i on t.item=i.num AND t.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	where t.ruta=1 AND to_char(t.fecha_hora, 'DD/MM/YYYY HH24:MI')='01/05/2020 10:30'
	group by tp.nombre, t.vuelo;
--16 (group by por si hay mismos tipos de producto de distintas compras o entregas)
select tp.nombre producto, sum(t.cantidad) cantidad from transporta t
	join item i on t.item=i.num AND t.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	where t.ruta=1 AND to_char(t.fecha_hora, 'DD/MM/YYYY HH24:MI')='01/05/2020 10:30'
	group by tp.nombre;
--17 
select to_char(e.fecha_entrega, 'DD/MM/YYYY HH24:MI') fecha, c.nombre centro, tp.nombre producto, sum(e.cantidad) Cantidad from entregado e
	join item i on e.item=i.num AND e.compra=i.compra
	join tipo_producto tp on tp.id=i.tipo
	join centro_distribucion c on c.id=e.centro
	where e.ruta=1 AND to_char(e.fecha_hora, 'DD/MM/YYYY HH24:MI')='01/05/2020 10:30'
	group by e.fecha_entrega, c.nombre, tp.nombre;
--18 
--	Join Exterior
--	Muestra para todas las compras cuantas entregas tienen (Mostrando también el id y nombre de la comunidad y el id y nombre del proveedor).
select c.id compra, cmd.nombre comunidad, p.nombre proveedor, count(distinct ce.entrega) from compra c 
	left join contenido_entrega ce on c.id=ce.compra
	join comunidad cmd on c.comunidad=cmd.id
	join proveedor p on c.proveedor=p.id
	group by c.id, cmd.nombre, p.nombre
	order by c.id;
--19 
-- 	Expresion de consulta (como la 9)
--	Cuanto más cara fué la compra más cara en comparación a las otras
select id, (select max(precio) from compra)-precio "Diferencia con la mas cara" from compra;
--20 
--	Subconsulta de fila
--	¿Cuál es o son los Centros Sanitarios que necesitan o necesitaron más respiradores?, ¿cuántos?
--	(tipo_producto id=2)
select distinct pn.centro, cs.nombre centro, pn.cantidad from productos_necesitados pn
	join centro_sanitario cs on cs.id=pn.centro
	where (producto,cantidad) = SOME (select producto, max(cantidad) from productos_necesitados
											where producto=2
											group by producto);