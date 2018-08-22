import requests
import json

auth = "akYZIxhpNARKoOr18kDAOy3ihz3JhS8F"

path = "10.0.0.204:16021"

def get_path():
    return "http://" + path + "/api/v1/" + auth + "/"

# r = requests.get(get_path())
while(True):
    command = input().split(' ')

    if(command[0] == "get"):
        r = requests.get(get_path() + command[1])
        print(r.text)
    elif(command[0] == "put"):
        r = requests.put(get_path() + command[1], json=json.loads(command[2]))



