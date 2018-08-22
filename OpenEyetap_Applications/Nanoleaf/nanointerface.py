import requests
import json

class NanoleafInterface:

    def __init__(self, url, auth):
        self.path = "http://" + url + "/api/v1/" + auth

    # Brightness
    def set_brightness(self, brightness):
        requests.put(self.path + "/state/brightness", json={"value": brightness})

    def get_brightness(self):
        request = requests.get(self.path + "/state/brightness")
        return json.loads(request.text)["value"]

    # Hue
    def set_hue(self, hue):
        requests.put(self.path + "/state/hue", json={"value": hue})

    def get_hue(self):
        request = requests.get(self.path + "/state/hue")
        return json.loads(request.text)["value"]

    # Saturation
    def set_saturation(self, saturation):
        requests.put(self.path + "/state/sat", json={"value": saturation})

    def get_saturation(self):
        request = requests.get(self.path + "/state/sat")
        return json.loads(request.text)["value"]

    # Effects
    def set_effect(self, effect):
        requests.put(self.path + "/effects", json={"select": effect})

    def get_effect(self):
        request = requests.get(self.path + "/effects")
        return json.loads(request.text)["select"]
    
    def get_effects(self):
        request = requests.get(self.path + "/effects")
        return json.loads(request.text)["effectsList"]


if __name__ == "__main__":
    nano = NanoleafInterface("10.0.0.204:16021", "akYZIxhpNARKoOr18kDAOy3ihz3JhS8F")
    print(nano.get_effect())
    print(nano.get_effects())
    nano.set_effect("Love is Love")