import locale
import gettext
import urllib.request
import urllib.error
import json

_ = gettext.gettext

class Notas:     
    def __init__(self):
        self.notas = [_("do"), _("do♯/re♭"), _("re"), _("re♯/mi♭"), _("mi"), _("fa"), _("fa♯/sol♭"), _("sol"), _("sol♯/la♭"), _("la"), _("la♯/si♭"), _("si")]
        self.nota_actual=0
    
    def set_nota_actual_index(self,nota):
        self.nota_actual=nota 

    def get_nota_actual_index(self):
    	return self.nota_actual
    
    def get_nota_actual(self):
    	return self.notas[self.nota_actual]
    
    def get_notas(self):
    	return self.notas


class Intervals(Notas):
    def __init__(self,ip):
        Notas.__init__(self)
        self.ip=ip
        self.error=[False,""]
        self.directions = {_("Ascendente"):"asc", _("Descedente"):"des"}
        self.direction=0

        #Los intervalos pretenden ser de caracter mas estático, por lo que solo los cargamos en el init
        try:
            json_response = urllib.request.urlopen('http://' + self.ip + '/intervals').read()
            self.diccionario_intervalos=json.loads(json_response)["data"]
            self.intervalo_actual = 0
        
        except urllib.error.HTTPError as e:
            print('Error code: ', e.code)
            self.error[0]=True 
            self.error[1]=_("El recurso solicitado no se encuentra")

        except urllib.error.URLError as e:
            print('Reason: ', e.reason)
            self.error[0]=True 
            self.error[1]=_("No se puede conectar al servidor")

        except:
            print(_("Algo ha ido mal :("))
            self.error[0]=True 
            self.error[1]=_("Algo ha ido mal :(") 
    
    def songs(self):
        #Suposición inicial: sin fallos
        self.error=[False,""] 
        #Las canciones tienen un caracter más variable por lo que las solicitamos al api cada vez que se necesiten
        try:
            json_response = urllib.request.urlopen('http://' + self.ip + '/songs/'+ self.get_actual_interval_name() +"/"+self.get_direction_value()).read()
            return json.loads(json_response)["data"]

        except urllib.error.HTTPError as e:
            print('Error code: ', e.code)
            self.error[0]=True 
            self.error[1]=_("El recurso solicitado no se encuentra")

        except urllib.error.URLError as e:
            print('Reason: ', e.reason)
            self.error[0]=True 
            self.error[1]=_("No se puede conectar al servidor")

        except:
            print(_("Algo ha ido mal :("))
            self.error[0]=True 
            self.error[1]=_("Algo ha ido mal :(") 
    
    def aplicar_intervalo(self):
        #Suponemos estos ÚNICOS Casos
        #   T
        #   ST
        #   T ST
        if self.get_direction_value()=="asc":
            sign=1
        else:
            sign=-1
        desplazamiento=0

        #Decido hacerlo con splists xq no se la longitud del desplazamiento, si supones que solo puede ser 1ST dado que 2ST=1T
        split_st=self.get_value().split("ST")

        if split_st[0].isnumeric(): # habia un ST ["NUM",""]
            desplazamiento=int(split_st[0])
        else:
            split_t=split_st[0].split("T")
            if split_t[1].isnumeric():  # habia un T ST["NUM","NUM"]
                desplazamiento=int(split_t[0])*2+int(split_t[1])
            else: # habia un T ["NUM",""]
                desplazamiento=int(split_t[0])*2
        
        return self.notas[(self.nota_actual+sign*desplazamiento)%(len(self.notas))]

    #Geters
    def get_actual_interval_index(self): 
    	return self.intervalo_actual
    
    def get_intervals(self):
        return list(self.diccionario_intervalos.keys())

    def get_actual_interval_name(self):
        return self.get_intervals()[self.get_actual_interval_index()]

    def get_actual_direction_index(self):
    	return self.direction

    def get_directions(self):
    	return list(self.directions.keys())
   
    def get_direction_value(self):
        return self.directions[
            self.get_directions()[self.get_actual_direction_index()]
            ]
    
    def get_value(self):#Interval value
        return self.diccionario_intervalos[
            self.get_intervals()[self.get_actual_interval_index()]
            ]

    def get_error(self):
        return self.error

    #Seters
    def set_interval_actual_index(self, interval):
    	self.intervalo_actual = interval

    def set_direction_index(self, direction):
    	self.direction = direction

 