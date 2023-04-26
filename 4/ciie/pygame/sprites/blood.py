import pygame as pg
from settings import *
from pygame.math import Vector2
from managers.resourcemanager import ResourceManager as GR


"""
Sangre que aparecerá al asesinal a algún personaje.
"""
class Blood(pg.sprite.Sprite):

    def __init__(self, blood_group, pos, sx, sy, rot) -> None:
        pg.sprite.Sprite.__init__(self, blood_group)
        self.pos = pos
        self.image = GR.load_image(GR.BLOOD_IMAGE)
        self.image = pg.transform.scale(self.image, (sx * SPRITE_BOX, sy * SPRITE_BOX))
        self.image = pg.transform.rotate(self.image, rot-90)
        self.rect = self.image.get_rect()
        self.mask = pg.mask.from_surface(self.image)
        self.pos = Vector2(pos)
        self.rect.center = pos