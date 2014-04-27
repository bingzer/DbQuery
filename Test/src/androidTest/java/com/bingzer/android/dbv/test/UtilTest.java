package com.bingzer.android.dbv.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.internal.SQLiteBuilder;

/**
 * Created by Ricky on 8/9/13.
 */
public class UtilTest extends AndroidTestCase{

    public void testSafeEscape(){
        String bad = "'hello'";
        String good = "hello";

        String newBad = Util.safeEscape(bad);
        String newGood = Util.safeEscape(good);

        assertEquals(newBad, "'''hello'''");
        assertTrue(newGood.equalsIgnoreCase("'hello'"));
    }

    public void testJoin(){
        assertTrue(Util.join(",", "a", "b").equals("a,b"));
        assertTrue(Util.join(".", "a", "b").equals("a.b"));
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void testBindArgs(){
        String expected = "Name = 'Whatever' AND Age = 2 OR Address is null";
        String actual = Util.bindArgs("Name = ? AND Age = ? OR Address is ?", "Whatever", 2, null);
        assertEquals(expected, actual);

        expected = "Name = 'I''m fine. You?' AND Age = 2 OR Address is 'Wallaby Way, Sidney?'";
        actual = Util.bindArgs("Name = ? AND Age = ? OR Address is ?", "I'm fine. You?", 2, "Wallaby Way, Sidney?");
        assertEquals(expected, actual);
    }

    public void testBindArgs_WithMissingArgs(){
        String expected = "Hello 'World', how are you ?";
        String actual = Util.bindArgs("Hello ?, how are you ?", "World");
        assertEquals(expected, actual);

    }

    ////////////////////////////////////////////////////////////////////////////////

    public void testArguments(){
        IDatabase db = DbQuery.getDatabase("TestDb");
        db.open(1, new SQLiteBuilder.WithoutModeling(getContext()));

        Cursor cursor = db.get("Customers")
                .select("Name = ?", "$Yo$")
                .query();
        cursor.close();
    }
}
