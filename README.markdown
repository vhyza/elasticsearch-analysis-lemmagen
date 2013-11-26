LemmaGen Analysis for ElasticSearch
===================================

The LemmaGen Analysis plugin provides [jLemmaGen lemmatizer](https://bitbucket.org/hlavki/jlemmagen) as Elasticsearch [token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html).

[jLemmaGen](https://bitbucket.org/hlavki/jlemmagen) is Java implementation of [LemmaGen](http://lemmatise.ijs.si/) project (originally written in C++ and C#).

[LemmaGen](http://lemmatise.ijs.si/) is open-source lemmatizer which includes lexicons for following European languages.

* Bulgarian (bg)
* Czech (cs)
* English (en)
* Estonian (et)
* French (fr)
* Hungarian (hu)
* Macedonian (mk)
* Polish (pl)
* Romanian (ro)
* Russian (ru)
* Slovak (sk)
* Slovene (sl)
* Serbian (sr)
* Ukrainian (uk)

Instalation
===========

To install the plugin, run:

```bash
bin/plugin --url http://bit.ly/analysis-lemmagen --install elasticsearch-analysis-lemmagen
```


**Restart Elasticsearch**, and you should see ...

```bash
[2013-11-25 00:33:01,146][INFO ][plugins] [Forrester, Lee] loaded [analysis-lemmagen], sites []
```

in logs.

Usage
=====

This plugin provides [token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html) of type `lemmagen`. You can specify language by setting `lexicon` option. Valid values are `bg, cs, en, et, fr, hu, mk, pl, ro, ru, sk, sl, sr, uk`. Default is `en`.

Example
-------
```bash
# Delete test index
#
curl -X DELETE 'http://localhost:9200/lemmagen-test'

# Create index with lemmagen filter 
#
curl -X POST 'http://localhost:9200/lemmagen-test' -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "lemmagen_filter_en": {
            "type": "lemmagen",
            "lexicon": "en"
          }
        },
        "analyzer": {
          "lemmagen_en": {
            "type": "custom",
            "tokenizer": "uax_url_email",
            "filter": [
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
        "text" : { "type" : "string", "analyzer" : "lemmagen_en" }
      }
    }
  }
}'

# Try it using _analyze api
#
curl -X GET 'http://localhost:9200/lemmagen-test/_analyze?analyzer=lemmagen_en&pretty' -d 'I am late.'

# RESPONSE:
# 
# {
#   "tokens" : [ {
#     "token" : "i",
#     "start_offset" : 0,
#     "end_offset" : 1,
#     "type" : "<ALPHANUM>",
#     "position" : 1
#   }, {
#     "token" : "be",
#     "start_offset" : 2,
#     "end_offset" : 4,
#     "type" : "<ALPHANUM>",
#     "position" : 2
#   }, {
#     "token" : "late",
#     "start_offset" : 5,
#     "end_offset" : 9,
#     "type" : "<ALPHANUM>",
#     "position" : 3
#   } ]
# }

# Index document
#
curl -XPUT 'http://localhost:9200/lemmagen-test/message/1' -d '{
    "user"         : "tester",
    "published_at" : "2013-11-15T14:12:12",
    "text"         : "I am late."
}'

# Refresh index
#
curl -XPOST 'http://localhost:9200/lemmagen-test/_refresh'

# Search
#
curl -X GET 'http://localhost:9200/lemmagen-test/_search?pretty' -d '{
  "query" : {
    "match" : {
      "text" : "is"
    }
  }
}'

# RESPONSE
#
#{
#  "took" : 53,
#  "timed_out" : false,
#  "_shards" : {
#    "total" : 5,
#    "successful" : 5,
#    "failed" : 0
#  },
#  "hits" : {
#    "total" : 1,
#    "max_score" : 0.15342641,
#    "hits" : [ {
#      "_index" : "lemmagen-test",
#      "_type" : "message",
#      "_id" : "1",
#      "_score" : 0.15342641, "_source" : {
#        "user"         : "tester",
#        "published_at" : "2013-11-15T14:12:12",
#        "text"         : "I am late."
#    }
#    } ]
#  }
#}
```

**NOTE**: `lemmagen` token filter doesn't lowercase. If you wan't your tokens to be lowercased, add [lowercase token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-lowercase-tokenfilter.html) into your analyzer `filters`.

```bash
# Create index with lemmagen and lowercase filter
#
curl -X POST 'http://localhost:9200/lemmagen-lowercase-test' -d '{
  "settings": {
    "index": {
      "analysis": {
        "filter": {
          "lemmagen_filter_en": {
            "type": "lemmagen",
            "lexicon": "en"
          }
        },
        "analyzer": {
          "lemmagen_lowercase_en": {
            "type": "custom",
            "tokenizer": "uax_url_email",
            "filter": [ "lemmagen_filter_en", "lowercase" ]
          }
        }
      }
    }
  },
  "mappings" : {
    "message" : {
      "properties" : {
        "text" : { "type" : "string", "analyzer" : "lemmagen_lowercase_en" }
      }
    }
  }
}'
```

Plugin is tested on elasticsearch version `0.90.7`.

Credits
=======

[LemmaGen team](http://lemmatise.ijs.si/Home/Contact) for original `C++`, `C#` implementation

[Michal Hlaváč](https://bitbucket.org/hlavki/jlemmagen) for `Java` implementation of LemmaGen

License
=======
    Copyright 2013 Vojtěch Hýža <http://vhyza.eu>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
