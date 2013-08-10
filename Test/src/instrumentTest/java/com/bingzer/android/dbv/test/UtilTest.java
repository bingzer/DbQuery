package com.bingzer.android.dbv.test;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.Util;

/**
 * Created by Ricky on 8/9/13.
 */
public class UtilTest extends AndroidTestCase{

    public void test_safeEscape(){
        String bad = "'hello'";
        String good = "hello";

        bad = Util.safeEscape(bad);
        good = Util.safeEscape(good);

        assertTrue(bad.equalsIgnoreCase("'''hello'''"));
        assertTrue(good.equalsIgnoreCase("'hello'"));
    }
}
