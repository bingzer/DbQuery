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
    compile (group:'com.bingzer.android.dbv', name: 'dbquery', version:'1.0.0')
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
    compile (group:'com.bingzer.android.dbv', name: 'dbquery', version:'1.0.0-SNAPSHOT', changing: true)
}
```

**IMPORTANT** Major refactoring is currently being done in the Dev branch

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

# Database Creation
``` java
int dbVersion = 1;
...
IDatabase db = DbQuery.getDatabase("MyDb");
db.open(dbVersion, new SQLiteBuilder() {

    @Override 
    public Context getContext() {
       return getApplicationContext();
    }
    
    @Override
    public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
       // do the db modeling here..
       
       // this will create table PersonTable with Id and Name column
       modeling.add("PersonTable")           
            // method-chaining : configure columns inside this PersonTable
            .addPrimaryKey("Id")
            .add("Name", "TEXT")
            .add("Age", "INTEGER");
    }
});
...
```

# Using `ITable`
`IDatabase`provides access to the newly-created or existing tables inside the db
``` java
    ITable personTable = db.get("PersonTable");
```
`ITable` provides lots of methods for you to use to achieve common CRUD tasks

# `Select` Statement
Select and return all columns whose age is over 50 y/o
``` java
Cursor cursor = personTable.select("Age > ?", 50)
                    .query();
                    
// SELECT * FROM PersonTable
// WHERE Age > 50
```
Select and return name whose age is over 50 y/o
``` java
Cursor cursor = personTable.select("Age > ?", 50)
                    .columns("Name")
                    .query();
                    
// SELECT Name FROM PersonTable
// WHERE Age > 50
```
Select all order by age
```
Cursor cursor = personTable.select()
                    .orderBy("Age")
                    .query();
// SELECT * FROM PersonTable
// ORDER BY Age ASC
```    
    
Select with complex query. 
(i.e: Return all person who's over 25 y/o and whose name starts with 'John')
``` java
Cursor cursor = personTable.select("Age > ? AND Name LIKE ?", 25, "John%")
                    .orderBy("Age", "Name")
                    .query();

// SELECT * FROM PersonTable
// WHERE Age > 25 AND Name LIKE 'John%'
// ORDER BY Age ASC, Name ASC
```

#`Join` Operation
To join tables, the API provides:
``` java
Cursor cursor = db.get("Orders O")
                     .join("Customers C", "C.Id = O.CustomerId")
                     .select(10, "Name = ?", "John Doe")
                     .query();
                     
//   SELECT * FROM Orders O 
//   INNER JOIN Customers C
//      ON C.Id = O.CustomerId
//   WHERE Name = 'John Doe'
//   LIMIT 10;
```    

# `Update` Operation
Update age to 21 by specifying a condition
``` java
personTable.update("Age", 21, "Name = ? AND Address = ?", "John Doe", "42 Wallaby Way, Sidney");
```    
Update age to 21 by using <code>Id</code>
``` java
int johnId = ...
personTable.update("Age", 21, johnId);
```    
Update age to 21 and Address to something else by specifying a condition
``` java
personTable.update(new String[]{"Age", "Address"},
                   new Object[]{21, "Something Else"},
                   "Name = ? AND Address = ?", "John Doe", "42 Wallaby Way, Sidney");
```
Update using `ContentValues`.
(i.e: Update with `ContentValues` whose name is John)
``` java
ContentValues v = new ContentValues();
...
personTable.update(v, "Name = ?", "John");
```

# `Insert` Operation
Insert a person
``` java
personTable.insert("Name", "Age")
           .values("John Doe", 29);
// or
personTable.insert(new String[]{"Name", "Age"},
                   new Object[]{"John Doe", 29});
```
Insert a person using `ContentValues`
``` java
ContentValues v = new ContentValues();
...
personTable.insert(v);
```

# `Delete` Operation
Delete by <code>id</code>
``` java
int johnId = ...
personTable.delete(johnId);
```
Bulk-delete by multiple <code>id</code>s
``` java
personTable.delete(1,2,11,34);
```
Bulk-delete with condition
(i.e: Delete whose name starts with 'John')
``` java
personTable.delete("Name LIKE ?", "John%");
```

# Other Methods
Count everybody whose under 25 y/o
``` java
int count = personTable.count("Age < ?", 25);
```
Count all rows
``` java
int count = personTable.count();
```
Run raw sql
``` java
String sql = ...    
// unsafe
personTable.raw(sql).query();
// safe
personTable.raw(sql, <selectionArgs>).query();
```
Run raw sql from db level
``` java
String sql = ...
db.raw(sql).query();
// or
db.raw(sql, <selectionArgs>).query();
```


# License
``` java
/**
 * Copyright 2013 Ricky Tobing
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
