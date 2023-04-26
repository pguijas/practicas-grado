import webbrowser
import threading
import gi
gi.require_version('Gtk', '3.0')
from gi.repository import GLib

class Controller():
	
	def __init__(self, model, view):
		self.set_model(model)
		self.set_view(view)

	def set_model(self, model):
		self.model = model

	def set_view(self, view):
		self.view = view
		if self.model.get_error()[0]:
			#1º debemos construir la ventana padre
			view.build_view(valid_information=False, intervals = [], directions = [], notas = [])
			self.view.err_dialog(self.model.get_error()[1],True)

		else:
			intervals = self.model.get_intervals()
			directions = self.model.get_directions()
			notas = self.model.get_notas()

			#Comboboxes quedarán con el 1º elemento seleccionado(igual que el modelo)
			view.build_view(valid_information=True, intervals=intervals, directions = directions, notas = notas)
		
			#Connects
			view.connect_delete_event(self.view.main_quit)
			view.connect_interval_cb1_changed(self.on_intervals_changed)
			view.connect_interval_cb2_changed(self.on_intervals_changed)
			view.connect_direction_cb1_changed(self.on_direction_changed)
			view.connect_direction_cb2_changed(self.on_direction_changed)
			view.connect_search1_b_clicked(self.on_search1_b_clicked)
			view.connect_search2_b_clicked(self.on_search2_b_clicked)
			view.connect_back_clicked(self.on_back_clicked)
			view.connect_notes_changed(self.on_notes_changed)
			view.connect_song_list_row_activated(self.on_row_activated)

	def main(self):
		#Si falló el modelo, no podrá ejecutarse la vista
		if not(self.model.get_error()[0]):
			self.view.show()
		self.view.main()

	def on_row_activated(self, listbox, row):
		webbrowser.open(row.get_url())
	 
	def on_intervals_changed(self, combobox):
		interval = combobox.get_active()
		self.model.set_interval_actual_index(interval)

	def on_direction_changed(self, combobox):
		direction = combobox.get_active()
		self.model.set_direction_index(direction)

	#Acceso al Servidor
	def on_search1_b_clicked(self, w):
		self.view.update_view(spiner1=True,search1_b_off=True)
		threading.Thread(target=self._on_search1_b,daemon=True).start()

	def _on_search1_b(self):
		i_index = self.model.get_actual_interval_index()
		ad_index = self.model.get_actual_direction_index()
		GLib.idle_add(self._change_notes) 
		songs = self.model.songs()
		if self.model.get_error()[0]==True:
			GLib.idle_add(self._update,1,True,0,0,[])
			GLib.idle_add(self.view.err_dialog,self.model.get_error()[1],False)
		else:
			#Actualizamos comboboxes de la pantalla principal y metemos las canciones
			GLib.idle_add(self._update, 1, False, i_index, ad_index, songs)

	#Acceso al Servidor
	def on_search2_b_clicked(self, w):
		self.view.update_view(spiner2=True,search2_b_off=True)
		threading.Thread(target=self._on_search2_b,daemon=True).start()

	def _on_search2_b(self):
		i_index = self.model.get_actual_interval_index()
		ad_index = self.model.get_actual_direction_index()
		GLib.idle_add(self._change_notes)
		songs = self.model.songs()
		if self.model.get_error()[0]==True:
			GLib.idle_add(self._update,2,True,0,0,[])
			GLib.idle_add(self.view.err_dialog,self.model.get_error()[1],False)
		else:
			#Actualizamos comboboxes de la pantalla de bienvenida y metemos las canciones
			GLib.idle_add(self._update, 2, False, i_index, ad_index, songs)

	#Creamos esta función dado que idle_add pasa los parámetros por orden y no por nombre
	def _update(self, screen, error, i_index,ad_index, songs):
		if error:
			#Si hay un error activamos los botones para que lo pueda seguir intentando
			if screen==1:
				self.view.update_view(search1_b_off=False,spiner1=False)
			elif screen==2:
				self.view.update_view(search2_b_off=False,spiner2=False)
		else:
			if screen==1:
				self.view.update_view(cb2_intervals = i_index, cb2_direction= ad_index, search1_b = songs, spiner1=False, search1_b_off=False)
			elif screen==2:
				self.view.update_view(cb1_intervals = i_index, cb1_direction= ad_index, search2_b = songs, spiner2=False,search2_b_off=False)
			
	def on_back_clicked(self, w):
		self.view.update_view(back_b = True) #metemos un true x meter algo para que sea una clave y un valor
		
	def on_notes_changed(self, combobox):
		n = combobox.get_active()
		self.model.set_nota_actual_index(n)
		self._change_notes()

	# Aquí ya tendrríamos la información de los intervalos guardada por lo que si se recuperó bien esa 
	# información no tendría por que fallar
	def _change_notes(self):
		nota1 = self.model.get_nota_actual()
		nota2 = self.model.aplicar_intervalo()
		self.view.update_view(cb2_notes = [nota1, nota2])
