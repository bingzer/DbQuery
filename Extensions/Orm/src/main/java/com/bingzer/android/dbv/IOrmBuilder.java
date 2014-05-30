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

import java.util.List;

/**
 * Defines a contract interface for an {@code ORM} database builder.
 * This interface also {@code is} a type of {@link IEntityFactory}
 * @see com.bingzer.android.dbv.IDatabase.Builder
 */
public interface IOrmBuilder extends IDatabase.Builder, IEntityFactory {

    /**
     * Adds an internal table.
     * @param tableName table name
     */
    void addInternalTable(String tableName);

    /**
     * Returns the list of internal tables
     */
    List<String> getInternalTables();

}
