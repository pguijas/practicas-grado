import pygame as pg
from sprites.character import Character
from settings import *
from math import pow, sqrt
from pygame.math import Vector2
from sprites.gun import Pistol, Rifle
from managers.resourcemanager import ResourceManager as GR


############
#   Mobs   #
############


class Mob(Character):

    def __init__(self, mob_group, x, y, image, gun, collide_groups, row, area, numImagenes, positions):
        super().__init__(mob_group, image, MOB_HIT_RECT.copy(), x, y, MOB_HEALTH, collide_groups, positions, row, numImagenes)
        self.acc = Vector2(0, 0)
        self.gun = gun
        self.gun.bullets = self.gun.MAG_SIZE*10
        self.gun.current_mag = self.gun.MAG_SIZE
        self.last_shot = pg.time.get_ticks()
        self.reloading = False
        self.dead = False
        self.area = area
        self.activated = False
        self.last_change = pg.time.get_ticks()


    # Cambiamos a animacion de muerte
    def die(self):
        if not self.dead:
            self.numPostura = 1
            self.numImagenPostura = 0
            self.dead = True


    # El Mob nos empieza a perseguir
    def activate(self, area):
        if self.area == area:
            self.moving = True   


    def update(self):
        # Comportamiento de nuestra IA de Mobs, disparar siempre y cuando
        # no este muerto
        if not self.dead:
            if not self.reloading and self.gun.current_mag == 0:
                self.gun.do_reload()
                self.reloading = True
            elif self.moving and self.gun.current_mag != 0:
                self.reloading = False
                self.gun.shoot(self.pos, self.rot)
            self.gun.update()
        super().update()


# Mob con pistola
class MobBasico(Mob):

    def __init__(self, mob_group, x, y, bullets, collide_groups, area):
        gun = Pistol(bullets)
        # Pasamos hoja de Sprites y demás atributos de este mob
        super().__init__(mob_group, x, y, GR.GUNNER, gun, collide_groups, 2, area, [8, 4], GR.GUNNER_POSITIONS)


    # Comportamiento del Mob con pistola
    def update(self, player_pos, dt):
        if not self.dead:
            distance = sqrt(pow(player_pos.x - self.pos.x, 2) + pow(player_pos.x - self.pos.x, 2))
            if not self.moving:
                self.numImagenPostura = 0
                return
            if pg.time.get_ticks() - self.last_change > ANIM_DELAY:
                self.numImagenPostura = (self.numImagenPostura + 1)%8
                self.last_change = pg.time.get_ticks()
            self.moving = True
            self.rot = (player_pos - self.pos).angle_to(Vector2(1, 0))
            if distance > MOB_ATTK_DISTANCE/2:
                self.acc = Vector2(MOB_SPEED, 0).rotate(-self.rot)
                self.acc += self.vel * -1
                self.vel += self.acc * (dt/1000)
                self.pos += self.vel * (dt/1000) + 0.5 * self.acc * (dt/1000) ** 2
        elif (self.numImagenPostura < 3) and (pg.time.get_ticks() - self.last_change > ANIM_DELAY):
            self.last_change = pg.time.get_ticks()
            self.numImagenPostura += 1
        super().update()


# Mob con rifle
class MobRiffle(Mob):

    def __init__(self, mob_group, x, y, bullets, collide_groups, area):
        gun = Rifle(bullets)
        # Pasamos hoja de Sprites y demás atributos de este mob
        super().__init__(mob_group, x, y, GR.MOB, gun, collide_groups, 2, area, [8, 4], GR.MOB_POSITIONS)


    # Comportamiento del Mob con Rifle
    def update(self, player_pos, dt):
        if not self.dead:
            distance = sqrt(pow(player_pos.x - self.pos.x, 2) + pow(player_pos.x - self.pos.x, 2))
            if not self.moving:
                self.numImagenPostura = 0
                return
            if pg.time.get_ticks() - self.last_change > ANIM_DELAY:
                self.numImagenPostura = (self.numImagenPostura + 1)%8
                self.last_change = pg.time.get_ticks()
            self.moving = True
            self.rot = (player_pos - self.pos).angle_to(Vector2(1, 0))
            if distance > MOB_ATTK_DISTANCE/2:
                self.acc = Vector2(MOB_SPEED, 0).rotate(-self.rot)
                self.acc += self.vel * -1
                self.vel += self.acc * (dt/1000)
                self.pos += self.vel * (dt/1000) + 0.5 * self.acc * (dt/1000) ** 2
        elif (self.numImagenPostura < 3) and (pg.time.get_ticks() - self.last_change > ANIM_DELAY):
            self.last_change = pg.time.get_ticks()
            self.numImagenPostura += 1
        super().update()