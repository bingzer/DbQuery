DbQuery
==============

DbQuery is a lightweight and flexible SQLite Query API for Android. See code for concept. 
DbQuery provides a new and simpler way to query data by minimizing the need to write SQL string inside the code.

Still a work in progress

Database Creation
--------------

    int dbVersion = 1;
    ...
    IDatabase db = DbQuery.getDatabase("MyDb");
    db.create(dbVersion, new SQLiteBuilder() {
    
        @Override 
        public Context getContext() {
           return getApplicationContext();
        }
        
        @Override 
        public MigrationMode getMigrationMode() {
           return MigrationMode.DropIfExists;
        }
        
        @Override
        public void onCreate(IDatabase.Modeling modeling) {
           // do the db modeling here..
           
           // this will create table PersonTable with Id and Name column
           modeling.add("PersonTable")           
                // method-chaining : configure columns inside this PersonTable
                .add("Id", "INTEGER", "primary key autoincrement not null")
                .add("Name", "TEXT")
                .add("Age", "INTEGER");
        }
    });
    ...
    
Using <code>ITable</code>
-----------
<code>IDatabase</code> provides access to the newly-created or existing tables inside the db

    ITable personTable = db.get("PersonTable");

<code>ITable</code> provides lots of methods for you to use to achieve common CRUD tasks

<code>Select</code> Query
-----------
Select and return all columns whose age is over 50 y/o

    Cursor cursor = personTable.select("Age > ?", 50).query();

Select and return name whose age is over 50 y/o

    Cursor cursor = personTable.select("Age > ?", 50).columns("Name").query();

Select all order by age

    Cursor cursor = personTable.select()
                        .orderBy("Age")
                        .query();
    
Select with complex query. 
(i.e: Return all person who's over 25 y/o and whose name starts with 'John')

    Cursor cursor = personTable.select("Age > ? AND Name LIKE ?", 25, "John%")
                        .orderBy("Age", "Name")
                        .query();

<code>Join</code> Operation
-----------
To join tables, the API provides:

    Cursor cursor = db.get("Orders O")
                         .join("Customers C", "C.Id = O.CustomerId")
                         .select(10, "Name = ?", "John Doe");
                         
    // This code will produce SQL query:
    //   SELECT * FROM Orders O 
    //   INNER JOIN Customers C
    //      ON C.Id = O.CustomerId
    //   WHERE Name = 'John Doe'
    //   LIMIT 10;
    

<code>Update</code> Operation
-----------
Update age to 21 by id

    personTable.update("Age", 21, "Id = ?", 1);
    
Update using ContentValues.
(i.e: Update with ContentValues whose name is John)

    ContentValues v = new ContentValues();
    ...
    personTable.update(v, "Name = ?", "John");


<code>Insert</code> Operation
-----------
Insert a person

    personTable.insert("Name", "Age").values("Ricky", 29);

Insert a person using ContentValues

    ContentValues v = new ContentValues();
    ...
    personTable.insert(v);


<code>Delete</code> Operation
-----------
Delete by id

    personTable.delete(1);

Bulk-delete

    personTable.delete(1,2,11,34);

Delete with condition
(i.e: Delete whose name starts with 'John')

    personTable.delete("Name LIKE ?", "John%");
    
Other Methods
-----------

Count everybody whose under 25 y/o

    int count = personTable.count("Age < ?", 25);

Count all rows

    int count = personTable.count();
    
Run raw sql

    String sql = ...    
    // unsafe
    personTable.raw(sql).query();
    // safe
    personTable.raw(sql, <selectionArgs>).query();
    
Run raw sql from db level

    String sql = ...
    db.raw(sql).query();
    // or
    db.raw(sql, <selectionArgs>).query();
    


License
-----------

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

