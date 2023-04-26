import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR
from escenas.escena import Escena

"""
Escena que implementa comportamientos de menú
"""
class Menu(Escena):

    def __init__(self,director, btns, back, logo=None, logoy=0):
        Escena.__init__(self, director)
        self.click = False
        self.btn_group = pg.sprite.Group(*btns)
        self.back = back
        self.logo = logo
        self.logoy= logoy


    # Dibuja Caja Centrada y Devuelve su tamaño 
    def __draw_box(self, display):
        bg = GR.load_image(GR.BOX_BG) 
        rect = bg.get_rect()
        rect.center = (WIDTH/2,HEIGHT/2)
        display.blit(bg, rect) 
        return rect.size


    # Dibuja Logo Centrado con la posibilidad de añadirle un desplazamiento
    def __draw_logo(self, display, dx=0, dy=0):
        logo = GR.load_image(self.logo)
        rect = logo.get_rect()
        rect.center = (WIDTH/2+dx, HEIGHT/2+dy)
        display.blit(logo, rect) 


    # Dibuja el fondo
    def __draw_back(self, display, dx=0, dy=0):
        logo = GR.load_image(GR.START_IMG)
        rect = logo.get_rect()
        rect.center = (WIDTH/2+dx, HEIGHT/2+dy)
        display.blit(logo, rect) 

    # Dibuja el menú
    def draw(self,display):
        if self.back:
            self.__draw_back(display, dx = 30, dy=50)

        _,box_y = self.__draw_box(display)

        if self.logo is not None:
            self.__draw_logo(display,dy=-((box_y/4))+self.logoy)

        for btn in self.btn_group:
            btn.draw(display)


    def events(self, events):
        self.click = False
        for event in events:
            if event.type == pg.QUIT:
                self.director.exitProgram()
            elif event.type == pg.MOUSEBUTTONDOWN:
                self.click = True


    def update(self, _dt):
        mouse_pos = pg.mouse.get_pos()
        self.btn_group.update(mouse_pos,self.click)


    def play_music(self):
        pass

