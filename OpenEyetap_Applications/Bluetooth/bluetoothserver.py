# based on: l2capclient.py, a Demo L2CAP server for pybluez, written 2007-08-15 04:04:52Z albert

import bluetooth
import json
import threading
from interface import interface
import subprocess

HEADER_LENGTH = 128

def decode_file(data, filename):
    f = open("temp/" + filename, "wb")
    f.write(data)
    f.close()

cmd = 'sudo hciconfig hci0 piscan'
subprocess.check_output(cmd, shell = True)

gui = interface()
gui.start()

server_sock=bluetooth.BluetoothSocket(bluetooth.RFCOMM)
port = 1

server_sock.bind(("",port))
server_sock.listen(1)

#uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ef"
#bluetooth.advertise_service( server_sock, "SampleServerL2CAP",
#                   service_id = uuid,
#                   service_classes = [ uuid ]
#                    )
print("Waiting for connection...")
client_sock,address = server_sock.accept()
print("Accepted connection from ",address)

gui.clear_message()

# data = client_soc.recv(1024)



data = client_sock.recv(HEADER_LENGTH)
print("Header received: ", str(data))
print("Header size: ", len(data))

while data:    
    decoded_data = data.decode("utf-8")
    
    decoded_data = decoded_data[:decoded_data.find('}')]
    decoded_data += '}'
    
    json_header = json.loads(decoded_data)  
    
    type = json_header["type"]
    bytes = json_header["bytes"]
    
    current_bytes = 0;
    data = b''
    read_bytes = 0;
    
    while read_bytes < bytes:
        print("Current file size: ", read_bytes)
        bytes_to_read = min(1024, bytes - read_bytes)
        data += client_sock.recv(bytes_to_read)
        read_bytes = len(data)
        print("Data received. Number of bytes read:", read_bytes, " / ", bytes)
        
        
    if type == "file":
        print("File size: ", len(data))
        name = json_header["name"]
        ext = json_header["ext"]
          
        filename = name + "." + ext
        
        decode_file(data, filename)
    elif type == "notif":
        print("Processing notification")
        
        json_string = data.decode("utf-8")
        print(json_string)
        notif_data = json.loads(json_string)
        
        print("Package: " + notif_data["package"])
        print("Title: " + notif_data["title"])
        print("Text: " + notif_data["text"])
        print("Image: " + notif_data["img"])
    
        title = notif_data["title"]
        package = notif_data["package"]
        text = notif_data["text"]
        image = "resources/notif.png"
        if(notif_data["img"] != ""):
            image = "temp/" + notif_data["img"]
        print(image)
    
        gui.get_message(title, package, text, image)
        
    data = client_sock.recv(HEADER_LENGTH)
    print("Header received: ", str(data))
    
    

#while data:
    # client_sock.send('Echo => ' + str(data))
    # this is where data is processed
    
    # TODO: parse multiple json requests at once
    #print("Notification received.")
    #json_string = data.decode("utf-8")

    #notif_data = json.loads(json_string)
    #print("Package: " + notif_data["package"])
    #print("Title: " + notif_data["title"])
    #print("Text: " + notif_data["text"])
    
    #title = notif_data["title"]
    #package = notif_data["package"]
    #text = notif_data["text"]
    
    #gui.get_message(title, package, text)

    # receive next message
    #data = client_sock.recv(1024)
    #print("Data received:", str(data))

client_sock.close()
server_sock.close()
gui.exit()

def find_newline(data):
    string_data = data.decode("UTF-8")
    