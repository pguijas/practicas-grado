from pygame import Rect

#################################################
#  Parámetros para el funcionamiento del juego  #
#################################################

# General game setings
TITLE = "The Code"
WIDTH = 1024  # 64 * 16
HEIGHT = 768  # 64 * 12
FPS = 120
SPRITE_BOX = 64
DIALOG_SPEED = 30
ANIM_DELAY = 50  #delay entre animacioness
DELAY_GAMEOVER = 400
USER_CONFIG_FILE = "config.json"

# MUSIC    
START_MUSIC    = 'resources/music/start_background.ogg'
MAIN_MUSIC     = 'resources/music/main_background.ogg'
GAMEOVER_MUSIC = 'resources/music/gameover.ogg'
VICTORY_MUSIC  = 'resources/music/victory.ogg'

# Player settings
PLAYER_HEALTH = 100
PLAYER_SPEED = 500.0
PLAYER_HIT_RECT = Rect(0, 0, 35, 35)

# GUNS
BULLET_DAMAGE = 34
MOB_BULLET_DAMAGE = BULLET_DAMAGE
EXPLOSION_LIFETIME = 100
HIT_LIFETIME = 100

# Mob Settings
MOB_SPEED = 500
MOB_HIT_RECT = Rect(0, 0, 30, 30)
MOB_HEALTH = 10
MOB_ATTK_DISTANCE = 200

# Gui Sizes
GUI_FONT_SIZE = 20
