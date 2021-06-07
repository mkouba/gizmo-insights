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

## Where do I find my generated classes?

1. Production app
 - `target/quarkus-app/quarkus/generated-bytecode.jar`
 - `-Dquarkus.package.fernflower.enabled=true`

2. Testing, dev mode
 - classes are not written to the filesystem!
 - `-Dquarkus.debug.generated-classes-dir=dump`
 
You can also use `-Dquarkus.debug.generated-sources-dir=dump` to dump the pseudo-source `*.zig` files. 