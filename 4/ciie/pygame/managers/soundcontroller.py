import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR
from managers.user_config import UserConfig as UC


"""
Singleton destinado a gestionar el audio. Tatno musica como sonidos.
"""
class SoundController:

    music_volume = 75
    sound_volume = 75


    @classmethod
    def init(self):
        pg.mixer.pre_init(44100,-16,2, 3072)
        pg.mixer.init()
        #Cargamos config usrs
        try:
            self.music_volume = UC.get("music_volume")
        except:
            pass

        try:
            self.sound_volume = UC.get("sound_volume")
        except:
            pass


    @classmethod
    def play_menu(self):
        pg.mixer.music.load(START_MUSIC)
        self.set_music_volume(rel=0.4)
        pg.mixer.music.play(-1)


    @classmethod
    def play_main(self):
        pg.mixer.music.load(MAIN_MUSIC)
        self.set_music_volume(rel=0.2)
        pg.mixer.music.play(-1)
    

    @classmethod
    def play_gameover(self):
        pg.mixer.music.load(GAMEOVER_MUSIC)
        self.set_music_volume(rel=1)
        pg.mixer.music.play(-1)


    @classmethod
    def play_victory(self):
        pg.mixer.music.load(VICTORY_MUSIC)
        self.set_music_volume(rel=1)
        pg.mixer.music.play(-1)


    @classmethod
    def set_music_volume(self,rel=-1):
        if rel != -1:
            self.relative_music_volume = rel
        pg.mixer.music.set_volume(self.relative_music_volume*self.music_volume/100)

    #Control

    @classmethod
    def pause(self):
        pg.mixer.music.pause()


    @classmethod
    def unpause(self):
        pg.mixer.music.unpause()

    #Sounds 

    @classmethod
    def play_metralleta(self):
        sound = GR.load_sound(GR.METRALLETA)
        sound.set_volume(self.sound_volume/100)
        sound.play()
            

    @classmethod
    def play_pistola(self):
        sound = GR.load_sound(GR.PISTOLA)
        sound.set_volume(self.sound_volume/100)
        sound.play()
          

    @classmethod
    def play_ametralladora(self):
        sound = GR.load_sound(GR.AMETRALLADORA)
        sound.set_volume(self.sound_volume/100)
        sound.play()
    

    @classmethod
    def play_selection(self):
        sound = GR.load_sound(GR.SELECTION)
        sound.set_volume(self.sound_volume/100)
        sound.play()
    

    @classmethod
    def play_item(self):
        sound = GR.load_sound(GR.ITEM)
        sound.set_volume(self.sound_volume/100)
        sound.play()


    @classmethod
    def play_no_ammo(self):
        sound = GR.load_sound(GR.NO_AMMO)
        sound.set_volume(self.sound_volume*2/100)
        sound.play()

    #Volumen

    @classmethod
    def get_music_volume(self):
        return self.music_volume


    @classmethod
    def update_music_volume(self,diff):
        self.music_volume = self.music_volume + diff
        if self.music_volume<0:
            self.music_volume=0
        elif self.music_volume>100:
            self.music_volume=100
        UC.update("music_volume",self.music_volume)
        self.set_music_volume()


    @classmethod
    def get_sound_volume(self):
        return self.sound_volume


    @classmethod
    def update_sound_volume(self,diff):
        self.sound_volume = self.sound_volume + diff
        if self.sound_volume<0:
            self.sound_volume=0
        elif self.sound_volume>100:
            self.sound_volume=100
        UC.update("sound_volume",self.sound_volume)