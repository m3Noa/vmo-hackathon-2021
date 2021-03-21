# Backend VMO-Global Car Sharing

## Overview

This is a VMO-Global Car Sharing application. The actions that a user can performed are: create an car, view list cars, view details car, create an customer, view list customer,view details customer, deactive the customer, create an rental, update, started, return and cancel the rental. 

All actions performed on an account are recorded in Scalar DL, which means that the appliance information is recorded in a tamper-evident way, similar to how blockchains record blocks. This means that if any entity like car, customer and rental was altered (either intentionally or not), it is possible to detect this. 

This application uses some contracts:

+ `Car Category`
    - `CarCategoryCreater.java`
    - `CarCategoryDelete.java`
    - `CarCategoryDetails.java`
    - `CarCategoryGetList.java`
    - `CarCategoryUpdater.java`
+ `Car`
    - `CarCreater.java`
    - `CarDelete.java`
    - `CarDetails.java`
    - `CarGetList.java`
    - `CarGetListAvailable.java`
    - `CarUpdater.java`
+ `Customer`
    - `CustomerCreater.java`
    - `CustomerDeactive.java`
    - `CustomerDetails.java`
    - `CustomerGetList.java`
    - `CustomerGetListAll.java`
    - `CustomerUpdater.java`
+ `Car Rental`
    - `CarRentalCreater.java`
    - `CarRentalDetails.java`
    - `CarRentalGetList.java`
    - `CarRentalUpdater.java`
    - `CarRentalUpdateStatus.java`

