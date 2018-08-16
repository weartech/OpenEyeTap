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
        Gtk.Window.set_composited()
        screen = self.get_screen()
        rgba = screen.get_rgba_visual()
        print(screen.is_composited())
        if rgba != None and screen.is_composited():
            print("HEY")
            self.set_visual(rgba)
                
        self.set_app_paintable(True)
        self.connect("draw", self.area_draw)
        
        box = Gtk.Box()
        self.add(box)
        
        
    def on_button_clicked(self, widget):
        print("HELLO")
        
    def area_draw(self, widget, cr):
        cr.set_source_rgba(.8, .8, .8, 0.0)
        cr.set_operator(cairo.OPERATOR_SOURCE)
        cr.paint()
        cr.set_operator(cairo.OPERATOR_OVER)
        

win = Overlay()
win.connect("destroy", Gtk.main_quit)
win.show_all()
Gtk.main()