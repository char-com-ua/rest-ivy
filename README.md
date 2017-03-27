# rest-ivy
Simple rest api for local ivy repository.

## goal
Implement web requests to work with repository without build system.
It should be web application deployable to tomcat.
I choose the groovy lang to keep the simplicity (for me)
Full groovy library set includes ant and ivy - so that,s all what we need.

### dependencies
#### build
* `java 1.7+` (earlier version also may work)
* `groovy 2.4.5+` set path to your groovy/bin

to build just run:

`groovy build.groovy`


#### run
* `java 1.7+` (earlier version also may work)
* `tomcat 8+` (earlier version also may work)

to run:
* start tomcat
* put `rest-ivy.war` into `tomcat/webapps` directory
* configure `ivysettings-*.xml` files in the tomcat root directory (see configuration section)

### configuration
Check ivysettings-sample/ in the source code. Those settings works for me.

To support multithreading the property `ivy.cache.default.root` reffers to `tomcat/temp/.ivy2-${threadId}`

The attribute ` useOrigin="true" ` forces ivy to avoid copy artifacts to local cache

```xml
<ivysettings>
    ...
    <caches 
        useOrigin="true" 
        ivyPattern="${ivy.shared.default.ivy.pattern}" 
        artifactPattern="${ivy.shared.default.artifact.pattern}"
        defaultCacheDir="${ivy.cache.default.root}"
    />
</ivysettings>
```


### API

detailed description you can find inside.

#### /retrieve
This request corresponds to [inline ivy retrieve task](http://ant.apache.org/ivy/history/2.1.0/use/retrieve.html).
Resolves and returns content of only one artifact.

#### /resolve
The same as `/retrieve` except it returns the list of resolved artifacts instead of content

#### /revisions
returns revisions list for organization+module in json or text format

#### /repository
repository browser that can be used in url resolvers / publishers


