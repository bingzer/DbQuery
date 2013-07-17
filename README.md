DbQueryProject
==============

SQLite Query API for Android

Database Creation
--------------

    int dbVersion = 1;
    IDatabase db = DbEngine.getDatabase("MyDb");
    db.create(version, new SQLiteBuilder() {
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


Query 'SELECT'
-----------
Select all person's name whose age is over 50 y/o

    Cursor cursor = db.get("PersonTable").select("Age > ?", 50).query();

Select all

    Cursor cursor = db.get("PersonTable").select(null).query();

Query 'UPDATE'
-----------
Update age to 21 by id

    db.get("PersonTable").update("Age", 21, "Id = ?", 1).query();

Query 'INSERT'
-----------
Adds a person's name

    db.get("PersonTable").insert("Name", "Age").values("Ricky", 29).query();


Query 'DELETE'
-----------
Delete by id

    int deleted = db.get("PersonTable").delete(1).query();


Bulk-delete

    int deleted = db.get("PersonTable").delete(1,2,11,34).query();


Query 'COUNT'
-----------

Count everybody whose under 25 y/o

    int count = db.get("PersonTable").count("Age < ?", 25).query();






