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
 * Represents a contract interface for an {@code Environment} .
 * Environment is a virtual place where an {@code IBaseEntity}
 * will live in.
 */
public interface IEnvironment {

    /**
     * Returns the database
     */
    IDatabase getDatabase();

    /**
     * Returns the factory
     */
    IEntityFactory getEntityFactory();

}
