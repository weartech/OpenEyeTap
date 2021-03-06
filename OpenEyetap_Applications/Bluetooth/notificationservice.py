from tkinter import *
import PIL
from PIL import ImageTk, Image
import os
import threading
import time
from queue import Queue
import json

class NotificationService(threading.Thread):
    def run(self):
        # set up UI
        self.root = Tk()
        
        self.root.overrideredirect(True)

        width = self.root.winfo_screenwidth()
        height = self.root.winfo_screenheight() // 3
        
               
        self.frame = Frame(self.root, width=width, height=height,
                           borderwidth=2, relief=RAISED)
        self.frame.pack_propagate(False)
        #self.frame.config(bg="blue")
        self.frame.pack()

        # set up subframes
        self.left_frame = Frame(self.frame)
        self.left_frame.config(bg="blue")
        self.left_frame.pack(side=LEFT)

        self.image = ImageTk.PhotoImage(Image.open("resources/notif.png"))
        self.image_panel = Label(self.left_frame, image=self.image, borderwidth=0, highlightthickness=0)
        self.image_panel.pack()

        self.right_frame = Frame(self.frame)
        self.right_frame.config(bg="blue")
        self.right_frame.pack(side=LEFT)

        self.label_title = Label(self.right_frame, text="Title", fg="white", bg="blue")
        self.label_title.config(font=("Arial", 60))
        self.label_title.pack()

        self.label_package = Label(self.right_frame, text="Title", fg="white", bg="blue")
        self.label_package.config(font=("Arial", 20))
        self.label_package.pack()

        self.label_text = Label(self.right_frame, text="Title", fg="white", bg="blue")
        self.label_text.config(font=("Arial", 30))
        self.label_text.pack()

        # set up notification queue
        self.notifications = Queue()

        # for increasing/decreasing opacity
        self.opacity = 0.0
        self.increasing_opacity = False
        
        self.location = 0
    


        # self.fade_notification()

        #self.root.attributes('-alpha', 0.0)

        self.root.mainloop()


    def get_data(self, data):
        print("Processing notification")
        
        json_string = data.decode("utf-8")
        print(json_string)
        notif_data = json.loads(json_string)
        
        if("package" in notif_data):
            print("Package: " + notif_data["package"])

        if("title" in notif_data):
            print("Title: " + notif_data["title"])

        if("text" in notif_data):
            print("Text: " + notif_data["text"])

        if("img" in notif_data):
            notif_data["img"] = "temp/" + notif_data["img"]
            print("Image: " + notif_data["img"])
        else:
            notif_data["img"] = "resources/notif.png"
            print("Image: " + notif_data["img"])

        self.prepare_notifications(notif_data)

    def prepare_notifications(self, data):
        if self.notifications.empty:
            self.notifications.put(data)
            self.increasing_opacity = True
            self.display_notifications()
    
    # only call this function when notifications empty
    def display_notifications(self):
        if not self.notifications.empty:
            notif = self.notifications.get()

            if 'title' in notif:
                self.label_title['text'] = notif['title']
            if 'package' in notif:
                self.label_package['text'] = notif['package']
            if 'text' in notif:
                self.label_text['text'] = notif['text']
            if 'img' in notif:
                self.image = ImageTk.PhotoImage(Image.open(notif['img']))
                self.image_panel.configure(image=self.image)
                self.image_panel.image = self.image

            self.root.after(3000, self.display_notifications)        
        else:
            self.increasing_opacity = False

    def fade_notification(self):
        # opacity decrease if notifications empty, else increase
        # cap at 0.0 and 1.0
        #if(self.increasing_opacity):
        #    if(self.opacity < 0.0):
        #        self.opacity = 0.0
        #    else:
        #        self.opacity -= 0.01
        #else:
        #    if(self.opacity > 1.0):
        #        self.opacity = 1.0
        #    else:
        #        self.opacity += 0.01
        
        #self.root.attributes('-alpha', self.opacity)
        
        if not self.increasing_opacity:
            if(self.location != 0):
                self.opacity -= 1
        else:
            if(self.location != self.root.winfo_height()):
                self.location += 1
        
        pos = "+0+" + str(self.root.winfo_screenheight() - self.location)
        
        self.root.geometry(pos)

        
        self.root.after(2, self.fade_notification)
            


