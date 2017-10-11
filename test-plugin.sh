# Delete test index
#
curl --header "Content-Type:application/json" -X DELETE 'http://localhost:9200/lemmagen-test' && echo

# Create index with lemmagen filter
#
curl --header "Content-Type:application/json" -X PUT 'http://localhost:9200/lemmagen-test' -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "lemmagen_filter_en": {
            "type": "lemmagen",
            "lexicon": "en"
          },
          "protected_words": {
              "type": "keyword_marker",
              "keywords": ["apples"]
          },
          "unique_stem": {
            "type": "unique",
            "only_on_same_position": true
          }
        },
        "analyzer": {
          "lemmagen_en": {
            "type": "custom",
            "tokenizer": "uax_url_email",
            "filter": [
              "lemmagen_filter_en"
            ]
          },
          "lemmagen_with_keyword_repeat": {
            "type": "custom",
            "tokenizer": "uax_url_email",
            "filter": [
              "keyword_repeat",
              "lemmagen_filter_en",
              "unique_stem"
            ]
          },
          "lemmagen_with_keyword_marker": {
            "type": "custom",
            "tokenizer": "uax_url_email",
            "filter": [
              "protected_words",
              "lemmagen_filter_en"
            ]
          }
        }
      }
    }
  },
  "mappings" : {
    "message" : {
      "properties" : {
        "text" : { "type" : "text", "analyzer" : "lemmagen_en" }
      }
    }
  }
}' && echo

# Try it using _analyze api
#
curl --header "Content-Type:application/json" -X POST 'http://localhost:9200/lemmagen-test/_analyze?pretty' -d '{
  "text": "I am late because I am eating apples",
  "analyzer": "lemmagen_en"
}' && echo

curl --header "Content-Type:application/json" -X POST 'http://localhost:9200/lemmagen-test/_analyze?pretty' -d '{
  "text": "I am late because I am eating apples",
  "analyzer": "lemmagen_with_keyword_marker"
}' && echo

curl --header "Content-Type:application/json" -X POST 'http://localhost:9200/lemmagen-test/_analyze?pretty' -d '{
  "text": "I am late because I am eating apples",
  "analyzer": "lemmagen_with_keyword_repeat"
}' && echo

# Index document
#
curl --header "Content-Type:application/json" -XPUT 'http://localhost:9200/lemmagen-test/message/1?refresh=wait_for' -d '{
    "user"         : "tester",
    "published_at" : "2013-11-15T14:12:12",
    "text"         : "I am late."
}' && echo

# Search
#
curl --header "Content-Type:application/json" -X GET 'http://localhost:9200/lemmagen-test/_search?pretty' -d '{
  "query" : {
    "match" : {
      "text" : "is"
    }
  }
}'
