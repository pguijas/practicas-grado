from matplotlib.style import available
import pygame as pg
from sprites.character import Character
from pygame.math import Vector2
from settings import *
from math import cos, pi
from control import Controler
from sprites.gun import MachineGun, Pistol, Rifle
from managers.resourcemanager import ResourceManager as GR
from utils.observable import Observable


class Player(Character, Observable):

    def __init__(self, x, y, bullets, collide_groups, observers, level):
        Character.__init__(self, None, GR.PLAYER, PLAYER_HIT_RECT, x, y, PLAYER_HEALTH, collide_groups, GR.HERO_POSITIONS, 5, [8, 8, 8, 8, 3])
        Observable.__init__(self, observers)
        self.last_shot = 0
        pg.mouse.set_pos((x+10) * SPRITE_BOX, y * SPRITE_BOX)
        self.mouse = pg.mouse.get_pos()
        self.controler = Controler()
        self.guns = [Pistol(bullets), Rifle(bullets), MachineGun(bullets)][0:level]
        self.gunSelector = 0
        self.shooting = False
        self.reloading = False
        self.last_change = pg.time.get_ticks()
        #Notificamos a observadores inicialización
        self.notify("health", self.health)
        if self.guns != []:
            self.notify("gun", self.gunSelector)
            self.notify("ammo", self.guns[self.gunSelector].current_mag)
            self.notify("bullets", self.guns[self.gunSelector].bullets)


    # Acciones según la configuración del controlador
    def __callControler(self):
        
        if self.health <= 0 :
            if (self.numImagenPostura < 2) and (pg.time.get_ticks() - self.last_change > ANIM_DELAY*4):
                self.numImagenPostura += 1
            return
        
        # Dinámicas del jugador
        self.rot_speed = 0
        self.vel = Vector2(0, 0)
        speed = self.vel.copy()
        
        # Movimiento de ejes
        if self.controler.left():
            self.vel.x = -PLAYER_SPEED
        if self.controler.right():
            self.vel.x = PLAYER_SPEED
        if self.controler.up():
            self.vel.y = -PLAYER_SPEED
        if self.controler.down():
            self.vel.y = PLAYER_SPEED
        
        # Movimientos opuestos los cancelamos
        if self.controler.left() and self.controler.right():
            self.vel.x = 0
        if self.controler.up() and self.controler.down():
            self.vel.y = 0

        # Movimientos diagonales       
        if self.vel.x!=0 and self.vel.y!=0:
            self.vel *= cos(pi/4)

        # Animaciones suaves        
        if  pg.time.get_ticks() - self.last_change > ANIM_DELAY:
            if speed != self.vel:
                self.numImagenPostura = (self.numImagenPostura + 1)%8
            else:
                self.numImagenPostura = 0
            self.last_change = pg.time.get_ticks()

        # Comprobamos is hay que cambiar de pistola (y si podemos)
        pistol = self.controler.switchPistol()
        if self.guns != []:
            if (pistol > 0) and (pistol <= len(self.guns)):
                self.guns[self.gunSelector].cancel_reload()
                self.gunSelector = pistol -1
                self.notify("gun",pistol -1)
                self.notify("ammo", self.guns[self.gunSelector].current_mag)
                self.notify("bullets",self.guns[self.gunSelector].bullets)
        else:
            self.reloading = True
            return

        # Recargar
        if (self.controler.reload()) and (self.guns[self.gunSelector].bullets > 0):
            self.guns[self.gunSelector].do_reload()
            self.reloading = True
            self.notify("ammo",-1)

        # Disparar
        if self.controler.isShooting():
            self.guns[self.gunSelector].shoot(self.pos, self.rot)
            self.notify("ammo",self.guns[self.gunSelector].current_mag)
            self.notify("bullets",self.guns[self.gunSelector].bullets)


    def update_health(self, health):
        if health <= 0:
            self.health = 0
            self.numPostura = 4
            self.numImagenPostura = 0
        else:
            self.health = health
        self.notify("health", self.health)


    # Actualizamos la munición del jugador
    def update_ammo(self):
        for gun in self.guns:
                gun.bullets = gun.MAG_SIZE 
        self.notify("bullets", self.guns[self.gunSelector].bullets)


    def update(self, camera_pos, dt):
        self.__callControler()
        # Miramos a donde nos tenemos que mover y a donde mirar
        direction = pg.mouse.get_pos() - Vector2(camera_pos) - self.pos
        self.rot = direction.angle_to(Vector2(1, 0))
        self.pos += self.vel * (dt/1000)
        
        if self.guns != []:
            self.guns[self.gunSelector].update()

        if self.health <= 0:
            super().update()
            return

        # Según si estamos recargando, o con un arma, seleccionamos una fila de la hoja u otra
        if self.reloading:
            self.numPostura = 3
            if self.guns != [] and self.guns[self.gunSelector].reload == False:
                self.notify("ammo",self.guns[self.gunSelector].current_mag)
                self.notify("bullets",self.guns[self.gunSelector].bullets)
                self.reloading = False

        elif self.gunSelector == 0:
            self.numPostura = 1
        elif self.gunSelector == 1:
            self.numPostura = 0
        elif self.gunSelector == 2:
            self.numPostura = 2
        super().update()
        