import pygame as pg
from settings import *
import random
from pygame.math import Vector2
from managers.resourcemanager import ResourceManager as GR


"""
Sangre. Se creará cuando una bala colisione en un personaje, durará HIT_LIFETIME
"""
class Hit(pg.sprite.Sprite):

    def __init__(self, hit_group, pos, sx, sy, rot) -> None:
        pg.sprite.Sprite.__init__(self, hit_group)
        pos = pos + Vector2(15,0).rotate(-rot)
        self.image = GR.load_image(GR.HIT_IMAGE)
        self.image = pg.transform.rotate(self.image, random.randrange(0,360)+rot)
        self.image = pg.transform.scale(self.image, (sx * SPRITE_BOX, sy * SPRITE_BOX))
        self.pos = Vector2(pos)
        self.rect = self.image.get_rect()
        self.rect.center = self.pos
        self.spawn_time = pg.time.get_ticks()


    def update(self):
        if pg.time.get_ticks() - self.spawn_time > HIT_LIFETIME:
            self.kill()