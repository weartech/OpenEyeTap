# based on: l2capclient.py, a Demo L2CAP server for pybluez, written 2007-08-15 04:04:52Z albert

import bluetooth
import json
import threading
from interface import interface

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

data = client_sock.recv(1024)
print("Data received: ", str(data))

while data:
    # client_sock.send('Echo => ' + str(data))
    # this is where data is processed
    
    # TODO: parse multiple json requests at once
    print("Notification received.")
    json_string = data.decode("utf-8")

    notif_data = json.loads(json_string)
    print("Package: " + notif_data["package"])
    print("Title: " + notif_data["title"])
    print("Text: " + notif_data["text"])
    
    title = notif_data["title"]
    package = notif_data["package"]
    text = notif_data["text"]
    
    gui.get_message(title, package, text)

    # receive next message
    data = client_sock.recv(1024)
    print("Data received:", str(data))

client_sock.close()
server_sock.close()
gui.exit()
