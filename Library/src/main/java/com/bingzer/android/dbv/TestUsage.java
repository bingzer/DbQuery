/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bingzer.android.dbv;

import android.content.Context;

import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * This will be removed
 * Created by Ricky Tobing on 7/16/13.
 */
class TestUsage {

    void init(){

        int version = 0;
        version++;

        IDatabase db = DbQuery.getDatabase("Test");
        db.open(version, new SQLiteBuilder.WithoutModeling(null));

        //db.get("")


        Person person = new Person();
        db.get("Person").select().query(person);
        //db.get("Person").insert(person).query();

        IQuery.Select select = db.get("Person").select();


        PersonList list = new PersonList();
        db.get("").update(list);


        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {

            }
        });

        // -- transaction
        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {

            }
        }).commit();

        db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {

            }
        }).execute();
        db.get("Person").select().orderBy("Name").paging(10).query(list);

        // --- groupable
        db.get("").select().groupBy("","").having("","").query();
        db.get("").select().groupBy("").query();

        version++;
    }

    static class PersonList extends LinkedList<Person> implements IEntityList<Person> {

        @Override
        public List<Person> getEntityList() {
            return this;
        }

        @Override
        public Person newEntity() {
            return new Person();
        }
    }


    static class Person implements IEntity{

        private int id;
        private String name;
        private int age;


        /**
         * Returns the id
         *
         * @return the id
         */
        @Override
        public int getId() {
            return id;
        }

        @Override
        public void map(Mapper mapper) {
            mapper.map("Name", new Action<String>(String.class){
                @Override public void set(String value) {
                    name = value;
                }

                @Override public String get() {
                    return name;
                }
            });
            mapper.map("Age", new Action<Integer>(Integer.class){

                @Override public void set(Integer value) {
                    age = value;
                }

                @Override
                public Integer get() {
                    return age;
                }
            });
            mapper.mapId(new Action<Integer>(Integer.class) {
                @Override
                public void set(Integer value) {
                    id = value;
                }

                @Override
                public Integer get() {
                    return id;
                }
            });
        }
    }




}












