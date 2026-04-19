# This script reads a list of sticky notes from a JSON file and creates them in the application via the REST API.

import requests
import json
import logging

logging.basicConfig(
    level = logging.INFO,
    format = "%(asctime)s [%(levelname)s] %(message)s"
)
logger = logging.getLogger(__name__)

username = "zooey"
password = "test123"
stickyNotesUrl = "http://localhost:8080/api/v1/stickyNotes"
tagsUrl = "http://localhost:8080/api/v1/tags"
inputFilename = "stickynotes.json"

class StickyNoteCreator:

    def __init__(self):
        self.tag_name_to_id_cache = { }
        self.auth_info = (username, password)
        self.http_headers = {
            "Content-Type": "application/json"
        }
        self.session = None

    def __enter__(self):
        self.session = requests.Session()
        self.session.auth = self.auth_info
        self.session.headers.update(self.http_headers)
        return self

    def __exit__(self, exc_type, exc, traceback):
        if self.session:
            self.session.close()

    def get_tag_id_by_name(self, name):
        # Check cache first for mapping
        if name in self.tag_name_to_id_cache:
            return self.tag_name_to_id_cache[name]

        response = self.session.get(
                    tagsUrl,
                    params = { "name": name },
                    timeout = 5)
        response.raise_for_status()
        data = response.json()

        logger.debug(f"Received tag response: {data}")

        # Check if there is exactly one element in table
        if not data or not isinstance(data, list) or len(data) < 1:
            raise ValueError(f"Tag named '{name}' was not found")

        tagId = data[0].get('id')
        if tagId is None:
            raise ValueError(f"Tag identifier is null for tag named '{name}'")

        # Update cache
        self.tag_name_to_id_cache[name] = tagId
        return tagId

    # If the stickyNote contains tags information, read the tag name, fetch the id of the tag and populate it beside tag name.
    # This is needed because the REST service requires the id of the tag instead of its name.
    def update_object_ids(self, stickyNote):
        if "tags" in stickyNote:
            for tag in stickyNote['tags']:
                tagName = tag.get('tag', {}).get('name')
                if tagName is None:
                    raise ValueError(f"Missing name in tags for stickyNote {stickyNote}")
                tagId = self.get_tag_id_by_name(tagName)
                tag['tag']['id'] = tagId
        return stickyNote

    def create_sticky_note(self, stickyNote):
        updatedStickyNote = self.update_object_ids(stickyNote)

        response = self.session.post(
            stickyNotesUrl,
            json = updatedStickyNote,
            timeout = 5
        )
        response.raise_for_status()
        result = response.json()

        resultJsonText = json.dumps(result, indent=2)
        logger.info(f"Created sticky note: {resultJsonText}")


    def create(self, stickyNoteList):
        for stickyNote in stickyNoteList:
            try:
                self.create_sticky_note(stickyNote)
            except Exception as e:
                logger.error(f"Failed to create sticky note {stickyNote}: {e}")

if __name__ == "__main__":
    with open(inputFilename, encoding = "utf-8") as f:
        stickyNoteList = json.load(f)
    with StickyNoteCreator() as creator:
        creator.create(stickyNoteList)
