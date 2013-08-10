package com.bingzer.android.dbv.test.entities;

import com.bingzer.android.dbv.IEntity;

/**
 * Created by Ricky on 8/9/13.
 */
public class Person implements IEntity{

    private int id;
    private String name;
    private int age;
    private byte[] addressBytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setId(int id){
        this.id = id;
    }

    public byte[] getAddressBytes() {
        return addressBytes;
    }

    public void setAddressBytes(byte[] addressBytes) {
        this.addressBytes = addressBytes;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void map(IEntity.Mapper mapper) {
        mapper.map("Name", new IEntity.Action<String>(String.class) {

            @Override
            public void set(String value) {
                setName(value);
            }

            @Override
            public String get() {
                return getName();
            }
        });

        mapper.map("Address", new Action<byte[]>(byte[].class) {
            /**
             * Sets the value
             *
             * @param value the value to set
             */
            @Override
            public void set(byte[] value) {
                setAddressBytes(value);
            }

            /**
             * Returns the value
             *
             * @return the value
             */
            @Override
            public byte[] get() {
                return getAddressBytes();
            }

        });

        mapper.map("Age", new IEntity.Action<Integer>(Integer.class){

            @Override
            public void set(Integer value) {
                setAge(value);
            }

            @Override
            public Integer get() {
                return getAge();
            }
        });

        mapper.mapId(new IEntity.Action<Integer>(Integer.class){

            @Override
            public void set(Integer value) {
                setId(value);
            }

            @Override
            public Integer get() {
                return getId();
            }
        });

    }
}
