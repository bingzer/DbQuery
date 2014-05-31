package com.bingzer.android.dbv;

import java.util.LinkedList;

/**
 * Created by Ricky on 8/9/13.
 */
public class PersonList extends LinkedList<Person> implements IEntityList<Person>{
    @Override
    public Person newEntity() {
        return new Person();
    }
}
