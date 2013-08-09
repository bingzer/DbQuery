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

import java.lang.reflect.ParameterizedType;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
public interface IEntity {

    void map(Mapper mapper);


    public static interface Mapper {

        void map(String column, Action action);
    }


    public static abstract class Action<T>{

        private Class<?> type;

        public Action(){
            this(null);
        }

        public Action(Class<?> type){
            if(type == null) {
                // use reflection
                try{
                    type = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                }
                catch (ClassCastException e){
                    type = Object.class;
                }
            }

            this.type = type;
        }

        public Class<?> getType(){
            return type;
        }

        public abstract void set(T value);

        public abstract T get();

    }
}
