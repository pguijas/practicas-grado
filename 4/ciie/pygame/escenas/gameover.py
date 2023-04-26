from settings import *
from escenas.gui.buttons import ClasicButton
from managers.resourcemanager import ResourceManager as GR
from managers.soundcontroller import SoundController as SC
from escenas.menu import Menu
import escenas.partida


"""
Menú de Derrota
"""
class GameOver(Menu):

    def __init__(self, director, lvl):
        self.lvl = lvl
        retry_btn = ClasicButton("Reintentar", self.__go_retry, dy = 35)
        margin = retry_btn.get_size()[1]/2
        exit_btn = ClasicButton("Menu Principal", self.__go_exit, dy=margin*4.5)
        
        Menu.__init__(self, director, [retry_btn, exit_btn], False, logo=GR.GAMEOVER_IMG)

    def play_music(self):
        SC.play_gameover()

    ###############
    #  Callbacks  #
    ###############

    def __go_retry(self):
        SC.play_selection()
        partida = escenas.partida.Partida(self.director,self.lvl,dialog=False)
        self.director.changeEscena(partida)


    def __go_exit(self):
        SC.play_selection()
        self.director.exitEscena()