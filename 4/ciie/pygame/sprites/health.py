import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR


"""
Cajas de Vida
"""
class HP(pg.sprite.Sprite):

    def __init__(self, item_group, x, y):
        pg.sprite.Sprite.__init__(self, item_group)
        self.image = pg.transform.scale(GR.load_image(GR.HP_IMAGE), (0.65*SPRITE_BOX, 0.5*SPRITE_BOX))
        self.rect = self.image.get_rect()
        self.rect.center = (x, y)