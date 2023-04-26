from settings import *
from escenas.gui.buttons import ClasicButton
from managers.resourcemanager import ResourceManager as GR
from managers.soundcontroller import SoundController as SC
from escenas.menu import Menu

class Win(Menu):

    def __init__(self, director):
        button = ClasicButton("Menu Principal", self.__back, dy = 80)

        Menu.__init__(self, director, [button], False, logo=GR.VICTORY, logoy=60)

    def play_music(self):
        SC.play_victory()

    #Callback
    def __back(self):
        SC.play_selection()
        self.director.exitEscena()
        



    
