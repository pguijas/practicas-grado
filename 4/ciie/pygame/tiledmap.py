import pygame as pg
import pytmx
from settings import *
import sys
from managers.resourcemanager import ResourceManager as GR


"""
Clase encargada de manejar los mapas. 
Contien el tiledmap (tm) y nos proporciona un método para volcar los tiles a una superficie (make_map)
"""
class TiledMap:
    
    def __init__(self, lvl):
        try:
            tm = GR.load_map(GR.LEVEL[lvl])
        except:
            print("El mapa no existe")
            sys.exit()

        self.width = tm.width * tm.tilewidth
        self.height = tm.height * tm.tileheight
        self.tmxdata = tm


    # Vuelca los 'tiles' en una superfice
    def __render(self, surface):
        ti = self.tmxdata.get_tile_image_by_gid
        for layer in self.tmxdata.visible_layers:
            if isinstance(layer, pytmx.TiledTileLayer):
                for x, y, gid, in layer:
                    tile = ti(gid)
                    if tile:
                        surface.blit(tile, (x * self.tmxdata.tilewidth, 
                                            y * self.tmxdata.tileheight))


    def make_map(self):
        temp_surface = pg.Surface((self.width, self.height))
        self.__render(temp_surface)
        return temp_surface

