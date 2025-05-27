```toml
name = 'add'
method = 'POST'
url = 'http://localhost:8080/api/etudiant'
sortWeight = 1000000
id = 'd9aee5b5-9936-42bb-b44d-379535e9b63b'

[body]
type = 'JSON'
raw = '''
{
  "email" : "a@a.com",
  "password" : "root",
  "dateNaissance" : "2001-05-20"
}'''
```