Which can be found in [`src/main/java/com/scalar/carapp/contract/*`](src/main/java/com/scalar/carapp/contract/*).

## Get an auth token and a key pair (a certificate and a private key)

`PLEASE SKIP IF YOU WANT USE THE DEFAULT CERTIFICATE, PRIVATE KEY AND SAMPLE DATA INCLUDE IN SOURCE CODE`

You will need a [GitHub](https://github.com/) account to continue. If you don't have one, please create a free account.

We will authorize you through GitHub OAuth to grant you access to the Sandbox environment. Please visit our [sandbox site](https://scalar-labs.com/sandbox/), read the terms of use, and press the button Try Now. We will provide you with a zip file containing the necessary access token, key pair and configuration file. The access token is only used for authentication with Sandbox API gateway. The key pair is used for communicating with Scalar DL network.

Please note that we generate a pair of a private key and a certificate and register the certificate for ease of use for the Sandbox environment, but those steps are usually required in your own environment.

Extract and copy files client.properties, certificate file and private key file to folder conf/

Structure folder

```
conf/
    | - client.properties
    | - certificate.pem
    | - private-key.pem
```

Example contents of file client.properties

```
# Optional. A hostname or an IP address of the server. Use localhost by default if not specified.
# It assuems that there is a single endpoint that is given by DNS or a load balancer.
scalar.dl.client.server.host=sandbox.scalar-labs.com

# Optional. A port number of the server. Use 50051 by default if not specified.
scalar.dl.client.server.port=443

# Optional. A port number of the server for privileged services. Use 50052 by default if not specified.
#scalar.dl.client.server.privileged_port=50052

# Required. The holder ID of a certificate.
# It must be configured for each private key and unique in the system.
scalar.dl.client.cert_holder_id=username

# Optional. The version of the certificate. Use 1 by default if not specified.
# Use another bigger integer if you need to change your private key.
#scalar.dl.client.cert_version=1

# Required. The path of the certificate file.
scalar.dl.client.cert_path=./conf/certificate.pem

# Required. The path of a corresponding private key file to the certificate.
# Exceptionally it can be empty in some requests to privileged services
# such as registerCertificate and registerFunction since they don't need a signature.
scalar.dl.client.private_key_path=./conf/private-key.pem

# Optional. A flag to enable TLS communication. False by default.
scalar.dl.client.tls.enabled=true

# Optional. A custom CA root certificate for TLS communication.
# If the issuing certificate authority is known to the client, it can be empty.
#scalar.dl.client.tls.ca_root_cert_path=/path/to/ca-root-cert

# Optional. An authorization credential. (e.g. authorization: Bearer token)
# If this is given, clients will add "authorization: <credential>" http/2 header.
scalar.dl.client.authorization.credential=Basic xxxxxxxxxxxxxxxxx

```


## Setup

#### 1. Scalar DL Client SDK

Download the [Scalar DL Client SDK](https://github.com/scalar-labs/scalardl-java-client-sdk). Make sure Scalar DL is running and register all the required contracts by executing

Put SCALAR_SDK_HOME as variable name and provide the path of the Scalar DL Client SDK.

```
SCALAR_SDK_HOME = /path/to/scalardl-java-client-sdk
```

#### 2. Install JDK

Install [Java SE Development 8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html/), in this document we use JDK 8u121 and set enviroment path for JAVA_HOME.

```
JAVA_HOME = /path/to/Java/jdk1.8.0_121
```

#### 3. Install Gradle

Install and set enviroment for Gradle, Find guidelines on [Official Documentation](https://docs.gradle.org/current/userguide/installation.html#installing_with_a_package_manager).

Check version of after installed by command line `gradle -v`:

```
$ gradle -v

------------------------------------------------------------
Gradle 6.8.3
------------------------------------------------------------

Build time:   2021-02-22 16:13:28 UTC
Revision:     9e26b4a9ebb910eaa1b8da8ff8575e514bc61c78

Kotlin:       1.4.20
Groovy:       2.5.12
Ant:          Apache Ant(TM) version 1.10.9 compiled on September 27 2020
JVM:          1.8.0_121 (Oracle Corporation 25.121-b13)
OS:           Windows 10 10.0 amd64
```

## Build and Run

Run build use `gradle build` to generate files contracts class (Run command line from the `hackathon-backend` folder):

```
$ cd hackathon-backend
$ gradle build

Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.8.3/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 2s
4 actionable tasks: 4 executed
```

Register list contracts with Sandbox use `./register`  (Run command line from the `hackathon-backend` folder):

```
$ ./register

{
    "status_code": "OK",
    "output": null,
    "error_message": null
}

```

Run `gradle bootRun` in the `hackathon-backend` folder to start server. It should create a server on `localhost:8082` to which you can send HTTP requests in order to interact with the app.

```
$ gradle bootRun

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.4)

2021-03-19 21:52:01 INFO  Application:55 - Starting Application using Java 1.8.0_121 on ...
2021-03-19 21:52:01 INFO  Application:662 - No active profile set, falling back to default profiles: default
2021-03-19 21:52:03 INFO  TomcatWebServer:108 - Tomcat initialized with port(s): 8082 (http)
Mar 19, 2021 9:52:03 PM org.apache.catalina.core.StandardService startInternal
INFO: Starting service [Tomcat]
Mar 19, 2021 9:52:03 PM org.apache.catalina.core.StandardEngine startInternal
INFO: Starting Servlet engine: [Apache Tomcat/9.0.44]
Mar 19, 2021 9:52:03 PM org.apache.catalina.core.ApplicationContext log
INFO: Initializing Spring embedded WebApplicationContext
2021-03-19 21:52:03 INFO  ServletWebServerApplicationContext:289 - Root WebApplicationContext: initialization completed in 1404 ms
2021-03-19 21:52:04 INFO  ThreadPoolTaskExecutor:181 - Initializing ExecutorService 'applicationTaskExecutor'
2021-03-19 21:52:04 INFO  TomcatWebServer:220 - Tomcat started on port(s): 8082 (http) with context path ''
2021-03-19 21:52:04 INFO  Application:61 - Started Application in 3.116 seconds (JVM running for 3.515)
<==========---> 80% EXECUTING [15s]


```

