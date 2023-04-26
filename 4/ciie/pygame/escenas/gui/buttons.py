import pygame as pg
import sys
from settings import *
from managers.resourcemanager import ResourceManager as GR
from escenas.gui.button import Button

#######################################
#    Direferentes tipos de botones    #
#######################################

#Botón Clásico
class ClasicButton(Button):
    def __init__(self, text, callback, dx=0, dy=0):
        Button.__init__(self,GR.BTN_BG, text, callback, dx=dx, dy=dy)


#Botón de nivel (puede estar bloqueado y es cuadrado)
class LevelButton(Button):
    def __init__(self, lvl, callback, locked=False, dx=0, dy=0):
        level=str(lvl)
        if locked:
            level = "x"
            callback = lambda a : a

        Button.__init__(self,GR.LVL_BTN, level, callback, dx=dx, dy=dy)
        self.lvl=lvl
        
    def update(self, mouse_pos, click):
        self.hover = self.rect.collidepoint(mouse_pos)
        if self.hover and click:
            self.callback(self.lvl)
        

#Botón Toggle (ON/OFF)
class ToogleButton(Button):
    def __init__(self, state, callback1, dx=0, dy=0):
        Button.__init__(self,GR.BTN_BG, "ON", callback1, dx=dx, dy=dy)
        
        self.state = state

        # Text 2
        font = GR.load_font(GR.MAIN_FONT,GUI_FONT_SIZE)
        self.text2_surface = font.render("OFF", True, (255,255,255))
        self.text2_surface_hover = font.render("OFF", True, (154,122,37))
        
    def draw(self, display):
        display.blit(self.image, self.rect) 
        text = self.text_surface
        if self.hover:
            text = self.text_surface_hover

        if not self.state:
            text = self.text2_surface
            if self.hover:
                text = self.text2_surface_hover

        display.blit(text,self.text_rect)   
    

    def update(self, mouse_pos, click):
        self.hover = self.rect.collidepoint(mouse_pos)
        if self.hover and click:
            self.state = not self.state
            self.callback()
