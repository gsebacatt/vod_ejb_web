# VoD project

This is a project for a web application using Java EE and stateless entreprise
java beans as a backend and Angular 2 as a frontend. The following sections
covers the architecture details, how frontend and backend interacts and finally
how to install  and use it.

## Architecture

This project is composed of a backend implemented in Java Entreprise Edition and
a frontend using Angular2 beta. The backend and the frontend interacts with each
other through REST requests and responses. 

### REST API

#### Resources

The backend business logic is exposed as REST resources, please note that the
backend implementation could differ from the facade, as an example director and
author are member of the same Person table but exposed as different resources.
Below is a list of exposed resources available.

- dvd 
- dvd_provider 
- dvd_order
- author
- director
- payment : this resource is only available under the scope of a dvd_order, i.e with a url such as '/dvd_order/:id/payment'
- shipment : this resource is only available under the scope of a dvd_order, same as payment
- arrival : this resource is only available under the scope of a dvd
- search : a search among a collection of objects is exposed as resource, with the following fields : 
  - resource [string] : name of the resource type searched
  - id [number] : id of the resource, optional
  - fields [object] : a json providing fields to use in the search, eg if a dvd is searched according to its title, fields will be {title:"stringUsedForSearch"}
  - parentResource : a search object with the same fields as above. It is used to scope a search within a parent resource, eg search for a dvd with a particular author.

#### Use-case scenarios

- the base url is `http://localhost/vod-2/api/`.
- programs such as POSTMan or Paw are the perfect tools to test REST APIs.

##### Create a dvd

- POST at `dvd`
- body : 
```javascript
{
  "title": "8 Miles",
  "price": "10",
  "quantity": "20"
}
```

##### Create a dvd provider

- POST at `dvd_provider`
- body :
```javascript
{
  "name": "Big major"
}
```

##### Add a dvd to a dvd provider

- POST at 'dvd_provider/:id/dvd'
- body :
```javascript
{
  "title": "8 Miles",
  "id": dvdId,
  "price": "10",
  "quantity": "20"  
}
```

##### Create a dvd order

- POST at `dvd_order`

##### Add a dvd to a dvd order

- POST at `dvd_order/:id/dvd`
- same a "Add a dvd to a dvd provider"
- a dvd could as well be added to `author` and `director` with the same pattern

##### Available business logic related resources for a dvd order 

- These resources carry heavy lifting business logic and should be used with great care.
- POST at `dvd_order/:id/payment` to trigger a payment for a dvd order
- POST at `dvd_order/:id/shipment` to trigger a shipment for a dvd order
- POST at `dvd/:id/arrival` with a body as a json containing a `quantity` property

##### Search resource

- searches for every dvd which name contains 'Mile' and has an Author of id 1
- POST at `search`
- body :
```javascript
{
  "resource": "dvd",
  "fields": {
    "title": "Mile"
  },
  "parentResource": {
    "id": 1,
    "resource": "author"
  }
}
```

## Installation

### Backend

The backend codebase was coded using NetBeans IDE but is meant to be used without any IDE leveraging maven's lifecycle commands.

#### Database
- One mysql server installed, create a database named 'vod_project'
- It should be accessible with the url localhost:$DEFAULT_PORT$/vod_project

#### Wildfly server

The project uses Wildfly as the Java EE container, you must download it, and
follow the
[quickstart](https://docs.jboss.org/author/display/WFLY9/Getting+Started+Guide)
instruction to have it running in standalone mode and with an access to the
admin console (with `bin/add-user.sh`). Each time the backend is redeployed, the database is cleaned (it
uses a `drop-and-create` java persistence strategy).

##### Add MySQL as a data source to Wildfly
[__source__](http://giordanomaestro.blogspot.fr/2015/02/install-jdbc-driver-on-wildfly.html)
- download the MySQL jar connector
- go to your Wildfly directory
- `cd modules/system/layers/base && mkdir -p com/mysql/driver/main && cd com/mysql/driver/main`
- copy the jar into `modules/system/layers/base/com/mysql/driver/main`
- create a file named `module.xml` alogn with the jar file with the following content 
```xml
  <module xmlns="urn:jboss:module:1.3" name="com.mysql.driver">
    <resources>
      <resource-root path="NAME_OF_YOUR_JAR_FILE" />
    </resources>
    <dependencies>
      <module name="javax.api"/>
      <module name="javax.transaction.api"/>
    </dependencies>
  </module>
```
- in wildfly home, `cd bin/` then `./jboss-cli.sh` (you must have an instance of wildfly started using ./standalone.sh)
- run `/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql.driver,driver-class-name=com.mysql.jdbc.Driver)` 
- if you get a success, then you can go to the next step, if not, you have missed something.
- open wildfly console admin (localhost:9990) and Configuration > Subsystems > Datasources > Non-XA > Add
- at step 1/3 selec "MySQL connector" and ensure it have the following specs :
  - name : `vod_project`
  - JNDI : `java:/vod_project`
- at step 2/3 "JDBC Driver" go to detected driver and you should have "mysql", select it.
- at step 3/3, enter your information related to the connection to your mysql server.

#### Compile and deploy

Once wildfly is connected to the database and running, go to the project root,
and run `mvn clean install wildfly:deploy`. The backend should be deployed at
`http://localhost:8080/vod-2`.  

#### Undeploy

Run `mvn wildfly:undeploy -fae` to undeploy the app, note that `mvn clean install wildfly:deploy` will undeploy and deploy the app.

### Frontend

`Comming soon`

## How to use it

### Ruby scripts

The project contains some ruby scripts to use the backend, below are some
instruction to get it running.

- you need to have `ruby >= 2.0` installed
- if you do not have bundle installed, `gem install bundle`
- go to `$PROJECT_ROOT$/src/main/ruby/api_client`
- `bundle install`
- open a new terminal in the same directory
- `sidekiq -r ./api_client.rb`

#### Seed the database 

- a script that generates randomized data is provided in ruby, just type `ruby seed_db.rb` with Sidekiq running.

#### Run the load_test

- the load test is intended to stress the server, creating deadlock likely situations and DB connections pool shortage. Type `ruby load_test.rb` with Sidekiq running.