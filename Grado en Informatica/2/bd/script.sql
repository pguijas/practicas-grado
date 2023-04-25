DROP TABLE comunidad CASCADE CONSTRAINTS;
DROP TABLE proveedor CASCADE CONSTRAINTS;
DROP TABLE tipo_producto CASCADE CONSTRAINTS;
DROP TABLE compra CASCADE CONSTRAINTS;
DROP TABLE item CASCADE CONSTRAINTS;
DROP TABLE vuelo CASCADE CONSTRAINTS;
DROP TABLE entrega CASCADE CONSTRAINTS;
DROP TABLE contenido_entrega CASCADE CONSTRAINTS;
DROP TABLE centro_sanitario CASCADE CONSTRAINTS;
DROP TABLE necesidad CASCADE CONSTRAINTS;
DROP TABLE productos_necesitados CASCADE CONSTRAINTS;
DROP TABLE productos_destinados CASCADE CONSTRAINTS;
DROP TABLE centro_distribucion CASCADE CONSTRAINTS;
DROP TABLE ruta CASCADE CONSTRAINTS;
DROP TABLE recorridos CASCADE CONSTRAINTS;
DROP TABLE conductor CASCADE CONSTRAINTS;
DROP TABLE camion CASCADE CONSTRAINTS;
DROP TABLE viaje CASCADE CONSTRAINTS;
DROP TABLE transporta CASCADE CONSTRAINTS;
DROP TABLE entregado CASCADE CONSTRAINTS;

