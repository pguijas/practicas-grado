#!/usr/bin/env python3

import sys

from p1 import e2e
import textwrap


""" 
Este test está formado por tres partes. En la primera parte se comprueba que la interfaz contenga todos los elementos
que se establecieron en el wireframe diseñado. En la segunda se comprueba que la interfaz funciona perfectamente si 
buscásemos un intervalo de 3M ascendente. Como en esta segunda parte estamos comprobando la búsqueda en la primera 
pantalla, es decir, se usan los elementos de la primera pantalla para buscar dicho intervalo, nos queda por comprobar
que los elementos de la segunda pantalla vayan bien. Para ello creamos la tercera parte que busca el intervalo 4j 
descendente usando los elementos de la segunda pantalla.
"""

def show_passed():
	print('\033[92m', "    Passed", '\033[0m')

def show_not_passed(e):
	print('\033[91m', "    Not passed", '\033[0m')
	print(textwrap.indent(str(e), "    "))

#Devuelve una lista con los objetos que tengan dicho rol y/o nombre
def get_objects(parent, role=None, name=None):
	def _check(obj):
		return (role is None or obj.get_role_name() == role) and (name is None or obj.get_name() == name)
	return [obj for _, obj in e2e.tree(parent) if _check(obj)]


def dump_desktop():
	desktop = e2e.Atspi.get_desktop(0)
	for _, app in e2e.children(desktop):
		print(app.get_name())

		
def dump_app(name):
	desktop = e2e.Atspi.get_desktop(0)
	app = next((app for _, app in e2e.children(desktop) if app.get_name() == name), None)
	if not app:
		print(f"App {name} not found in desktop")
		sys.exit(0)
	for path, node in e2e.tree(app):
		try:
			n = node.get_n_actions()
		except:
			n = 0
		actions = [node.get_action_name(i) for i in range(0, n)]
		if actions:
			actions_s = f" actions: {actions}"
		else:
			actions_s = ""
		print("  "*len(path), f"{path} {node.get_role_name()}({node.get_name()}) {actions_s}",
			  sep= "")



def lanzo_aplicacion(ctx):
	process, app = e2e.run(ctx.path)
	assert app is not None
	return e2e.Ctx(path= ctx.path, process= process, app= app)


def veo_frame_ventana(app):
	frame = e2e.get_obj(parent= app, role="frame", name="Intervalos Musicales")
	assert frame and frame.get_name()=="Intervalos Musicales"

def veo_label_principal(app):
	label = e2e.get_obj(parent= app, role="label", name="Seleccione el intervalo")
	assert label and label.get_name()=="Seleccione el intervalo"


def compruebo_contenido_combobox(cb, name_cb, lista):
	assert cb and cb.get_name()==name_cb
	menu = e2e.get_obj(parent=cb, role="menu")
	assert menu and menu.get_child_count()==len(lista)
	items = e2e.children(menu)
	while(True):
		item=next(items, None)
		if item is not None:
			assert item[1].get_name() == lista[item[0]] #Comprobamos que estén y que estén en orden
		else: 
			break

def veo_comboboxes_intervalos(app):
	comboboxes = get_objects(parent= app, role="combo box", name="2M")
	num = len(comboboxes)
	assert comboboxes is not None and num==2
	lista= ["2M","2m", "3M", "3m", "4aum", "4j", "5j", "6M", "6m", "7M", "7m", "8a"]
	for i in range(0, num):
		compruebo_contenido_combobox(cb=comboboxes[i], name_cb="2M", lista=lista)


def veo_comboboxes_direcciones(app):
	comboboxes = get_objects(parent= app, role="combo box", name="Ascendente")
	num = len(comboboxes)
	assert comboboxes and num==2
	lista=["Ascendente", "Descedente"]
	for i in range(0, num):
	   compruebo_contenido_combobox(cb=comboboxes[i], name_cb="Ascendente", lista=lista)

def veo_botones_buscar(app):
	botones = get_objects(parent= app, role="push button", name="Buscar")
	num = len(botones)
	assert botones and num==2

def veo_boton_retroceder(app):
	boton = get_objects(parent= app, role="push button", name="<")
	num = len(boton)
	assert boton and num==1

