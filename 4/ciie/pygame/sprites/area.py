import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR


"""
Areas destinadas a activar a los distintos mobs.
"""
class Area(pg.sprite.Sprite):

    def __init__(self, group, x, y, w, h, number):
        pg.sprite.Sprite.__init__(self, group)
        self.rect = pg.Rect(x, y, w, h)
        self.x = x
        self.y = y
        self.rect.x = x
        self.rect.y = y
        self.number = number
        self.collided = False
    
    def get_number(self):
        return self.number
