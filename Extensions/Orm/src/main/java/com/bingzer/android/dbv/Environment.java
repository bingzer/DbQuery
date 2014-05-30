/**
 * Copyright 2014 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import com.bingzer.android.dbv.contracts.IEntityFactory;
import com.bingzer.android.dbv.contracts.IEnvironment;


public class Environment implements IEnvironment {

    private static IEnvironment environment;
    public static IEnvironment getLocalEnvironment(){
        if(environment == null)
            environment = new Environment();
        return environment;
    }

    /////////////////////////////////////////////////////////////////////////////////

    private IDatabase database = null;
    private IEntityFactory factory = null;

    private Environment(){
        this(null, null);
    }

    public Environment(IDatabase database, IEntityFactory factory){
        this.database = database;
        this.factory = factory;
    }

    /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setDatabase(IDatabase db) {
        database = db;
    }

    @Override
    public IDatabase getDatabase(){
        return database;
    }

    @Override
    public void setEntityFactory(IEntityFactory factory) {
        this.factory = factory;
    }

    @Override
    public IEntityFactory getEntityFactory(){
        return factory;
    }


}
