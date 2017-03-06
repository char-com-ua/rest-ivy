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
TODO... describe tweaks about ivysettings.xml config about cache to avoid dublicating of your repository


### API

#### /retrieve
This request corresponds to [inline ivy retrieve task](http://ant.apache.org/ivy/history/2.1.0/use/retrieve.html).

Resolves and returns content of only one artifact.

Parameters:

| name     | description |
|----------|-------------|
| org      | the organisation of the module to retrieve. |
| mod      | the name of the module to retrieve.  |
| rev      | the revision constraint of the module to retrieve. |
| type     | comma separated list of artifact types to accept in the path, * for all |
| conf     | the configuretion |
| settings | the ivy settings filename suffix. The file named `ivysettings-${settings}.xml` must be located in the root of your tomcat. if parameter not used the file name `ivysettings.xml` used to load ivy config.|

*returns http codes*

* `200` content of the module found plus header `x-file-name` with file name from repository.
* `500` any error occured including not found. content contains error message in text format.
* `400` no input parameters. help displayed in text format.

#### /resolve
The same as `/retrieve` except it returns the list of resolved artifacts instead of content
