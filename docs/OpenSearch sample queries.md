# Open Search Sample Queries
This page contains sample queries for Open Search. These queries can be used as a starting point for building your own queries to search for specific information in your Open Search index.

The easiest way to execute these queries is to use the Open Search Dev Tools in OpenSearch Dashboards. You can access the Dev Tools by clicking on the "Dev Tools" link in the left-hand menu of the OpenSearch Dashboards or just access it directly via URI http://localhost:5601/app/dev_tools#/console

It is also possible to execute these queries using the Open Search REST API. You can use tools like curl or Postman to send HTTP requests to the Open Search endpoint.


## Show all documents in the index
```
GET /playground.stickynote/_search
{
  "query": {
    "match_all": { }
  }
}
```

## Use full text search to find documents with title field containing specific keywords (e.g. kafka)
```
GET /playground.stickynote/_search
{
  "query": {
    "match": {
      "title": "kafka"
    }
  }
}
```

## Use fuzzy text search to find documents with title field containing specific keywords (e.g. kafka)
```
GET /playground.stickynote/_search
{
  "query": {
    "match": {
      "title": {
        "query": "kfka",
        "fuzziness": "AUTO"
      }
    }
  }
}
```

## Use full text search to find documents with title or body field containing specific keywords (e.g. git push). This will automatically sort the results based on relevance score, so documents that match the query more closely will appear higher in the results.
```
GET /playground.stickynote/_search
{
  "query": {
      "bool": {
        "should": [
        { 
          "match": {
            "title": "git push"
          }
        },
        {
          "match": {
            "body": "git push"
          }
        }
        ]
      }
  }
}
```
