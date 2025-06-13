```toml
name = 'add'
method = 'POST'
url = 'http://localhost:8080/api/cours'
sortWeight = 1000000
id = '5756b21d-6ed2-4bd1-9bfc-65f199175486'

[body]
type = 'JSON'
raw = '''
{
  "nom" : "JAVA",
  "localDateTime" : "2025-06-10T22:00:00.000Z",
  "localDate" : "2025-06-10T22:00:00.000Z",
  "instant" : "2025-06-10T22:00:00.000Z",
  "professeur" : {"id" : 3}
  }
}'''
```
