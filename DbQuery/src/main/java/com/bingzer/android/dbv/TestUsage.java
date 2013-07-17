package com.bingzer.android.dbv;

/**
 * Created by 11856 on 7/16/13.
 */
class TestUsage {

    void init(){

        int version = 0;

        IDatabase db = DbEngine.getDatabase("Test");
        db.create(version, new IDatabase.Builder() {
            @Override
            public MigrationMode getMode() {
                return MigrationMode.ErrorIfExists;
            }

            @Override
            public void onCreate(IDatabase.Modeling modeling) {
                modeling.add("");
            }
        });


        db.get("table").select(null).columns("col1","col1").query();
        // output = "SELECT col1, col2 FROM table"

        db.get("table").insert("col1", "col2").values(1, 2).query();



    }


}
