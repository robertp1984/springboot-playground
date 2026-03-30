# This script reads a list of sticky notes from a JSON file and creates them in the application via the REST API.

from urllib import request
import json
import base64

username="zooey"
password="test123"
baseUrl = "http://localhost:8080/api/v1/stickyNotes"

with open('stickynotes.json') as f:
    stickyNoteList = json.load(f)

for stickyNote in stickyNoteList:
    b64auth = base64.standard_b64encode(('%s:%s' % (username, password)).encode()).decode()
    headers = {
        'Authorization': 'Basic %s' % b64auth,
        'Content-Type': 'application/json'
    }
    req = request.Request(baseUrl, method="POST", headers=headers)

    data = json.dumps(stickyNote).encode()
    r = request.urlopen(req, data=data)

    responseContent = r.read().decode("utf-8")
    responseObject = json.loads(responseContent)
    print(json.dumps(responseObject, indent=2))
