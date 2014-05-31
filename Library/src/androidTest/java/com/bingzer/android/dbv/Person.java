package com.bingzer.android.dbv;

/**
 * Created by Ricky on 8/9/13.
 */
public class Person implements IEntity{

    private long id = -1;
    private String name;
    private int age;
    private byte[] addressBytes;

    public Person(){
        this(null, -1, null);
    }

    public Person(String name, int age, byte[] addressBytes){
        this.name = name;
        this.age = age;
        this.addressBytes = addressBytes;
    }

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

    public void setId(long id){
        this.id = id;
    }

    public byte[] getAddressBytes() {
        return addressBytes;
    }

    public void setAddressBytes(byte[] addressBytes) {
        this.addressBytes = addressBytes;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void map(Mapper mapper) {
        mapper.map("Name", new Delegate.TypeString() {

            @Override
            public void set(String value) {
                setName(value);
            }

            @Override
            public String get() {
                return getName();
            }
        });

        mapper.map("Address", new Delegate.TypeBytes() {
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

        mapper.mapId(new Delegate.TypeId(this){
            @Override
            public void set(Long id) {
                setId(id);
            }
        });

    }
}
