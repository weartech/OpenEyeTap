from tkinter import *
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

        self.label_title = Label(self.topFrame, text="Waiting for connection...", fg="white", bg="black")
        self.label_title.config(font=("Arial", 40))

        self.label_package = Label(self.topFrame, text="Ensure Bluetooth discovery is enabled for this device", fg="white", bg="black")
        self.label_package.config(font=("Arial", 30))
        
        self.label_text = Label(self.topFrame, text="Select this device in the companion Android app", fg="white", bg="black")
        self.label_text.config(font=("Arial", 30))
        
        self.label_title.pack()
        self.label_package.pack()
        self.label_text.pack()
        
        self.root.mainloop()
    
    def clear_message(self):
        self.label_title['text'] = ""
        self.label_package['text'] = ""
        self.label_text['text'] = ""
    
    def get_message(self, title, package, text):
        self.label_title['text'] = title
        self.label_package['text'] = package
        self.label_text['text'] = text
        
        time.sleep(2)
        
        self.clear_message()
        
        

