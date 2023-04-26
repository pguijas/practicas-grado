import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR


"""
Cajas de Munición
"""
class Ammo(pg.sprite.Sprite):

    def __init__(self, item_group, x, y):
        pg.sprite.Sprite.__init__(self, item_group)
        self.image = pg.transform.scale(GR.load_image(GR.AMMO_IMAGE), (0.65*SPRITE_BOX, 0.65*SPRITE_BOX))
        self.rect = self.image.get_rect()
        self.rect.center = (x, y)
