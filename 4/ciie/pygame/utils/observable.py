"""
Observable (destinada a herencia).
Patrón Observador
"""
class Observable:

    def __init__(self, observers):
        self.observers = observers

    def notify(self, type, change):
        for observer in self.observers:
            observer.update(type,change)

