import pygame as pg
import sys
from settings import *
from managers.resourcemanager import ResourceManager as GR

class Button(pg.sprite.Sprite):

    #Inicializamos componentes que formarán Button
    def __init__(self, bg, text, callback, dx=0, dy=0):
        pg.sprite.Sprite.__init__(self)

        self.callback = callback

        #Bg Btn
        self.image = GR.load_image(bg)
        self.rect = self.image.get_rect()
        self.rect.center = (WIDTH/2+dx,HEIGHT/2+dy)
        
        #Hovered
        self.hover = False

        #Text
        font = GR.load_font(GR.MAIN_FONT,GUI_FONT_SIZE)
    
        self.text_surface = font.render(text, True, (255,255,255))
        self.text_surface_hover = font.render(text, True, (154,122,37))

        self.text_rect = self.text_surface.get_rect()
        self.text_rect.center = (WIDTH/2+dx,HEIGHT/2+dy)
        

    def update(self, mouse_pos, click):
        self.hover = self.rect.collidepoint(mouse_pos)
        if self.hover and click:
            self.callback()
        
    def draw(self, display):
        display.blit(self.image, self.rect) 
        text = self.text_surface
        if self.hover:
            text = self.text_surface_hover

        display.blit(text,self.text_rect)   

    def get_size(self):
        return self.rect.size
