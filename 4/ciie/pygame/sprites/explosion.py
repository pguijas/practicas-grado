import pygame as pg
from settings import *
from pygame.math import Vector2
from managers.resourcemanager import ResourceManager as GR


"""
Explosión. Se creará cuando una bala colisione contra un objeto, durará HIT_LIFETIME
"""
class Explosion(pg.sprite.Sprite):


    def __init__(self, explosion_group, pos, sx, sy) -> None:
        pg.sprite.Sprite.__init__(self, explosion_group)
        self.image = GR.load_image(GR.EXPLOSION_IMAGE)
        self.image = pg.transform.scale(self.image, (sx * SPRITE_BOX, sy * SPRITE_BOX))
        self.mask = pg.mask.from_surface(self.image)
        self.rect = self.image.get_rect()
        self.rect.center = pos
        self.pos = Vector2(pos)
        self.spawn_time = pg.time.get_ticks()


    def update(self):
        if pg.time.get_ticks() - self.spawn_time > EXPLOSION_LIFETIME:
            self.kill()