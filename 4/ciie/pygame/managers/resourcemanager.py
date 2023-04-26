import os
import pygame as pg
import pytmx
from pygame.locals import RLEACCEL


"""
Singleton destinado a gestionar diversos recursos. También se usará como índice de rutas.
"""
class ResourceManager:

    image_resources = {}
    map_resources   = {}
    font_resources  = {}
    sound_resources = {}
    coord_resources = {}
    dialog_resources = {}

    RESOURCE_PATH = "resources"

    # Imágenes de Sprites
    PLAYER              = 'images-sprites/hero_sprites.png'
    BULLET_IMG          = 'images-sprites/bullet.png'
    EXPLOSION_IMAGE     = 'images-sprites/explode_bullet.png'
    MOB                 = 'images-sprites/soldiers.png'
    GUNNER              = 'images-sprites/gunners.png'
    BLOOD_IMAGE         = 'images-sprites/blood.png'
    HIT_IMAGE           = 'images-sprites/blood-splatter.png'
    AMMO_IMAGE          = 'images-sprites/ammo.png'
    HP_IMAGE            = 'images-sprites/HP.png'
    
    # Posiciones de los sprites
    HERO_POSITIONS   = 'positions/coordHero.txt'
    MOB_POSITIONS    = 'positions/coordMobs.txt'
    GUNNER_POSITIONS = 'positions/coordGunner.txt'

    # Imágenes de GUI
    LOGO_IMG     = 'images-gui/logo.png'
    START_IMG    = 'images-gui/inicio.png'
    BTN_BG       = 'images-gui/btn_bg.png'
    BOX_BG       = 'images-gui/box_bg.png'
    GAMEOVER_IMG = 'images-gui/gameover.png'
    PAUSE_IMG    = 'images-gui/pause.png'
    LVL_BTN      = 'images-gui/lvl_btn.png'
    SELECT_LOGO  = 'images-gui/select_level_logo.png'
    HUD          = 'images-gui/hud.png'
    PISTOLHUD    = 'images-gui/pistolhud.png'
    SMGHUD       = 'images-gui/smghud.png'
    MGHUD        = 'images-gui/mghud.png'
    VICTORY      = 'images-gui/victoria.png'
    DIALOG_BG      = 'images-gui/dialog_bg.png'

    # Fuentes
    MAIN_FONT = "fonts/ModernDOS9x16.ttf"

    # Sonidos
    SELECTION     = 'sound_effects/selection.mp3'
    METRALLETA    = 'sound_effects/metralleta.mp3'
    AMETRALLADORA = 'sound_effects/ametralladora.mp3'
    PISTOLA       = 'sound_effects/pistola.mp3'
    ITEM          = 'sound_effects/item.mp3'
    NO_AMMO       = 'sound_effects/no_ammo.mp3'

    # Mapas
    LEVEL = ['maps/level0.tmx','maps/level1.tmx','maps/level2.tmx','maps/level3.tmx','maps/level4.tmx']    
    
    # Diálogos
    DIALOGS = ['dialogs/level0.txt','dialogs/level1.txt','dialogs/level2.txt','dialogs/level3.txt','dialogs/level4.txt']

    
    @classmethod
    def load_image(self, nombre, colorkey=None):
        if nombre in self.image_resources:
            return self.image_resources[nombre]
        else:
            fullname = os.path.join(self.RESOURCE_PATH, nombre)
            try:
                imagen = pg.image.load(fullname)
            except (pg.error):
                print('Cannot load image: ', fullname)
                raise SystemExit
            imagen = imagen.convert_alpha() # o convert_alpha??
            if colorkey is not None:
                if colorkey == -1:
                    colorkey = imagen.get_at((0,0))
                imagen.set_colorkey(colorkey, RLEACCEL)
            self.image_resources[nombre] = imagen
            return imagen


    @classmethod
    def load_coord(self, name):
        if name in self.coord_resources:
            return self.coord_resources[name]
        else:
            fullname = os.path.join(self.RESOURCE_PATH, name)
            pfile = open(fullname,'r')
            raw_coord = pfile.read()
            pfile.close()
            datos = raw_coord.split()
            self.coord_resources[name] = datos
            return datos


    @classmethod
    def load_map(self, nombre):
        if nombre in self.map_resources:
            return self.map_resources[nombre]
        else:
            tm = pytmx.load_pygame(os.path.join(self.RESOURCE_PATH, nombre), pixelalpha=True)
            self.map_resources[nombre] = tm    
            return tm


    @classmethod
    def load_font(self, nombre, size):
        if (nombre,size) in self.font_resources:
            return self.font_resources[(nombre,size)]
        else:
            fullname = os.path.join(self.RESOURCE_PATH, nombre)
            try:
                font = pg.font.Font(fullname,size)
            except (pg.error):
                print('Cannot load font: ', fullname)
                raise SystemExit

            self.font_resources[(nombre,size)] = font
            return font


    @classmethod
    def load_sound(self, nombre):
        if nombre in self.sound_resources:
            return self.sound_resources[nombre]
        else:
            fullname = os.path.join(self.RESOURCE_PATH, nombre)
            try:
                sound = pg.mixer.Sound(fullname)
            except (pg.error):
                print('Cannot load sound: ', fullname)
                raise SystemExit

            self.sound_resources[nombre] = sound
            return sound


    @classmethod
    def load_dialog(self, name):
        if name in self.dialog_resources:
            return self.dialog_resources[name]
        else:
            fullname = os.path.join(self.RESOURCE_PATH, name)
            dialog = open(fullname,'r')
            raw = dialog.read()
            dialog.close()
            data = [ (line.split("|")[0],line.split("|")[1]) for line in raw.splitlines()]
            self.dialog_resources[name] = data
            return data