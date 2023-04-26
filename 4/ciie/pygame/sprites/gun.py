import pygame as pg
from settings import *
from managers.resourcemanager import ResourceManager as GR
from sprites.bullet import Bullet
from managers.soundcontroller import SoundController as SC

##########
#  Guns  #
##########

'''
Clase abstracta que implementa todo el funcionamiento básico de un arma.
'''
class AbstractGun():
    

    def __init__(self, bullet_group, mag_size, bullet_img, reload_time, rate, damage, speed, lifetime, soundFunction):
        self.bullet_group = bullet_group
        self.MAG_SIZE = mag_size
        self.current_mag = mag_size
        self.bullets = 0
        self.reload_time = reload_time
        self.bullet_img = bullet_img
        self.rate = rate
        self.speed = speed
        self.lifetime = lifetime
        self.last_shot = pg.time.get_ticks()
        self.damage = damage    # no lo usamos, pero lo dejamos por si se quiere implementar de otra forma
        self.soundFunction = soundFunction
        self.reload=False
    

    def do_reload(self):
        if not self.reload or self.current_mag != self.MAG_SIZE:
            self.reload=True
            self.reload_moment = pg.time.get_ticks()


    def cancel_reload(self):
        self.reload=False


    def shoot(self, pos, rot):
        now = pg.time.get_ticks()
        if not self.reload and self.current_mag != 0:
            if now - self.last_shot > self.rate:
                self.last_shot = now
                self.current_mag -= 1
                Bullet(self.bullet_group, pos, rot, self.bullet_img, self.speed, self.lifetime)
                self.soundFunction()
        elif self.current_mag == 0:
            if now - self.last_shot > 1000:
                self.last_shot = now
                SC.play_no_ammo()
    

    def update(self):
        if self.reload:
            if self.MAG_SIZE == self.current_mag:
                self.reload = False
                return
            now = pg.time.get_ticks()
            if now - self.reload_moment > self.reload_time:
                if self.bullets >= self.MAG_SIZE:
                    load = self.MAG_SIZE - self.current_mag%self.MAG_SIZE
                    self.bullets -= load
                    self.current_mag += load
                else:
                    bullets = self.bullets + self.current_mag
                    if bullets > self.MAG_SIZE:
                        self.bullets = bullets%self.MAG_SIZE
                        self.current_mag = self.MAG_SIZE
                    else:
                        self.bullets = 0
                        self.current_mag = bullets
                self.reload=False



class Pistol(AbstractGun):

    def __init__(self, bullet_group):
        super().__init__(bullet_group=bullet_group, mag_size=7, bullet_img=GR.BULLET_IMG, reload_time=15*FPS, rate=4*FPS, damage=34, speed=1500, lifetime=20*FPS, soundFunction=SC.play_pistola)



class Rifle(AbstractGun):

    def __init__(self, bullet_group):
        super().__init__(bullet_group=bullet_group, mag_size=30, bullet_img=GR.BULLET_IMG, reload_time=20*FPS, rate=1*FPS, damage=40, speed=1500, lifetime=5*FPS, soundFunction=SC.play_metralleta)



class MachineGun(AbstractGun):

    def __init__(self, bullet_group):
        super().__init__(bullet_group=bullet_group, mag_size=75, bullet_img=GR.BULLET_IMG, reload_time=30*FPS, rate=100, damage=30, speed=1500, lifetime=3*FPS, soundFunction=SC.play_ametralladora)
    

