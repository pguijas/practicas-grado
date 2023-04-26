import pygame as pg
from settings import *
from pygame.math import Vector2
from sprites.explosion import *
from managers.resourcemanager import ResourceManager as GR


"""
Proyectiles
"""
class Bullet(pg.sprite.Sprite):

    def __init__(self, bullet_group, pos, rot, img, speed, lifetime):
        pg.sprite.Sprite.__init__(self, bullet_group)
        # Dirección en la que dispara el jugador
        dir = Vector2(1, 0).rotate(-rot)
        # Rotamos la imagen en esta dirección
        self.image = GR.load_image(img)
        self.image = pg.transform.rotate(self.image, rot-90)
        self.rect = self.image.get_rect()
        self.rect.center = pos
        # Posicion inicial en la que se dispara
        self.pos = Vector2(pos)
        # calculamos su máscara
        self.mask = pg.mask.from_surface(self.image)
        # Velocidad 'vectorial' de la bala 
        self.vel = dir * speed
        self.lifetime = lifetime
        self.spawn_time = pg.time.get_ticks()


    def update(self, dt):
        # Avanzamos en la dirección inicial que disparamos
        self.pos += self.vel * (dt/1000)
        self.rect.center = self.pos
        if pg.time.get_ticks() - self.spawn_time > self.lifetime:
            self.kill()
    