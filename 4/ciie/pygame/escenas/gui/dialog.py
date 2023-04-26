from managers.resourcemanager import ResourceManager as GR
import pygame as pg
from settings import *
import sys

class Dialog():

    def __init__(self,level,enable=True):

        #Load BG
        self.image = GR.load_image(GR.DIALOG_BG)
        self.rect = self.image.get_rect() 
        self.rect.bottomright = (WIDTH,HEIGHT)

        #Zones
        xbox,ybox = self.rect.topleft
        self.name_pos_x = xbox+25
        self.name_pos_y = ybox+20        
        self.txt_pos_x = xbox+25
        self.txt_pos_y = ybox+45

        #Atributos Propios
        self.start_palabra = 0
        self.palabra = 0
        self.lines=[]
        self.last_time = pg.time.get_ticks()
        self.dialog_list = GR.load_dialog(GR.DIALOGS[level])
        self.dialog = 0
        self.font = GR.load_font(GR.MAIN_FONT,GUI_FONT_SIZE)
        self.enable = enable

        #IndicadorPulsa Espacio
        font1 = GR.load_font(GR.MAIN_FONT,12)
        self.indicacion_surface = font1.render("(Presiona espacio)", True, (240,240,240))
        self.indicacion_rect = self.indicacion_surface.get_rect()
        x,y = self.rect.bottomright
        self.indicacion_rect.bottomright =(x-7,y-22)

        #Inicializamos Texto Diálogo
        if not self.is_done():
            self.__load_name()
            self.__load_frase()


    ################################
    #    Operar sobre Diálogos     #
    ################################

    def next_dialog(self):
        self.dialog = self.dialog + 1
        self.start_palabra = 0
        self.palabra = 0
        self.lines=[]

    def is_done(self):
        return (len(self.dialog_list) - 1 < self.dialog) or not self.enable
 
    def __get_actual_name(self):
        x,_ = self.dialog_list[self.dialog]
        return x

    def __get_actual_frase(self):
        _,x = self.dialog_list[self.dialog]
        return x

    def update(self,_dt):
        if not self.is_done():
            now = pg.time.get_ticks()
            if now - self.last_time > DIALOG_SPEED:
                self.__load_name()
                self.__load_frase()
                self.last_time = now

    def set_enable(self):
        self.enable = True
        
    ##########################
    #  Renderizacion TEXTO   #
    ##########################

    def __load_name(self):
        self.name_surface = self.font.render(self.__get_actual_name(), True, (154,122,37))
        self.name_rect = self.name_surface.get_rect()
        self.name_rect.topleft = (self.name_pos_x,self.name_pos_y)

    def __load_frase(self):
        if self.palabra != -1:
            splited = self.__get_actual_frase().split()
            render_txt = ' '.join(splited[self.start_palabra:self.palabra])

            #Creamos Nueva línea
            rendered = self.font.render(render_txt, True, (240,240,240))        
            self.dialog_rect = rendered.get_rect()
            #Si sobrepasa creamos nueva línea
            if self.dialog_rect.size[0] > self.rect.size[0]-50:
                #Si la palabra ocupa todo el cuadro no podemos hacer gran cosa
                if self.palabra == self.start_palabra:
                    print("la palabra es demasiado grande, no cabe en una línea")
                    sys.exit()

                render_txt = ' '.join(splited[self.start_palabra:self.palabra-1])
                rendered = self.font.render(render_txt, True, (240,240,240)) 
                
                #Desacemos cambios en línea actual y creamos una nueva vacía     
                self.lines[-1] = rendered
                self.palabra = self.palabra - 1 # decrementamos puesto que en esta iteración no se llegó a printear
                self.start_palabra = self.palabra 
                self.lines.append(self.font.render("", True, (154,122,37)))

            else:
                if self.lines == []:
                    self.lines.append(rendered)
                else:
                    self.lines[-1] = rendered


            #Determinamos Si Continuamos o paramos
            if len(splited)-1 < self.palabra:
                self.palabra = -1
            else:
                self.palabra = self.palabra + 1
    
    def draw(self, display):
        if not self.is_done():
            display.blit(self.image, self.rect)
            display.blit(self.name_surface, self.name_rect)
            margin = 0
            for line in self.lines:
                display.blit(line, pg.Rect(self.txt_pos_x,self.txt_pos_y+margin,350,120))
                margin = margin + 25
            display.blit(self.indicacion_surface, self.indicacion_rect)