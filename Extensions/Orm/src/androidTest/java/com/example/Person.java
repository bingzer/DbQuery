package com.example;

import com.bingzer.android.dbv.BaseEntity;
import com.bingzer.android.dbv.Delegate;

/**
 * Created by 11856 on 5/30/2014.
 */
public class Person extends BaseEntity {

    private String name;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The table name that this entity represents
     */
    @Override
    public String getTableName() {
        return "Person";
    }

    /**
     * Determines how to map column and the class variable.
     *
     * @param mapper the mapper object
     */
    @Override
    public void map(Mapper mapper) {
        mapId(mapper);
        mapper.map("Name", new Delegate.TypeString(){
            @Override
            public void set(String value) {
                setName(value);
            }
            @Override
            public String get() {
                return getName();
            }
        });

        mapper.map("Age", new Delegate.TypeInteger(){
            @Override
            public void set(Integer value) {
                setAge(value);
            }
            @Override
            public Integer get() {
                return getAge();
            }
        });
    }
}
