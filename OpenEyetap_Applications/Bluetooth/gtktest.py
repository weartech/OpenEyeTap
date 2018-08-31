import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk
import cairo
import sys

class Overlay(Gtk.Window):
    
    def __init__(self):
        Gtk.Window.__init__(self, title="Hello World")
        
        # borderless
        # self.set_decorated(False)
        screen = self.get_screen()
        
        self.set_opacity(0.2)
        
        

win = Overlay()
win.connect("destroy", Gtk.main_quit)
win.show_all()
Gtk.main()