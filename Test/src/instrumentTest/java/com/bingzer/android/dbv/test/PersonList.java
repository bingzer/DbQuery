package com.bingzer.android.dbv.test;

import com.bingzer.android.dbv.IEntityList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky on 8/9/13.
 */
public class PersonList extends LinkedList<Person> implements IEntityList<Person>{

    @Override
    public List<Person> getEntityList() {
        return this;
    }

    @Override
    public Person newEntity() {
        return new Person();
    }
}
