package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.utils.DbUtils;
import com.bingzer.android.dbv.SQLiteBuilder;

/**
 * Created by Ricky on 8/9/13.
 */
public class DbUtilsTest extends AndroidTestCase{

    public void testSafeEscape(){
        String bad = "'hello'";
        String good = "hello";

        String newBad = DbUtils.safeEscape(bad);
        String newGood = DbUtils.safeEscape(good);

        assertEquals(newBad, "'''hello'''");
        assertTrue(newGood.equalsIgnoreCase("'hello'"));
    }

    public void testJoin(){
        assertTrue(DbUtils.join(",", "a", "b").equals("a,b"));
        assertTrue(DbUtils.join(".", "a", "b").equals("a.b"));
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void testBindArgs(){
        String expected = "Name = 'Whatever' AND Age = 2 OR Address is null";
        String actual = DbUtils.bindArgs("Name = ? AND Age = ? OR Address is ?", "Whatever", 2, null);
        assertEquals(expected, actual);

        expected = "Name = 'I''m fine. You?' AND Age = 2 OR Address is 'Wallaby Way, Sidney?'";
        actual = DbUtils.bindArgs("Name = ? AND Age = ? OR Address is ?", "I'm fine. You?", 2, "Wallaby Way, Sidney?");
        assertEquals(expected, actual);
    }

    public void testBindArgs_WithMissingArgs(){
        String expected = "Hello 'World', how are you ?";
        String actual = DbUtils.bindArgs("Hello ?, how are you ?", "World");
        assertEquals(expected, actual);

    }

    ////////////////////////////////////////////////////////////////////////////////

    public void testArguments(){
        IDatabase db = DbQuery.getDatabase("DbUtilsTest");
        db.open(1, new SQLiteBuilder(){

            @Override
            public Context getContext() {
                return DbUtilsTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Customers")
                        .addPrimaryKey("Id")
                        .addText("Name")
                        .ifNotExists();
            }
        });

        Cursor cursor = db.get("Customers")
                .select("Name = ?", "$Yo$")
                .query();
        cursor.close();
    }
}