CREATE TABLE comunidad(
	id NUMERIC(2) PRIMARY KEY,
	nombre VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE proveedor(
	id NUMERIC(4) PRIMARY KEY,
	nombre VARCHAR(20) UNIQUE NOT NULL,
	loc VARCHAR(35)
);

CREATE TABLE tipo_producto(
	id NUMERIC(4) PRIMARY KEY,
	nombre VARCHAR(20) UNIQUE NOT NULL,
	descipcion VARCHAR(35)
);

CREATE TABLE compra(
	id NUMERIC(10) PRIMARY KEY,
	precio NUMERIC(10) NOT NULL,
	fecha DATE NOT NULL,
	proveedor NUMERIC(4) NOT NULL REFERENCES proveedor (id),
	comunidad NUMERIC(2) NOT NULL REFERENCES comunidad (id)
);

CREATE TABLE item(
	compra NUMERIC(10) REFERENCES compra (id), 
	num NUMERIC(4),
	tipo NUMERIC(4) NOT NULL REFERENCES tipo_producto (id),
	cantidad NUMERIC(10) NOT NULL,
	PRIMARY KEY (compra,num)
);

CREATE TABLE vuelo(
	id NUMERIC(4) PRIMARY KEY,
	fecha_salida DATE NOT NULL,
	fecha_llegada DATE NOT NULL,
	procedencia VARCHAR(20) NOT NULL,
	destino NOT NULL REFERENCES tipo_producto (id)
);

CREATE TABLE entrega(
	id NUMERIC(4) PRIMARY KEY,
	fecha_estimada DATE NOT NULL,
	vuelo NUMERIC(4) NOT NULL REFERENCES vuelo
);

CREATE TABLE contenido_entrega(
	compra NUMERIC(10), 
	item NUMERIC(4), 
	entrega NUMERIC(4) REFERENCES entrega,
	cantidad NUMERIC(10) NOT NULL,
	FOREIGN KEY (compra,item) REFERENCES item,
	PRIMARY KEY (compra,item,entrega)
);

CREATE TABLE centro_sanitario(
	id NUMERIC(4) PRIMARY KEY,
	nombre VARCHAR(20) UNIQUE NOT NULL,
	loc VARCHAR(35) NOT NULL,
	comunidad NUMERIC(2) NOT NULL REFERENCES comunidad
);

CREATE TABLE necesidad(
	centro NUMERIC(4) REFERENCES centro_sanitario,
	semana NUMERIC(4),
	fecha DATE NOT NULL,
	PRIMARY KEY (centro,semana)
);

CREATE TABLE productos_necesitados(
	centro NUMERIC(4),
	semana NUMERIC(4),
	producto REFERENCES tipo_producto,
	cantidad NUMERIC(4) NOT NULL,
	FOREIGN KEY (centro,semana) REFERENCES necesidad,
	PRIMARY KEY (centro,semana,producto)
);


CREATE TABLE productos_destinados(
	centro NUMERIC(4),
	semana NUMERIC(4),
	compra NUMERIC(10) , 
	item NUMERIC(4),
	vuelo NUMERIC(4) REFERENCES vuelo,
	cantidad NUMERIC(4) NOT NULL,
	FOREIGN KEY (centro,semana) REFERENCES necesidad,
	PRIMARY KEY (centro,semana,compra,item,vuelo)
);

CREATE TABLE centro_distribucion(
	id NUMERIC(4) PRIMARY KEY,
	nombre VARCHAR(20) UNIQUE NOT NULL,
	loc VARCHAR(35) NOT NULL,
	comunidad NUMERIC(2) NOT NULL REFERENCES comunidad
);

CREATE TABLE ruta(
	num NUMERIC(4) PRIMARY KEY,
	nombre VARCHAR(20) NOT NULL
);

CREATE TABLE recorridos(
	ruta NUMERIC(4) REFERENCES ruta,
	centro NUMERIC(4) REFERENCES centro_distribucion,
	orden NUMERIC(2) NOT NULL,
	PRIMARY KEY (ruta,centro)
);

CREATE TABLE conductor(
	dni VARCHAR(9) PRIMARY KEY,
	domicilio VARCHAR(35) NOT NULL,
	telefono NUMERIC(11) NOT NULL
);

CREATE TABLE camion(
	matricula VARCHAR(7) PRIMARY KEY,
	color VARCHAR(10) NOT NULL,
	modelo VARCHAR(10) NOT NULL
);

CREATE TABLE viaje(
	ruta NUMERIC(4),
	fecha_hora DATE,
	conductor VARCHAR(9) NOT NULL REFERENCES conductor,
	camion VARCHAR(7) NOT NULL REFERENCES camion,
	PRIMARY KEY (ruta,fecha_hora)
);

CREATE TABLE transporta(
	compra NUMERIC(10), 
	item NUMERIC(4),
	ruta NUMERIC(4),
	fecha_hora DATE,
	vuelo NUMERIC(4) REFERENCES vuelo,
	cantidad NUMERIC(4) NOT NULL,
	FOREIGN KEY (compra,item) REFERENCES item,
	FOREIGN KEY (ruta,fecha_hora) REFERENCES viaje,
	PRIMARY KEY (compra,item,ruta,fecha_hora,vuelo)
);

CREATE TABLE entregado(
	compra NUMERIC(10), 
	item NUMERIC(4),
	ruta NUMERIC(4),
	fecha_hora DATE,
	centro NUMERIC(4)REFERENCES centro_distribucion,
	cantidad NUMERIC(4) NOT NULL,
	fecha_entrega DATE NOT NULL,
	FOREIGN KEY (compra,item) REFERENCES item,
	FOREIGN KEY (ruta,fecha_hora) REFERENCES viaje,
	PRIMARY KEY (compra,item,ruta,fecha_hora,centro)
);

INSERT INTO comunidad VALUES (1, 'Galicia');
INSERT INTO comunidad VALUES (2, 'Asturias');
INSERT INTO proveedor VALUES (1, 'Mon SL', 'Alcorcón');
INSERT INTO tipo_producto VALUES (1, 'Mascarilla', 'Mascarilla Estandar');
INSERT INTO tipo_producto VALUES (2, 'Respirador', 'Respirador Estandar');
INSERT INTO tipo_producto VALUES (3, 'Guantes', 'Guantes Latex');
INSERT INTO compra VALUES (1, 40000, to_date('15/04/2020', 'DD/MM/YYYY'), 1, 1);
INSERT INTO compra VALUES (2, 3000, to_date('20/04/2020', 'DD/MM/YYYY'), 1, 2);
INSERT INTO compra VALUES (3, 666, to_date('01/05/2020', 'DD/MM/YYYY'), 1, 1);
INSERT INTO item VALUES (1, 1, 1, 3333);
INSERT INTO item VALUES (1, 2, 2, 40);
INSERT INTO item VALUES (1, 3, 3, 1000);
INSERT INTO item VALUES (2, 1, 3, 5000);
INSERT INTO item VALUES (3, 1, 1, 1000);
INSERT INTO vuelo VALUES (1,to_date('21/04/2020 13:03', 'DD/MM/YYYY HH24:MI'),to_date('21/04/2020 22:07', 'DD/MM/YYYY HH24:MI'),'Isla Mauricio',1);
INSERT INTO vuelo VALUES (2,to_date('1/05/2020 13:03', 'DD/MM/YYYY HH24:MI'),to_date('1/05/2020 13:00', 'DD/MM/YYYY HH24:MI'),'Sudáfrica',1);
INSERT INTO vuelo VALUES (3,to_date('4/05/2020 1:30', 'DD/MM/YYYY HH24:MI'),to_date('4/05/2020 13:00', 'DD/MM/YYYY HH24:MI'),'Sudáfrica',1);
INSERT INTO entrega VALUES (1,to_date('22/04/2020', 'DD/MM/YYYY'), 1);
INSERT INTO entrega VALUES (2,to_date('1/05/2020', 'DD/MM/YYYY'), 2);
INSERT INTO entrega VALUES (3,to_date('4/05/2020', 'DD/MM/YYYY'), 3);
INSERT INTO entrega VALUES (4,to_date('4/05/2020', 'DD/MM/YYYY'), 3);
INSERT INTO entrega VALUES (5,to_date('3/05/2020', 'DD/MM/YYYY'), 3);
INSERT INTO entrega VALUES (6,to_date('5/05/2020', 'DD/MM/YYYY'), 3);
INSERT INTO contenido_entrega VALUES (1, 1, 1, 1111);
INSERT INTO contenido_entrega VALUES (1, 1, 2, 2222);
INSERT INTO contenido_entrega VALUES (1, 2, 1, 1);
INSERT INTO contenido_entrega VALUES (1, 2, 2, 2);
INSERT INTO contenido_entrega VALUES (1, 2, 3, 37);
INSERT INTO contenido_entrega VALUES (1, 3, 3, 1000);
INSERT INTO contenido_entrega VALUES (2, 1, 4, 5000);
INSERT INTO centro_sanitario VALUES (1, 'Conxo', 'Santiago', 1);
INSERT INTO centro_sanitario VALUES (2, 'Barbanza', 'Ribeira', 1);
INSERT INTO necesidad VALUES (1, 1, to_date('20/04/2020', 'DD/MM/YYYY'));
INSERT INTO necesidad VALUES (2, 1, to_date('21/04/2020', 'DD/MM/YYYY'));
INSERT INTO productos_necesitados VALUES (1, 1, 1, 1000);
INSERT INTO productos_necesitados VALUES (1, 1, 2, 10);
INSERT INTO productos_necesitados VALUES (2, 1, 1, 1000);
INSERT INTO productos_necesitados VALUES (2, 1, 2, 1);
INSERT INTO productos_destinados VALUES (1,1,1,1,1,1000);
INSERT INTO productos_destinados VALUES (2,1,1,1,1,111);
INSERT INTO productos_destinados VALUES (2,1,1,1,2,889);
INSERT INTO productos_destinados VALUES (1,1,1,2,1,1);
INSERT INTO productos_destinados VALUES (1,1,1,2,2,1);
INSERT INTO productos_destinados VALUES (2,1,1,2,2,1);
INSERT INTO centro_distribucion VALUES (1,'Distribuciones Paco','Santiago',1);
INSERT INTO centro_distribucion VALUES (2,'Distribuciones Pico','Padrón',1);
INSERT INTO centro_distribucion VALUES (3,'Distribuciones Chema','Boiro',1);
INSERT INTO centro_distribucion VALUES (4,'Distribuciones Juana','Gijón',2);
INSERT INTO centro_distribucion VALUES (5,'Distribuciones Elena','Oviedo',2);
INSERT INTO centro_distribucion VALUES (6,'Distribuciones Feas','Covadonga',2);
INSERT INTO ruta VALUES (1,'Santiago-Boiro');
INSERT INTO ruta VALUES (2,'Santiago-Covadonga');
INSERT INTO recorridos VALUES (1,1,1);
INSERT INTO recorridos VALUES (1,2,2);
INSERT INTO recorridos VALUES (1,3,3);
INSERT INTO recorridos VALUES (2,1,1);
INSERT INTO recorridos VALUES (2,4,2);
INSERT INTO recorridos VALUES (2,5,3);
INSERT INTO recorridos VALUES (2,6,4);
INSERT INTO conductor VALUES ('53797370W','Cotarón, Boiro',711715359);
INSERT INTO camion VALUES ('3449FZP','MAN TGL','Blanco');
INSERT INTO viaje VALUES (1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),'53797370W','3449FZP');
INSERT INTO transporta VALUES (1,1,1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),1,1111);
INSERT INTO transporta VALUES (1,2,1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),1,1);
INSERT INTO entregado VALUES (1,1,1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),1,1000,to_date('01/05/2020 10:40', 'DD/MM/YYYY HH24:MI'));
INSERT INTO entregado VALUES (1,2,1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),1,1,to_date('01/05/2020 11:00', 'DD/MM/YYYY HH24:MI'));
INSERT INTO entregado VALUES (1,2,1,to_date('01/05/2020 10:30', 'DD/MM/YYYY HH24:MI'),3,111,to_date('01/05/2020 11:25', 'DD/MM/YYYY HH24:MI'));