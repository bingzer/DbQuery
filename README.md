[![Build Status](https://travis-ci.org/bingzer/DbQuery.svg?branch=master)](https://travis-ci.org/bingzer/DbQuery)

DbQuery
==============

`DbQuery` is a lightweight and fluent SQLite Query API for Android. The API provides a new and simpler way to query data by minimizing the need to write SQL string inside the code.

See complete **wiki** and **documentation** here:
[https://github.com/bingzer/DbQuery/wiki](https://github.com/bingzer/DbQuery/wiki)

Download
========
Download the latest stable binary (Master branch)
```groovy

repositories {
    mavenCentral()
}

dependencies {
    compile (group:'com.bingzer.android.dbv', name: 'dbquery', version:'2.0.0')
}
```

Download snapshots (Dev branch).
```groovy

repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}

// Force Gradle to get the latest SNAPSHOT everytime
configurations.all {
    // check for updates every build
    // change as needed
    resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
}

dependencies {
    compile (group:'com.bingzer.android.dbv', name: 'dbquery', version:'2.0.0-SNAPSHOT', changing: true)
}
```

Why
===
Sometimes we are stuck using the following code in Android development.
``` java
Cursor cursor = db.query("Customers", // table name
                         new String[] {"Id", "Address", "Age"},  // columns
                         "Id IN (?,?,?)",                       // whereClause
                         new String[]{"" + customerId1, "" + customerId2, "" + customerId3},  // whereArgs
                         null,         // groupBy
                         null,         // having
                         "Age");       // orderBy
```
The purpose of `DbQuery` is to be able to write this query differently
``` java
Cursor cursor = db.get("Customers")
                      .select("Id In (?,?,?)", customerId1, customerId2, customerId3) // whereClause
                      .columns("Id", "Address", "Age")    // columns
                      .orderBy("Age")  // orderBy 'Age'
                      .query(); 
```
`DbQuery` **will _change_** the way you write query.


# License
``` java
/**
 * Copyright 2014 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
