db.createCollection("stickynote", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         title: "Sticky Note Validation",
         required: [ "_id", "id", "title", "body", "type", "links", "created" ],
         additionalProperties:false,
         properties: {
            _id: {
            },
            id: {
               bsonType: "long",
               description: "'id' must be a long and is required"
            },
            title: {
               bsonType: "string",
               description: "'title' must be a string and is required"
            },
            body: {
               bsonType: "string",
               description: "'body' must be a string and is required"
            },
            type: {
               bsonType: "string",
               description: "'type' must be a string and is required"
            },
            links: {
               bsonType: "array",
               description: "'links' must be an array and is required",
               additionalProperties: false,
               items: {
                   bsonType: ["object"],
                   required: ["id", "link", "stickyNoteId"],
                   description: "'link' must contain the stated fields.",
                   properties: {
                       id: {
                       bsonType: "long",
                         description: "'id' is a long and is required"
                       },
                       link: {
                         bsonType: "string",
                         description: "'link' is a string and is required"
                       },
                       stickyNoteId: {
                       bsonType: "long",
                         description: "'stickyNoteId' is a long and is required"
                       }
                   }
               }
            },
            created: {
               bsonType: "date",
               description: "'created' must be a date and is required"
            }
         }
      }
   }
});

