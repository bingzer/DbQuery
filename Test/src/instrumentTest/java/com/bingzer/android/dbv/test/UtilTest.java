package com.bingzer.android.dbv.test;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.Util;

/**
 * Created by Ricky on 8/9/13.
 */
public class UtilTest extends AndroidTestCase{

    public void testSafeEscape(){
        String bad = "'hello'";
        String good = "hello";

        bad = Util.safeEscape(bad);
        good = Util.safeEscape(good);

        assertTrue(bad.equalsIgnoreCase("'''hello'''"));
        assertTrue(good.equalsIgnoreCase("'hello'"));
    }

    public void testJoin(){
        assertTrue(Util.join(",","a","b").equals("a,b"));
        assertTrue(Util.join(".","a","b").equals("a.b"));
    }

    public void testBindArgs(){
        assertTrue(Util.bindArgs("ENTER THE DARK SIDE", 1,2,3).equals("ENTER THE DARK SIDE"));
        assertTrue(Util.bindArgs("Hello ?, How ? You", "World", 'R', "Invalid").equals("Hello 'World', How 'R' You"));
        assertTrue(Util.bindArgs("INSERT INTO (?,?,?)", 1,2,3).equals("INSERT INTO (1,2,3)"));

        assertTrue(Util.bindArgs("?", "'wake'").equals("'''wake'''"));
    }

    public void testBindArgs_WithMissingArgs(){
        assertTrue(Util.bindArgs("Hello ?, how are you ?", "World").equals("Hello 'World', how are you ?"));
    }
}
