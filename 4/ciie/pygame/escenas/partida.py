import pygame as pg
from pygame.math import Vector2
from settings import *
from sprites.player import Player
from sprites.wall import Wall, Obstacle
from sprites.mob import MobBasico, MobRiffle
from sprites.blood import Blood
from sprites.explosion import Explosion
from sprites.hit import Hit
from sprites.ammo import Ammo
from sprites.health import HP
from sprites.area import Area
from tiledmap import TiledMap
from camera import Camera
from managers.user_config import UserConfig as UC
from managers.soundcontroller import SoundController as SC
from escenas.escena import Escena
from escenas.gameover import GameOver
from escenas.pause import Pause
from escenas.win import Win
from escenas.gui.hud import Hud
from escenas.gui.dialog import Dialog


"""
Partida (el juego en si)
"""
class Partida(Escena):
    
    def __init__(self,director, lvl, dialog=True):
        super().__init__(director)
        self.lvl = lvl

        # Diálogos
        #    Diferenciación de niveles. 
        #    Hardcodeado puesto que solo hay dos casos particulares (primero y último)
        self.dialog_level = False
        self.dialog_last = False
        if lvl == 0:
            self.dialog_level = True
        if lvl == 4:
            self.dialog_last = True
        self.dialog = Dialog(self.lvl, enable=(dialog and not self.dialog_last))

        # Hud
        self.hud = Hud()

        # Creación de grupos
        self.walls = pg.sprite.Group()
        self.obstacle = pg.sprite.Group()
        self.bullets_player = pg.sprite.Group()
        self.bullets_mobs = pg.sprite.Group()
        self.mobs = pg.sprite.Group()
        self.explosions = pg.sprite.Group()
        self.blood = pg.sprite.Group()
        self.hits = pg.sprite.Group()
        self.ammo = pg.sprite.Group()
        self.health = pg.sprite.Group()
        self.area = pg.sprite.Group()
        self.mob_count = 0

        # renderizado del mapa 
        self.map = TiledMap(self.lvl)
        self.map_img = self.map.make_map()
        self.map_rect = self.map_img.get_rect()
        # instanciamos objetos
        for tile_object in self.map.tmxdata.objects:
            if tile_object.name == 'player':
                self.player = Player(tile_object.x, tile_object.y, self.bullets_player, [self.walls, self.obstacle],[self.hud, self.ammo, self.health], self.lvl)
            if tile_object.name == 'Wall':
                Wall(self.walls, tile_object.x, tile_object.y, 
                        tile_object.width, tile_object.height)
            if tile_object.name == 'Object':
                Obstacle(self.obstacle, tile_object.x, tile_object.y, 
                        tile_object.width, tile_object.height)
            if tile_object.name == 'ammo':
                Ammo(self.ammo, tile_object.x, tile_object.y)
            if tile_object.name == 'health':
                HP(self.health, tile_object.x, tile_object.y)
            if tile_object.name == 'mob':
                if tile_object.gun=='pistol':
                    MobBasico(self.mobs, tile_object.x, tile_object.y, self.bullets_mobs, [self.walls, self.obstacle], tile_object.area)
                elif tile_object.gun=='rifle':
                    MobRiffle(self.mobs, tile_object.x, tile_object.y, self.bullets_mobs, [self.walls, self.obstacle], tile_object.area)
                self.mob_count += 1
            if tile_object.name == 'area':
                Area(self.area, tile_object.x, tile_object.y, tile_object.width, tile_object.height, tile_object.number)
        self.camera = Camera(self.map.width, self.map.height)
        self.draw_debug = False
        self.won = False


    # Colisiones de las balas con otros objetos del juego
    def __bullet_hits(self):
        # balas de los malos impactan con el jugador
        hits = pg.sprite.spritecollide(self.player, self.bullets_mobs, True, pg.sprite.collide_mask)
        for hit in hits:
            self.player.update_health(self.player.health - MOB_BULLET_DAMAGE)
            Hit(self.blood, self.player.pos, 0.5, 0.5, -self.player.rot-30)
            self.player.vel = Vector2(0, 0)
            if self.player.health <= 0:
                self.player_die = pg.time.get_ticks()
        # balas del jugador impactan con mobs
        hits = pg.sprite.groupcollide(self.mobs, self.bullets_player, False, True, pg.sprite.collide_mask)
        for hit in hits:
            if (hit.health - BULLET_DAMAGE) > 0:
                hit.health -= BULLET_DAMAGE
                Hit(self.blood, hit.pos, 0.5, 0.5, -hit.rot-30)
                hit.vel = Vector2(0, 0)
                hit.follow = True
            else:
                Hit(self.blood, hit.pos, 0.5, 0.5, -hit.rot-30)
                Blood(self.blood, hit.pos, 0.5, 0.5, -hit.rot-110)
                if not hit.dead:
                    hit.die()
                    self.mob_count -= 1
                    if self.mob_count == 0:
                        self.won = True
                        self.won_delay = pg.time.get_ticks()

        # colisiones de balas con paredes
        hits = pg.sprite.groupcollide(self.bullets_mobs, self.walls, True, False)
        for hit in hits:
            Explosion(self.explosions, hit.pos, 0.1, 0.1)
        hits = pg.sprite.groupcollide(self.bullets_player, self.walls, True, False)
        for hit in hits:
            Explosion(self.explosions, hit.pos, 0.1, 0.1)


    # Colisiones del jugador con cajas de armamento
    def __ammo_collision(self):
        if self.player.guns != []:
            if self.player.guns[self.player.gunSelector].bullets == self.player.guns[self.player.gunSelector].MAG_SIZE:
                return
        hits = pg.sprite.spritecollide(self.player, self.ammo, True)
        for _ in hits:
            self.player.update_ammo()
            SC.play_item()


    # Colisiones del jugador con cajas de vida
    def __hp_collision(self):
        if self.player.health == PLAYER_HEALTH:
            return
        hits = pg.sprite.spritecollide(self.player, self.health, True)
        for _ in hits:
            self.player.update_health(PLAYER_HEALTH)#A topisimo de municion
            SC.play_item()
    

    # Colisiones del jugador con áreas de activación de Mobs
    def __area_collision(self):
        hits = pg.sprite.spritecollide(self.player, self.area, True)
        for hit in hits:
            areaID = hit.get_number()
            for mob in self.mobs:
                mob.activate(areaID)


    # Función para mostrar pantalla de gameover
    def __gameover(self):
        gameover = GameOver(self.director, self.lvl)
        self.director.changeEscena(gameover)


    # Función para mostrar pantalla de victoria
    def __win(self):
        if self.dialog_last:
            self.dialog_last = False
            self.dialog_level = True
            self.dialog.set_enable()
        else:
            win = Win(self.director)
            self.director.changeEscena(win)
            try:
                if UC.get("last_level")==self.lvl:
                    UC.update("last_level",self.lvl+1)
            except:
                UC.update("last_level",self.lvl+1)
            
    def play_music(self):
        SC.play_main()


    def events(self, events):
        for event in events:
            #Salir Ventana
            if event.type == pg.QUIT:
                self.director.exitProgram()
            #Pulsaciones Teclas
            if event.type == pg.KEYDOWN:
                if event.key == pg.K_ESCAPE or event.key == pg.K_p: #Menu Pausa
                    pause = Pause(self.director, self.lvl)
                    self.director.pushEscena(pause)
                elif event.key == pg.K_SPACE:
                    self.dialog.next_dialog()


    def update(self, dt):
        # Actualizamos grupos de sprites
        self.player.update(self.camera.camera.topleft, dt)
        self.bullets_player.update(dt)
        self.bullets_mobs.update(dt)
        self.mobs.update(self.player.pos, dt)
        self.explosions.update()
        self.blood.update()
        self.hits.update()
        # Colisiones
        self.__bullet_hits()
        self.__ammo_collision()
        self.__hp_collision()
        self.__area_collision()

        # Posición de la cámara
        self.camera.update(self.player)

        # Acabamos el nivel
        if (self.player.health <= 0) and (pg.time.get_ticks() - self.player_die > DELAY_GAMEOVER):
            Blood(self.blood, self.player.pos, 0.5, 0.5, -self.player.rot-110)
            self.__gameover()
        if not self.dialog_level and self.won and (self.mob_count == 0) and (pg.time.get_ticks() - self.won_delay > DELAY_GAMEOVER*4):
            self.__win()
        if self.dialog_level and self.dialog.is_done():
            self.__win()

        # Diálogo
        self.dialog.update(dt)


    def draw(self, display):
        # Dibujamos el mapa que se ve en la cámara
        display.blit(self.map_img, self.camera.apply_rect(self.map_rect))

        # Dibujamos cada sprite por profundidad (de mas lejos a mas cerca)
        for sprite in self.blood:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.explosions:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.ammo:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.health:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.bullets_mobs:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.bullets_player:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.hits:
            display.blit(sprite.image, self.camera.apply(sprite))
        for sprite in self.mobs:
            display.blit(sprite.image, self.camera.apply(sprite))
        display.blit(self.player.image, self.camera.apply(self.player))

        # Dibujamos el HUD y los dialogos
        self.hud.draw(display)
        self.dialog.draw(display)