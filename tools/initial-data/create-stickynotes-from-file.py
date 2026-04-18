# This script reads a list of sticky notes from a JSON file and creates them in the application via the REST API.

from urllib import request
from urllib.parse import urlencode
import json
import base64

username = "zooey"
password = "test123"
stickyNotesUrl = "http://localhost:8080/api/v1/stickyNotes"
tagsUrl = "http://localhost:8080/api/v1/tags"
inputFilename = "stickynotes.json"

def createHttpHeaders():
    b64auth = base64.standard_b64encode(('%s:%s' % (username, password)).encode()).decode()
    headers = {
        "Authorization": "Basic %s" % b64auth,
        "Content-Type": "application/json"
    }
    return headers

def getTagIdByName(name):
    httpHeaders = createHttpHeaders()
    queryParams = {
        "name": name
    }
    url = f"{tagsUrl}?{urlencode(queryParams)}"
    req = request.Request(url, method = "GET", headers = httpHeaders)

    r = request.urlopen(req)
    responseContent = r.read().decode("utf-8")
    responseObject = json.loads(responseContent)

    print("Received tag response: " + str(responseObject))
    return responseObject[0].get('id')

# If the stickyNote contains tags information, read the tag name, fetch the id of the tag and populate it beside tag name.
# This is needed because the REST service requires the id of the tag instead of its name.
def updateObjectIds(stickyNote):
    if "tags" in stickyNote:
        for tag in stickyNote['tags']:
            tagName = tag.get('tag', {}).get('name')
            tagId = getTagIdByName(tagName)
            tag['tag']['id'] = tagId

def main():
    with open(inputFilename) as f:
        stickyNoteList = json.load(f)
        httpHeaders = createHttpHeaders()

    for stickyNote in stickyNoteList:
        req = request.Request(stickyNotesUrl, method = "POST", headers = httpHeaders)

        updateObjectIds(stickyNote)

        print("Creating sticky note " + str(stickyNote))
        data = json.dumps(stickyNote).encode()
        r = request.urlopen(req, data=data)

        responseContent = r.read().decode("utf-8")
        responseObject = json.loads(responseContent)
        print("Created sticky note " + json.dumps(responseObject, indent = 2))

if __name__ == "__main__":
    main()
