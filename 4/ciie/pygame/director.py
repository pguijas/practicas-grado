import pygame as pg
from settings import *
from managers.user_config import UserConfig as UC

"""
Director de escena.
Encargado del flujo de escenas.
"""
class Director():

    def __init__(self):
        # Inicializamos la pantalla
        self.display = pg.display.set_mode((WIDTH, HEIGHT))
        pg.display.set_caption(TITLE)

        #Read FS config
        self.fs = False
        try:
            self.fs = UC.get("fullscreen")
        except:
            pass
        if self.fs:
            pg.display.toggle_fullscreen()

        # Inicializamos Atributos
        self.pila = []
        self.salir_escena = False
        self.music = True
        self.reloj = pg.time.Clock()

    def toogle_fullscreen(self):
        pg.display.toggle_fullscreen()
        self.fs = not self.fs
        UC.update("fullscreen",self.fs)

    def is_fullscreen(self):
        return self.fs

    def __loop(self, escena):
        self.salir_escena = False
        pg.event.clear() 
        
        while not self.salir_escena:
            dt = self.reloj.tick(60)
            # Delegamos lógicas en escena
            escena.events(pg.event.get()) #eventos gestionados por escena
            escena.update(dt)
            escena.draw(self.display)
            # Actualizamos Pantalla
            pg.display.flip()

    def run(self):
        #Ejecutamos Escenas De la Pila
        while (len(self.pila)>0):
            escena = self.pila[len(self.pila)-1]
            if self.music:
                escena.play_music()
                escena.music = False
            self.__loop(escena)


    def exitEscena(self, updateMusic = True):
        self.music = updateMusic
        self.salir_escena = True
        # Popeamos Escena
        if (len(self.pila)>0):
            self.pila.pop()
            if len(self.pila)==0: 
                self.salir_escena = True
                return

    def exitProgram(self):
        # Vaciamos pila
        self.pila = [] 
        self.salir_escena = True

    
    def changeEscena(self, escena, updateMusic = True):
        self.music = updateMusic
        self.exitEscena()
        # Ponemos la escena pasada en la cima de la pila
        self.pila.append(escena)

    def pushEscena(self, escena, updateMusic = True):
        self.music = updateMusic
        self.salir_escena = True
        self.pila.append(escena)
        