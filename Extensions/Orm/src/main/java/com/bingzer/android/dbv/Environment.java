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

/**
 * Default implementation of {@link IEnvironment}
 */
public class Environment implements IEnvironment {

    private static final IEnvironment environment = new Environment();

    /**
     * Returns the {@code local} environment.
     * Local environment is the default environment
     */
    public static IEnvironment getLocalEnvironment(){
        return environment;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    private IDatabase database = null;
    private IEntityFactory factory = null;

    /////////////////////////////////////////////////////////////////////////////////

    private Environment(){
        this(null, null);
    }

    /**
     * Creates an environment
     * @param database the database
     * @param factory the entity factory
     */
    public Environment(IDatabase database, IEntityFactory factory){
        this.database = database;
        this.factory = factory;
    }

    /////////////////////////////////////////////////////////////////////////////////

    protected void setDatabase(IDatabase db) {
        database = db;
    }

    protected void setEntityFactory(IEntityFactory factory) {
        this.factory = factory;
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the factory
     */
    @Override
    public IEntityFactory getEntityFactory(){
        return factory;
    }

    /**
     * Returns the database
     */
    @Override
    public IDatabase getDatabase(){
        return database;
    }


}
