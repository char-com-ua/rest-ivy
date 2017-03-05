# rest-ivy
Simple rest api for local ivy repository.

## goal
Implement web requests to work with repository without build system.
It should be web application deployable to tomcat.
I choose the groovy lang to keep the simplicity (for me)
Full groovy library set includes ant and ivy - so that,s all what we need.

### API

#### /retrieve
This request corresponds to [inline ivy retrieve task](http://ant.apache.org/ivy/history/2.1.0/use/retrieve.html).
Parameters:

| name | description |
|------|-------------|
| org  | the organisation of the module to retrieve. |
| name | the name of the module to retrieve.  |
| rev  | the revision constraint of the module to retrieve. |
| type | comma separated list of artifact types to accept in the path, * for all |
| settings | the ivy settings filename suffix. file named `ivysettings-${settings}.xml` must be located in the root of your tomcat|

