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
sticky_notes_url = "http://localhost:8080/api/v1/stickyNotes"
tags_url = "http://localhost:8080/api/v1/tags"
input_filename = "stickynotes.json"

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
                    tags_url,
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
    def update_object_ids(self, sticky_note):
        if "tags" in sticky_note:
            for tag in sticky_note['tags']:
                tagName = tag.get('tag', {}).get('name')
                if tagName is None:
                    raise ValueError(f"Missing name in tags for stickyNote {sticky_note}")
                tagId = self.get_tag_id_by_name(tagName)
                tag['tag']['id'] = tagId
        return sticky_note

    def create_sticky_note(self, sticky_note):
        updated_sticky_note = self.update_object_ids(sticky_note)

        response = self.session.post(
            sticky_notes_url,
            json = updated_sticky_note,
            timeout = 5
        )
        response.raise_for_status()
        result = response.json()

        result_json_text = json.dumps(result, indent=2)
        logger.info(f"Created sticky note: {result_json_text}")


    def create(self, sticky_note_list):
        for sticky_note in sticky_note_list:
            try:
                self.create_sticky_note(sticky_note)
            except Exception as e:
                logger.error(f"Failed to create sticky note {sticky_note}: {e}")

if __name__ == "__main__":
    with open(input_filename, encoding ="utf-8") as f:
        sticky_note_list = json.load(f)
    with StickyNoteCreator() as creator:
        creator.create(sticky_note_list)
