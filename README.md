DbQuery
==============

SQLite Query API for Android. See code for concept.
Still a work in progress

Database Creation
--------------

    int dbVersion = 1;
    ...
    IDatabase db = DbEngine.getDatabase("MyDb");
    db.create(dbVersion, new SQLiteBuilder() {
        @Override 
        public Context getContext() {
           return getApplicationContext();
        }
        
        @Override 
        public MigrationMode getMode() {
           return MigrationMode.DropIfExists;
        }
        
        @Override
        public void onCreate(IDatabase.Modeling modeling) {
           // do the database modeling here..
           // this will create table PersonTable with Id and Name column
           modeling.add("PersonTable")
                    .add("Id", "INTEGER", "primary key autoincrement not null")
                    .add("Name", "TEXT")
                    .add("Age", "INTEGER");
        }
    });
    ...



Query 'SELECT'
-----------
Select and return all columns whose age is over 50 y/o

    Cursor cursor = db.get("PersonTable").select("Age > ?", 50).query();

Select and return name whose age is over 50 y/o

    Cursor cursor = db.get("PersonTable").select("Age > ?", 50).columns("Name").query();

Select all order by age

    Cursor cursor = db.get("PersonTable")
                        .select(null)
                        .orderBy("Age")
                        .query();
    
Select with complex query. 
(i.e: Return all person who's over 25 y/o and whose name starts with 'John')

    Cursor curosr = db.get("PersonTable")
                        .select("Age > ? AND Name LIKE ?", 25, "John%")
                        .orderBy("Age", "Name")
                        .query();


Query 'UPDATE'
-----------

Update age to 21 by id

    db.get("PersonTable").update("Age", 21, "Id = ?", 1).query();
    
Update using ContentValues.
(i.e: Update with ContentValues whose name is John)

    ContentValues v = new ContentValues();
    ...
    db.get("PersonTable").update(v, "Name = ?", "John").query();


Query 'INSERT'
-----------
Insert a person

    db.get("PersonTable").insert("Name", "Age").values("Ricky", 29).query();

Insert a person using ContentValues

    ContentValues v = new ContentValues();
    ...
    db.get("PersonTable").insert(v).query();


Query 'DELETE'
-----------
Delete by id

    int deleted = db.get("PersonTable").delete(1).query();


Bulk-delete

    int deleted = db.get("PersonTable").delete(1,2,11,34).query();

Delete with condition
(i.e: Delete whose name starts with 'John')

    int deleted = db.get("PersonTable").delete("Name LIKE ?", "John%").query();


Other Methods
-----------

Count everybody whose under 25 y/o

    int count = db.get("PersonTable").count("Age < ?", 25);

Count all rows

    int count = db.get("PersonTable").count();
    
Run raw sql

    String sql = ...    
    // unsafe
    db.get("PersonTable").raw(sql).query();
    // safe
    db.get("PersonTable").raw(sql, <selectionArgs>).query();


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


