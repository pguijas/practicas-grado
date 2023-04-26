import pygame as pg
import sys
from settings import *
from managers.resourcemanager import ResourceManager as GR
from utils.observer import Observer

class Hud(Observer):
    #Inicializamos el botón mostrándolo en pantalla
    def __init__(self):
        self.gun_number = -1
        self.health= PLAYER_HEALTH
        self.ammo = 0
        self.totalammo = 0

        #Load BG
        self.image = GR.load_image(GR.HUD)
        self.rect = self.image.get_rect() 
        self.rect.bottomleft = (0,HEIGHT)

        self.__load_health()
        self.__load_ammo()
        self.__load_gun()

    #Health
    def __load_health(self):
        font = GR.load_font(GR.MAIN_FONT,19)
        self.health_surface = font.render(str(self.health)+"%", True, (255,255,255))
        self.health_rect = self.health_surface.get_rect()
        self.health_rect.center = (237,HEIGHT-38)
    
    #Ammo
    def __load_ammo(self):
        font = GR.load_font(GR.MAIN_FONT,16) #
        ammo = str(self.ammo)
        if self.ammo == -1:
            ammo = "--"
        self.ammo_surface = font.render(ammo+"/"+str(self.totalammo), True, (255,255,255))
        self.ammo_rect = self.ammo_surface.get_rect()
        self.ammo_rect.center = (90,HEIGHT-123)

    #Gun Icon
    def __load_gun(self):
        if self.gun_number == 0:
            self.gun = GR.load_image(GR.PISTOLHUD)
        elif self.gun_number == 1:
            self.gun = GR.load_image(GR.SMGHUD)
        elif self.gun_number == 2:
            self.gun = GR.load_image(GR.MGHUD)
        elif self.gun_number == -1:
            self.gun = pg.Surface((0,0))

        self.gun_rect = self.gun.get_rect() 
        self.gun_rect.center = (80,HEIGHT-60)

    def update(self,tipo,cambio):
        if tipo == "health":
            if self.health != cambio:
                self.health = cambio
                self.__load_health()

        elif tipo == "gun":
            if self.gun_number != cambio:
                self.gun_number = cambio
                self.__load_gun()

        elif tipo == "ammo":
            if self.ammo != cambio:
                self.ammo = cambio
                self.__load_ammo()
        
        elif tipo == "bullets":
            if self.totalammo != cambio:
                self.totalammo = cambio
                self.__load_ammo()

    def draw(self, display):
        display.blit(self.image, self.rect) 
        display.blit(self.health_surface, self.health_rect) 
        display.blit(self.ammo_surface, self.ammo_rect) 
        display.blit(self.gun, self.gun_rect) 
    
