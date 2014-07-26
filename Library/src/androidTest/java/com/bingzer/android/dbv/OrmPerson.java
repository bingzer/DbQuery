package com.bingzer.android.dbv;

public class OrmPerson extends BaseEntity {

    private String name;
    private int age;

    public OrmPerson(){
        this(null, -1);
    }

    public OrmPerson(String name, int age){
        this.name = name;
        this.age = age;
    }

    public OrmPerson(IEnvironment environment){
        super(environment);
    }

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
