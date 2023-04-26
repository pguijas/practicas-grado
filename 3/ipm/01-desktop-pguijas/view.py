import gettext
import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

_ = gettext.gettext

class SongRow(Gtk.ListBoxRow):
	def __init__(self,song):
		Gtk.ListBoxRow.__init__(self)
		self.url = song[1]
		hbox=Gtk.HBox()
		fav=False

		fav=song[2]=="YES"
		if fav:
			fav_text="\u2665"
		else:
			fav_text="    "
		hbox.pack_start(Gtk.Label(label=fav_text,margin=10, xalign=0), False, False, 0)
		hbox.pack_start(Gtk.Label(label=song[0],margin=10, xalign=0), False, False, 0)
		self.add(hbox)

	def get_url(self):
		return self.url


class View:

	@classmethod
	def main(Cls):
		Gtk.main()

	@classmethod
	def main_quit(cls, w, e):
		Gtk.main_quit()

	def _get_list_store_str(self, lista):
		LS = Gtk.ListStore(str)
		for item in lista:
			LS.append([item])
		return LS

	def build_view(self, valid_information, intervals, directions, notas):

		self.win = Gtk.Window(title=_("Intervalos Musicales"))
		if valid_information:
			self.box = Gtk.Box(halign= Gtk.Align.CENTER)

			#Definimos caja por ventana
			self.first_screen = Gtk.VBox(spacing=20, margin=25)
			self.second_screen = Gtk.VBox(spacing=8, margin=10)

			#1º Ventana
			#Titulo
			label_prin = Gtk.Label()
			label_prin.set_margin_top(125)
			label_prin.set_markup("<big>" + _("Seleccione el intervalo") + "</big>")
			
			#Inputs (HBOX)
			hbox1_1 = Gtk.HBox(spacing=8, margin=0, halign= Gtk.Align.CENTER)

			model_intervals = self._get_list_store_str(intervals) #model 
			self.cb1_intervals = Gtk.ComboBox(model=model_intervals, entry_text_column=0, active=0)
			renderer_text = Gtk.CellRendererText()
			self.cb1_intervals.pack_start(renderer_text, True)
			self.cb1_intervals.add_attribute(renderer_text, "text", 0)

			model_directions = self._get_list_store_str(directions) #model
			self.cb1_direction = Gtk.ComboBox(model=model_directions, entry_text_column=0, active=0)
			self.cb1_direction.pack_start(renderer_text, True)
			self.cb1_direction.add_attribute(renderer_text, "text", 0)

			self.search1_b = Gtk.Button(label=_("Buscar"))

			#Spiner
			self.spiner1=Gtk.Spinner()

			#Añadimos todo a la box de la 1º vista
			hbox1_1.pack_start(self.cb1_intervals, True, False, 10)
			hbox1_1.pack_start(self.cb1_direction, False, False, 0)
			hbox1_1.pack_start(self.search1_b, False, False, 25)
			self.first_screen.pack_start(label_prin, False, False, 0)
			self.first_screen.pack_start(hbox1_1, False, False, 0)
			self.first_screen.pack_start(self.spiner1, False, False, 0)
			
			#2º Ventana
			#Volver
			self.back_b = Gtk.Button(label="<",halign= Gtk.Align.CENTER)

			#Inputs (HBOX)
			hbox2_1 = Gtk.HBox(spacing=8, halign= Gtk.Align.CENTER)
			hbox2_1.set_margin_top(20)
			hbox2_1.set_margin_bottom(15)
			label_secnd = Gtk.Label(label=_("Seleccione el intervalo") + ":")
			self.cb2_intervals = Gtk.ComboBox(model=model_intervals, entry_text_column=0, active=0)
			self.cb2_intervals.pack_start(renderer_text, True)
			self.cb2_intervals.add_attribute(renderer_text, "text", 0)

			self.cb2_direction = Gtk.ComboBox(model=model_directions, entry_text_column=0, active=0)
			self.cb2_direction.pack_start(renderer_text, True)
			self.cb2_direction.add_attribute(renderer_text, "text", 0)

			self.search2_b= Gtk.Button(label=_("Buscar"))

			hbox2_1.pack_start(label_secnd, False, False, 0)
			hbox2_1.pack_start(self.cb2_intervals, False, False, 0)
			hbox2_1.pack_start(self.cb2_direction, False, False, 0)
			hbox2_1.pack_start(self.search2_b, False, False, 10)

			#Separator
			separator=Gtk.Separator()

			#Intervalo Ejemplo (VBOX->HBOX+Label)
			hbox2_2 = Gtk.HBox(spacing=8, margin_top = 30, halign= Gtk.Align.CENTER)
			label_ex = Gtk.Label(label=_("Ejemplo de intervalo desde") + ":")

			model_notas = self._get_list_store_str(notas) #model
			self.cb2_notes = Gtk.ComboBox(model=model_notas, entry_text_column=0, active=0)
			self.cb2_notes.pack_start(renderer_text, True)
			self.cb2_notes.add_attribute(renderer_text, "text", 0)
			hbox2_2.pack_start(label_ex, False, False, 0)
			hbox2_2.pack_start(self.cb2_notes, False, False, 0)

			hbox2_3=Gtk.HBox(halign= Gtk.Align.CENTER)
			self.label_not_ex=Gtk.Label(label=_("do - re")) #valor inicial estático (se sobreescribirá por información extraida del modelo)
			hbox2_3.pack_start(self.label_not_ex, False, False, 0)

			#Lista
			self.songs_list = Gtk.ListBox()
			self.songs_list.set_selection_mode(Gtk.SelectionMode.NONE)

			scrolleable_songs_list = Gtk.ScrolledWindow(margin_top=30)
			scrolleable_songs_list.add_with_viewport(self.songs_list)
			scrolleable_songs_list.set_min_content_height(150)

			#Spiner
			self.spiner2=Gtk.Spinner()

			#Añadimos todo a la box de la 2º vista
			self.second_screen.pack_start(hbox2_1, False, False, 0)
			self.second_screen.pack_start(separator, False, False, 0)
			self.second_screen.pack_start(hbox2_2, False, False, 0)
			self.second_screen.pack_start(hbox2_3, False, False, 0)
			self.second_screen.pack_start(scrolleable_songs_list, False, False, 0)
			self.second_screen.pack_start(self.back_b, False, False, 0)
			self.second_screen.pack_start(self.spiner2, False, False, 0)

			#Añadimos vistas a la caja principal
			self.box.pack_start(self.first_screen, False, False, 0)
			self.box.pack_start(self.second_screen, False, False, 0)
			self.win.add(self.box)

			self.win.set_default_size(480, 450)
		

	def _show_first_screen(self):
		self.second_screen.hide()
		self.first_screen.show_all()

	def _show_second_screen(self):
		self.first_screen.hide()
		self.second_screen.show_all()

	#Muestra view (1º pantalla)
	def show(self):
		self.win.show()
		self.box.show()
		self._show_first_screen()

	#Operaciones sobre la lista de canciones
	def _add_song(self, song):
		row = SongRow(song)
		self.songs_list.add(row)
		row.show()

	def _remove_songs(self):
		#Recorre el list box
		self.songs_list.foreach(self.songs_list.remove)

	def _add_songs(self, songs):
		self._remove_songs()
		for lista in songs:
			self._add_song(lista)
		self.songs_list.show_all()

	#Conects
	def connect_delete_event(self, fun):
		self.win.connect('delete-event', fun)

	def connect_interval_cb1_changed(self, fun):
		self.cb1_intervals.connect('changed', fun)

	def connect_interval_cb2_changed(self, fun):
		self.cb2_intervals.connect('changed', fun)	

	def connect_direction_cb1_changed(self, fun):
		self.cb1_direction.connect('changed', fun)

	def connect_direction_cb2_changed(self, fun):
		self.cb2_direction.connect('changed', fun)

	def connect_search1_b_clicked(self, fun):
		self.search1_b.connect('clicked', fun)

	def connect_search2_b_clicked(self, fun):
		self.search2_b.connect('clicked', fun)

	def connect_back_clicked(self, fun):
		self.back_b.connect('clicked', fun)

	def connect_notes_changed(self, fun):
		self.cb2_notes.connect('changed', fun)
	
	def connect_song_list_row_activated(self, fun):
		self.songs_list.connect("row-activated", fun)
	

	#Update Content 
	def _update_notes_example(self, lista):
		self.label_not_ex.set_label(f"{lista[0]} - {lista[1]}")
		
	def update_view(self, **kwargs):
		for name, value in kwargs.items():
			if name == 'cb1_intervals':
				self.cb1_intervals.set_active(value)
			elif name == 'cb2_intervals':
				self.cb2_intervals.set_active(value)
			elif name == 'cb1_direction':
				self.cb1_direction.set_active(value)
			elif name == 'cb2_direction':
				self.cb2_direction.set_active(value)
			elif name == 'search1_b':
				self._add_songs(value)
				self._show_second_screen()
			elif name == 'search2_b':
				self._add_songs(value)
			elif name == 'cb2_notes':
				self._update_notes_example(value)
			elif name == 'back_b':
				self._show_first_screen()
			elif name == 'spiner1':
				if value==True:
					self.spiner1.start()
				else:
					self.spiner1.stop()
			elif name == 'spiner2':
				if value==True:
					self.spiner2.start()
				else:
					self.spiner2.stop()
			elif name == 'search1_b_off':
				self.search1_b.set_sensitive(not(value))
			elif name == 'search2_b_off':
				self.search2_b.set_sensitive(not(value))
			else:
				raise TypeError(f"update_view() got an unexpected keyword argument '{name}'")

	def err_dialog(self,reason,critical):
		dialog = Gtk.MessageDialog(parent=self.win, message_type= Gtk.MessageType.ERROR, buttons= Gtk.ButtonsType.CLOSE, message_format= reason)
		dialog.run()
		#si es un error crítico, finaliza la ejecución
		if critical:
			exit()
		dialog.destroy()

