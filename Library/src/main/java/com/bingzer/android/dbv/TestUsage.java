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

import com.bingzer.android.dbv.queries.Select;

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

        for(ITable table : db.getTables()){
            for(String str : table.getColumns()){
            }
        }

        version++;
    }

    static class PersonList extends LinkedList<Person> implements IEntityList<Person> {

        @Override
        public Person newEntity() {
            return new Person();
        }

    }


    static class Person implements IEntity{
        private long id;
        private String name;
        private int age;


        /**
         * Returns the id
         *
         * @return the id
         */
        @Override
        public long getId() {
            return id;
        }

        @Override
        public void map(Mapper mapper) {
            //mapper.reflect(this);
            mapper.map("Name", new Delegate.TypeString(){
                @Override public void set(String value) {
                    name = value;
                }

                @Override public String get() {
                    return name;
                }
            });
            mapper.map("Age", new Delegate.TypeInteger(){

                @Override public void set(Integer value) {
                    age = value;
                }

                @Override
                public Integer get() {
                    return age;
                }
            });
            mapper.mapId(new Delegate.TypeId(this){

                @Override
                public void set(Long value) {
                    id = value;
                }
            });
        }
    }




}












