# lighthouse
Simple key-value web registry based on a Spray REST server.

## Deploying keys

For deploying keys at server's startup you should add a list of strings in server.conf like this

```sh
app.server.resources = ["key1","key2","key3"]
```

They will be automatically deployed in '/' path.

## Obtaining key value

```sh
method: GET
url: /<key>
```

Response might be a 204 error if key wasn't previously set or a 200 status code with the json media type of requested value.

## Setting a value

```sh
method: PUT
url: /<key>
header: content-type:'application/json; charse=utf-8'
content: <value>
```

## TODOs

- Persistent key-values
- Deploy of diferent value types
- Pretty index page :-)
- Add TTL to values.
