package com.bingzer.android.dbv;

import android.test.AndroidTestCase;

import com.example.Person;

public class BaseEntityTest extends AndroidTestCase {

    public void test_setget_id(){
        Person person = new Person();
        person.save();
    }
}
