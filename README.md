# lighthouse-server
Simple key-value web registry based on a Spray REST server.

## Deploying keys

For deploying keys at server's startup you should add a list of strings in server.conf like this

```sh
app.server.resources = ["key1","key2","key3"]
```

They will be automatically deployed in '/' path.
By default, all resources have 'ResourceValue' format:

```scala
case class ResourceValue(value: String)
```

So their equivalent JSON will be:

```sh
{ "value" : "my-value-string" }
```

## Running server

Just
```sh
sbt run
```

In order to check if server is running, you can send a GET Http request to / and it should response:

```
Lighthouse is up!
```

## Obtaining key value

Response might be a 204 error if key wasn't previously set or a 200 status code with the json media type of requested value.

```sh
Request:
	method: GET
	url: /<key>

Response1 (if available):
	{ "value" : "my-value-string" }

Response2 (if not available):
	<204 Status code>
```


## Setting a value

```sh
Request:
	method: PUT
	url: /path/to/my-key
	header: content-type:'application/json; charse=utf-8'
	content: { "value" : "my-value-string" }

Response:
	<200 Status code>
```

## TODOs

- Persistent key-values
- Deploy of diferent value types
- Security!
- Pretty index page :-)
- Add TTL to values.
