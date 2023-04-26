import pygame as pg
from pygame.math import Vector2
from managers.resourcemanager import ResourceManager as GR


"""
Clase destinada a la herencia. 
Implementa comportamientos comunes a los personajes como puede movimiento con animaciones, colisones con muros y vida
"""
class Character(pg.sprite.Sprite):

    def __init__(self, groups, img, hit_rect, x, y, health, collide_groups, pos, rows, numImagenes):

        if groups is None:
            super().__init__()
        else:
            super().__init__(groups)

        self.hoja = GR.load_image(img, colorkey=-1)
        
        # Cargamos las posiciones de las imágenes de la hoja
        data = GR.load_coord(pos)
        self.numPostura = 0
        self.numImagenPostura = 0
        cont = 0
        self.coordenadasHoja = []
        # Cargamos la hoja de sprites
        for linea in range(0, rows):
            self.coordenadasHoja.append([])
            tmp = self.coordenadasHoja[linea]
            for _ in range(1, numImagenes[linea]+1):
                tmp.append(pg.Rect((int(data[cont]), int(data[cont+1])), (int(data[cont+2]), int(data[cont+3]))))
                cont += 4
                
        self.moving = False
        self.image = self.hoja.subsurface(self.coordenadasHoja[self.numPostura][self.numImagenPostura])
        self.mask = pg.mask.from_surface(self.image)
        
        self.rect = self.image.get_rect()
        self.rect.center = (x, y)
        self.hit_rect = hit_rect
        self.hit_rect.center = self.rect.center

        self.vel = Vector2(0, 0)
        self.pos = Vector2(x, y)
        self.rot = 0

        self.health = health
        self.collision_groups = collide_groups


    # Función de colisión
    def __collide_hit_rect(self, one, two):
        return one.hit_rect.colliderect(two.rect)


    # Función para evitar colisionar con paredes y bugs
    def __collide_with_walls(self, sprite, group, dir):
        if dir == 'x':
            hits = pg.sprite.spritecollide(sprite, group, False, self.__collide_hit_rect)
            if hits:
                if hits[0].rect.centerx > sprite.hit_rect.centerx:
                    sprite.pos.x = hits[0].rect.left - sprite.hit_rect.width / 2
                if hits[0].rect.centerx < sprite.hit_rect.centerx:
                    sprite.pos.x = hits[0].rect.right + sprite.hit_rect.width / 2
                sprite.vel.x = 0
                sprite.hit_rect.centerx = sprite.pos.x
        if dir == 'y':
            hits = pg.sprite.spritecollide(sprite, group, False, self.__collide_hit_rect)
            if hits:
                if hits[0].rect.centery > sprite.hit_rect.centery:
                    sprite.pos.y = hits[0].rect.top - sprite.hit_rect.height / 2
                if hits[0].rect.centery < sprite.hit_rect.centery:
                    sprite.pos.y = hits[0].rect.bottom + sprite.hit_rect.height / 2
                sprite.vel.y = 0
                sprite.hit_rect.centery = sprite.pos.y


    def update(self):
        
        # rotamos la imagen y calculamos su máscara
        self.image = pg.transform.rotate(self.hoja.subsurface(self.coordenadasHoja[self.numPostura][self.numImagenPostura]), self.rot)
        self.mask = pg.mask.from_surface(self.image)

        self.rect = self.image.get_rect()
        self.rect.center = self.pos
        self.hit_rect.centerx = self.pos.x
        
        # Para cada eje calculamos sus colisiones con objetos
        for group in self.collision_groups:
            self.__collide_with_walls(self, group, 'x')
        self.hit_rect.centery = self.pos.y
        
        for group in self.collision_groups:
            self.__collide_with_walls(self, group, 'y')
        self.rect.center = self.hit_rect.center
