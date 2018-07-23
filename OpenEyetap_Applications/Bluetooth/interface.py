from tkinter import *
import PIL
from PIL import ImageTk, Image
import os
import threading
import time

class interface(threading.Thread):
    def run(self):
        self.root = Tk()
        self.root.configure(background='black')
        self.root.geometry("1280x720")
        
        # fullscreen, makes it difficult to navigate and bugfix
        # root.attributes('-fullscreen', True)

        self.topFrame= Frame(self.root)
        self.topFrame.config(bg="black")
        self.topFrame.pack()

        self.bottomFrame= Frame(self.root)
        self.bottomFrame.pack(side=BOTTOM)
        self.bottomFrame.config(bg="black")
        self.bottomFrame.pack()

        self.label_title = Label(self.topFrame, text="Waiting for connection...", fg="white", bg="black")
        self.label_title.config(font=("Arial", 40))
        
        self.image = ImageTk.PhotoImage(Image.open("resources/notif.png"))
        self.image_panel = Label(self.topFrame, image=self.image, borderwidth=0, highlightthickness=0, background="black")
        
        self.label_package = Label(self.bottomFrame, text="Ensure Bluetooth discovery is enabled for this device", fg="white", bg="black")
        self.label_package.config(font=("Arial", 30))
        
        self.label_text = Label(self.bottomFrame, text="Select this device in the companion Android app", fg="white", bg="black")
        self.label_text.config(font=("Arial", 30))
        
        self.label_title.pack(side=RIGHT)
        self.image_panel.pack(side=LEFT, fill="both", expand="yes")
        self.label_package.pack()
        self.label_text.pack()
        
        self.root.mainloop()
    
    def clear_message(self):
        self.label_title['text'] = ""
        self.label_package['text'] = ""
        self.label_text['text'] = ""
        self.image = ImageTk.PhotoImage(Image.open("resources/black.jpg"))
        self.image_panel.configure(image=self.image)
        self.image_panel.image = self.image

    
    def get_message(self, title, package, text, image):
        print("Displaying notification")
        self.label_title['text'] = title
        self.label_package['text'] = package
        self.label_text['text'] = text
        self.image = ImageTk.PhotoImage(Image.open(image))
        self.image_panel.configure(image=self.image)
        self.image_panel.image = self.image
        time.sleep(2)
        
        self.clear_message()
        
        

