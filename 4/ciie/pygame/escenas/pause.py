import pygame as pg
from settings import *
from escenas.gui.buttons import ClasicButton
from managers.resourcemanager import ResourceManager as GR
from managers.soundcontroller import SoundController as SC
from escenas.menu import Menu
import escenas.partida


"""
Menú de Pausa
"""
class Pause(Menu):

    def __init__(self, director, lvl):
        self.lvl = lvl
        continue_btn = ClasicButton("Continuar", self.__go_continue, dy = 5)
        margin = continue_btn.get_size()[1]/2
        retry_btn = ClasicButton("Reintentar", self.__go_retry, dy = margin*3.22)
        exit_btn = ClasicButton("Menu Principal", self.__go_exit, dy=margin*6)

        Menu.__init__(self, director, [continue_btn, retry_btn, exit_btn], False, logo=GR.PAUSE_IMG)


    def events(self, events):
        self.click = False
        for event in events:
            if event.type == pg.QUIT:
                SC.play_selection()
            #Pulsaciones Teclas
            elif event.type == pg.KEYDOWN:
                if event.key == pg.K_ESCAPE or event.key == pg.K_p:
                    self.__go_continue()
            elif event.type == pg.MOUSEBUTTONDOWN:
                self.click = True
    

    def play_music(self):
        SC.play_gameover()


    ###############
    #  Callbacks  #
    ###############

    def __go_continue(self):
        self.director.exitEscena()
    

    def __go_retry(self):
        SC.play_selection()
        partida = escenas.partida.Partida(self.director,self.lvl)
        self.director.exitEscena()
        self.director.exitEscena()
        self.director.pushEscena(partida)
        

    def __go_exit(self):
        SC.play_selection()
        self.director.exitEscena()
        self.director.exitEscena()