def veo_combobox_notas(app):
	combobox_list = get_objects(parent= app, role="combo box", name="do")
	num=len(combobox_list)
	assert combobox_list and num==1
	lista= ["do", "do♯/re♭", "re", "re♯/mi♭", "mi", "fa", "fa♯/sol♭", "sol", "sol♯/la♭", "la", "la♯/si♭", "si"]
	compruebo_contenido_combobox(cb=combobox_list[0], name_cb="do", lista=lista)

def veo_label_ejemplo_desde(app):
	label = e2e.get_obj(parent= app, role="label", name="Ejemplo de intervalo desde:")
	assert label and label.get_name()=="Ejemplo de intervalo desde:"

def veo_label_ejemplo(app):
	label = e2e.get_obj(parent= app, role="label", name="do - re")
	assert label and label.get_name()=="do - re"

def veo_lista_canciones(app):
	scroll = e2e.get_obj(parent=app, role="scroll pane")
	assert scroll is not None
	view_port = e2e.get_obj(parent=scroll, role="viewport")
	assert view_port is not None
	list_box = e2e.get_obj(parent=view_port, role="list box")
	assert list_box is not None



def test_contenido_elementos(ctx):

	e2e.show("______________________________")
	e2e.show("TEST COMPROBACIÓN ELEMENTOS")
	e2e.show("______________________________")

	e2e.show("""
		GIVEN he lanzado la aplicación
		THEN la ventana tiene el nombre de Intervalos Musicales
		""")
	try:
		ctx = lanzo_aplicacion(ctx)
		veo_frame_ventana(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN aparece label Seleccione el intervalo
	""")
	try:
		veo_label_principal(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN comprobamos si existen los comboboxes de intervalos
	AND comprobamos que estén todos los intervalos y que estén ordenados
	""")
	try:
		veo_comboboxes_intervalos(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN comprobamos si existen los comboboxes de direcciones
	AND comprobamos que estén todas las direcciones y que estén ordenadas
	""")
	try:
		veo_comboboxes_direcciones(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN comprobamos si existen los botones de buscar
	""")
	try:
		veo_botones_buscar(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN comprobamos si existe el botón de retroceder
	""")
	try:
		veo_boton_retroceder(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN aparece label Ejemplo de intervalo desde
	""")
	try:
		veo_label_ejemplo_desde(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN aparece label con el ejemplo
	""")
	try:
		veo_label_ejemplo(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)


	e2e.show("""
	THEN comprobamos si existe el combobox de notas
	""")
	try:
		veo_combobox_notas(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
	THEN comprobamos si existe lista scrolleable de canciones
	""")
	try:
		veo_lista_canciones(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.stop(ctx.process)

#Selecciona el item del menu del combobox
def selecciono_intervalo_cb(cb, name_item):
	e2e.do_action(cb, 'press')
	menu = e2e.get_obj(parent=cb, role="menu")
	item = e2e.get_obj(parent=menu, role="menu item", name=name_item)
	e2e.do_action(item, 'click')


#Comprueba si se actualiza la lista de canciones
def veo_canciones(parent, lista):
	list_box = e2e.get_obj(parent=parent, role="list box")
	num_songs = len(lista)
	#Comprobamos que existan todas las canciones 
	for i in range(0, num_songs):
		song = e2e.get_obj(parent=list_box, role="label", name=lista[i])
		assert song and song.get_name()==lista[i]
	#Comprobamos que no haya ninguna canción favorita (se cumple en las dos búsquedas) -> tiene que haber label vacía por canción
	labels_fav = get_objects(parent=list_box, role="label", name="    ")
	assert len(labels_fav) == num_songs
	

def selecciono_3M(app):
	cb = e2e.get_obj(parent= app, role="combo box", name="2M")
	selecciono_intervalo_cb(cb=cb, name_item="3M")
	assert cb and cb.get_name()=="3M"


def selecciono_ascendente(app):
	cb = e2e.get_obj(parent= app, role="combo box", name="Ascendente")
	selecciono_intervalo_cb(cb=cb, name_item="Ascendente")
	assert cb and cb.get_name()=="Ascendente"


def busco_intervalo(app):
	button = e2e.get_obj(parent= app, role="push button", name="Buscar")
	e2e.do_action(button, 'click')


def veo_ejemplo_intervalo(app):
	label = e2e.get_obj(parent= app, role="label", name="do - mi")
	assert label and label.get_name()=="do - mi"

def veo_canciones_3M(app):
	songs=["La primavera (Vivaldi)", "Oh when the saints go marching in", "Blister in the sun (Violent Femmes)",
	"Blue Danube", "Ob-la-di Ob-la-da (The Beatles)", "Kumbaya", "Sweet Child O'Mine - Bass riff intro (Guns N' Roses)"]
	veo_canciones(app, songs)
	

def selecciono_ejemplo_fa(app):
	cb = e2e.get_obj(parent= app, role="combo box", name="do")
	selecciono_intervalo_cb(cb=cb, name_item="fa")


def veo_ejemplo_desde_fa(app):
	label = e2e.get_obj(parent= app, role="label", name="fa - la")
	assert label and label.get_name()=="fa - la"



def test_funcionamiento_3M(ctx):

	e2e.show("________________________________________________")
	e2e.show(" TEST FUNCIONAMIENTO 3M SOBRE PRIMERA PANTALLA")
	e2e.show("________________________________________________")

	e2e.show("""
		GIVEN he lanzado la aplicación
		WHEN selecciono 3M
		THEN veo seleccionado 3M
		""")
	try:
		ctx = lanzo_aplicacion(ctx)
		selecciono_3M(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)


	e2e.show("""
		WHEN selecciono Ascendente
		THEN veo seleccionado Ascendente
		""")
	try:
		selecciono_ascendente(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
		WHEN busco el intervalo
		THEN veo el ejemplo del intervalo
		THEN veo las canciones representativas del intervalo
		""")
	try:
		busco_intervalo(ctx.app)
		veo_canciones_3M(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
		WHEN selecciono ejemplo desde fa
		THEN veo el intervalo a partir de fa
		""")
	try:
		selecciono_ejemplo_fa(ctx.app)
		veo_ejemplo_desde_fa(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)
	
	#No matamos el proceso porque el test de 4j se va a realizar sobre el test 3M
	test_funcionamiento_4j(ctx)


def selecciono_4j(app):
	#3M porque está modificado por la anterior búsqueda
	comboboxes = get_objects(parent= app, role="combo box", name="3M")
	cb = comboboxes[1]
	selecciono_intervalo_cb(cb=cb, name_item="4j")
	assert cb.get_name() =="4j" 

	
def selecciono_descendente(app):
	comboboxes = get_objects(parent= app, role="combo box", name="Ascendente")
	cb = comboboxes[1]
	selecciono_intervalo_cb(cb=cb, name_item="Descedente")
	assert cb.get_name() == "Descedente"

def busco_intervalo_boton2(app):
	botones = get_objects(parent= app, role="push button", name="Buscar")
	boton = botones[1]
	e2e.do_action(boton, 'click')

def veo_ejemplo_4j(app):
	#desde fa porque se modificó en la prueba anterior
	label = e2e.get_obj(parent= app, role="label", name="fa - do")
	assert label and label.get_name()=="fa - do"

def veo_canciones_4j(app):
	songs=["Adeste fideles", "Born Free (B.S.O.)"]
	veo_canciones(parent= app, lista=songs)
	


def test_funcionamiento_4j(ctx):

	e2e.show("________________________________________________")
	e2e.show(" TEST FUNCIONAMIENTO 4J SOBRE SEGUNDA PANTALLA")
	e2e.show("________________________________________________")

	e2e.show("""
		WHEN selecciono 4j
		THEN veo seleccionado 4j
		""")
	try:
		selecciono_4j(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
		WHEN selecciono descendente
		THEN veo seleccionado descendente
		""")
	try:
		selecciono_descendente(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)

	e2e.show("""
		WHEN busco el intervalo
		THEN veo el ejemplo del intervalo desde fa (seleccionado previamente)
		THEN veo las canciones representativas del intervalo
		""")
	try:
		
		busco_intervalo_boton2(ctx.app)
		veo_ejemplo_4j(ctx.app)
		veo_canciones_4j(ctx.app)
		show_passed()
	except Exception as e:
		show_not_passed(e)


	e2e.stop(ctx.process)

		
if __name__ == '__main__':
	
	if len(sys.argv) == 1:
		print("\033[0;31mError: Execute this test with the executable PATH. In this case execute this: './test-p1.py ./ipm-p1.py'.\033[00m")
	else:
		path = sys.argv[1]
		initial_ctx = e2e.Ctx(path = path, process = None, app = None)

		test_contenido_elementos(initial_ctx)
		test_funcionamiento_3M(initial_ctx)

		


	  
