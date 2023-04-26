################
#   Abstract   #
################

class Escena:

    def __init__(self, director):
        self.director = director

    def update(self, *args):
        raise NotImplemented("Tiene que implementar el metodo update.")

    def events(self, events):
        raise NotImplemented("Tiene que implementar el metodo eventos.")

    def draw(self, pantalla):
        raise NotImplemented("Tiene que implementar el metodo dibujar.")
    
    def play_music(self):
        raise NotImplemented("Tiene que implementar el metodo reproducir musica.")