import os
import pygame as pg
from settings import *
import json


"""
Singleton destinado a gestionar la configuración de caga usuario por medio de un JSON.
"""
class UserConfig:

    config = {}

    @classmethod
    def init(self):
        try:
            with open(USER_CONFIG_FILE) as json_file:
                self.config = json.load(json_file)
        except:
            pass
        
    
    @classmethod
    def update(self,key,value):
        self.config[key] = value
        with open(USER_CONFIG_FILE, 'w') as outfile:
            json.dump(self.config, outfile)


    @classmethod
    def get(self, key):
        return self.config[key]