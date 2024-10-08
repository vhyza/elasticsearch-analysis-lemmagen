# LemmaGen Analysis for ElasticSearch

The LemmaGen Analysis plugin provides [jLemmaGen lemmatizer](https://github.com/hlavki/jlemmagen) as Elasticsearch [token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html).

[jLemmaGen](https://github.com/hlavki/jlemmagen) is Java implementation of [LemmaGen](http://lemmatise.ijs.si/) project (originally written in C++ and C#).

## Instalation

### Plugin

Beginning with elasticsearch 5 installation is following:

```bash
# specify elasticsearch version
#
export VERSION=6.0.0
./bin/elasticsearch-plugin install https://github.com/vhyza/elasticsearch-analysis-lemmagen/releases/download/v$VERSION/elasticsearch-analysis-lemmagen-$VERSION-plugin.zip
```

For older elasticsearch version see installation instructions in [**releases section**](https://github.com/vhyza/elasticsearch-analysis-lemmagen/releases).

### Lexicon

**WARNING:** Beginning with elasticsearch 6.0 this plugin **no longer provides** built-in lexicons. There is [separate lemmagen-lexicons repository](https://github.com/vhyza/lemmagen-lexicons) with them.

Copy desired lexicon(s) from [lemmagen-lexicons repository](https://github.com/vhyza/lemmagen-lexicons) into elasticsearch `config/lemmagen` directory (keep the `.lem` extension).

For example to install Czech language support do:

```bash
cd elasticsearch
mkdir config/lemmagen
cd config/lemmagen
wget https://github.com/vhyza/lemmagen-lexicons/raw/master/free/lexicons/cs.lem
```

After plugin installation and **elasticsearch restart** you should see in logs something like:

```bash
[2018-02-20T17:46:09,038][INFO ][o.e.p.PluginsService] [1rZCAqs] loaded plugin [elasticsearch-analysis-lemmagen]
```

## Usage

This plugin provides [token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html) of type `lemmagen`.

You need to specify `lexicon` or `lexicon_path` attribute.

* `lexicon`      - name of the file located in `config/lemmagen` (with or without `.lem` extension)
* `lexicon_path` - relative path to the lexicon file from elasticsearch config directory

For example Czech lexicon can be specified with any of the following configuration:

```json
{
    "index": {
        "analysis": {
            "filter": {
                "lemmagen_lexicon" : {
                    "type": "lemmagen",
                    "lexicon": "cs"
                },
                "lemmagen_lexicon_with_ext" : {
                    "type": "lemmagen",
                    "lexicon": "cs.lem"
                },
                "lemmagen_lexicon_path" : {
                    "type": "lemmagen",
                    "lexicon_path": "lemmagen/cs.lem"
                }
            }
        }
    }
}
```

## Example

```bash
# Delete test index
#
curl -H "Content-Type: application/json" -X DELETE 'http://localhost:9200/lemmagen-test'

# Create index with lemmagen filter
#
curl -H "Content-Type: application/json" -X PUT 'http://localhost:9200/lemmagen-test' -d '{
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
  "mappings": {
    "properties": {
      "text": {
        "type": "text",
        "analyzer": "lemmagen_en"
      }
    }
  }
}'

# Try it using _analyze api
#
curl -H "Content-Type: application/json" -X GET 'http://localhost:9200/lemmagen-test/_analyze?pretty' -d '{
  "text": "I am late.",
  "analyzer": "lemmagen_en"
}'

# RESPONSE:
#
# {
#   "tokens" : [
#     {
#       "token" : "I",
#       "start_offset" : 0,
#       "end_offset" : 1,
#       "type" : "<ALPHANUM>",
#       "position" : 0
#     },
#     {
#       "token" : "be",
#       "start_offset" : 2,
#       "end_offset" : 4,
#       "type" : "<ALPHANUM>",
#       "position" : 1
#     },
#     {
#       "token" : "late",
#       "start_offset" : 5,
#       "end_offset" : 9,
#       "type" : "<ALPHANUM>",
#       "position" : 2
#     }
#   ]
# }

# Index document
#
curl -H "Content-Type: application/json" -XPUT 'http://localhost:9200/lemmagen-test/_doc/1?refresh=wait_for' -d '{
    "user"         : "tester",
    "published_at" : "2013-11-15T14:12:12",
    "text"         : "I am late."
}'

# Search
#
curl -H "Content-Type: application/json" -X GET 'http://localhost:9200/lemmagen-test/_search?pretty' -d '{
  "query" : {
    "match" : {
      "text" : "is"
    }
  }
}'

# RESPONSE
#
# {
#   "took" : 2,
#   "timed_out" : false,
#   "_shards" : {
#     "total" : 5,
#     "successful" : 5,
#     "skipped" : 0,
#     "failed" : 0
#   },
#   "hits" : {
#     "total" : 1,
#     "max_score" : 0.2876821,
#     "hits" : [
#       {
#         "_index" : "lemmagen-test",
#         "_type" : "message",
#         "_id" : "1",
#         "_score" : 0.2876821,
#         "_source" : {
#           "user" : "tester",
#           "published_at" : "2013-11-15T14:12:12",
#           "text" : "I am late."
#         }
#       }
#     ]
#   }
# }
```

**NOTE**: `lemmagen` token filter doesn't lowercase. If you want your tokens to be lowercased, add [lowercase token filter](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-lowercase-tokenfilter.html) into your analyzer `filters`.

```bash
# Create index with lemmagen and lowercase filter
#
curl -H "Content-Type: application/json" -X PUT 'http://localhost:9200/lemmagen-lowercase-test' -d '{
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
        "text" : { "type" : "text", "analyzer" : "lemmagen_lowercase_en" }
      }
    }
  }
}'
```

## Development

To copy dependencies located in `lib` directory to you local maven repository (`~/.m2`) run:

```bash
mvn initialize
```

and to create plugin package run following:

```bash
mvn package
```

After that build should be located in `./target/releases`.


## Release

To release for a new Elasticsearch version, add it to the versions in `.github/workflows/build.yml`.
Then tag the commit and Github Actions should perform the rest.

    git tag v1
    git push --tags


Credits
=======

[LemmaGen team](http://lemmatise.ijs.si/Home/Contact) for original `C++`, `C#` implementation

[Michal Hlaváč](https://github.com/hlavki/jlemmagen) for `Java` implementation of LemmaGen

License
=======
All source codes are licensed under Apache License, Version 2.0.

    Copyright 2018 Vojtěch Hýža <http://vhyza.eu>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
