# Terms lookup mechanism

## Adding test data

```
PUT /customers/_doc/1
{
  "name": "John Roberts",
  "following" : [2, 3]
}
```

```
PUT /customers/_doc/2
{
  "name": "Elizabeth Ross",
  "following" : []
}
```

```
PUT /customers/_doc/3
{
  "name": "Jeremy Brooks",
  "following" : [1, 2]
}
```

```
PUT /customers/_doc/4
{
  "name": "Diana Moore",
  "following" : [3, 1]
}
```

```
PUT /stories/_doc/1
{
  "customer": 3,
  "content": "Wow look, a penguin!"
}
```

```
PUT /stories/_doc/2
{
  "customer": 1,
  "content": "Just another day at the office... #coffee"
}
```

```
PUT /stories/_doc/3
{
  "customer": 1,
  "content": "Making search great again! #elasticsearch #elk"
}
```

```
PUT /stories/_doc/4
{
  "customer": 4,
  "content": "Had a blast today! #rollercoaster #amusementpark"
}
```

```
PUT /stories/_doc/5
{
  "customer": 4,
  "content": "Yay, I just got hired as an Elasticsearch consultant - so excited!"
}
```

```
PUT /stories/_doc/6
{
  "customer": 2,
  "content": "Chilling at the beach @ Greece #vacation #goodtimes"
}
```

## Querying stories from a customer's followers

```
GET /stories/_search
{
    "query": {
        "terms": {
            "customer": {
                "index": "customers",
                "type": "_doc",
                "id": 1,
                "path": "following"
            }
        }
    }
}
```