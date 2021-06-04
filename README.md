# Demo extension and example app for Quarkus Insights #52

First build the extension:

```
cd gizmonster
mvn clean install
```

Then run the example app in the dev mode:

```
cd ../
cd gizmo-started
mvn quarkus:dev
```

Finally, use the Swagger UI located at http://localhost:8080/q/swagger-ui/ or `curl -X 'POST' 'http://localhost:8080/transform/Foo' -H 'accept: text/plain'`
