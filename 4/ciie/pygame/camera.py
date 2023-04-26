import pygame as pg
from settings import *

"""
Clase que nos permite gestionar una vista aerea recortada.
"""
class Camera:

    def __init__(self, width, height):
        self.camera = pg.Rect(0, 0, width, height)
        self.width = width
        self.height = height

    # Obtener coordeneadas relativas a la posición de la cámara
    def apply(self, entity):
        return self.apply_rect(entity.rect)

    def apply_rect(self, rect):
        return rect.move(self.camera.topleft)

    def update(self, target):
        # calculamos el centro
        x = -target.rect.centerx + int(WIDTH / 2)
        y = -target.rect.centery + int(HEIGHT / 2)

        # limitamos scrolling al tamaño del mapa
        x = min(0, x)  # izq
        y = min(0, y)  # arriba
        x = max(-(self.width - WIDTH), x)  # derecha
        y = max(-(self.height - HEIGHT), y)  # abajo
        self.camera = pg.Rect(x, y, self.width, self.height)