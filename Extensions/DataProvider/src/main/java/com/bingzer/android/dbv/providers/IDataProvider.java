/**
 * Copyright 2013 Ricky Tobing
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
package com.bingzer.android.dbv.providers;

import com.bingzer.android.dbv.IDatabase;

/**
 * IDataProvider
 */
public interface IDataProvider {

    /**
     * Returns an {@link IDatabase}.
     * You must also call {@link IDatabase#open(int, com.bingzer.android.dbv.IDatabase.Builder)}
     *
     * Example of implementation:
     * <code>
     * <pre>
     *     ...
     *     public IDatabase openDatabase(){
     *         IDatabase db = DbQuery.getDatabase("MyDatabase");
     *         db.open(1, new SQLiteBuilder(){
     *             ...
     *         });
     *
     *         return db;
     *     }
     *     ...
     * </pre>
     * </code>
     *
     * @see {@link IDatabase}
     * @return {@link IDatabase}
     */
    IDatabase openDatabase();

    /**
     * Returns the authority string. Android documentation suggested to use
     * package style name (i.e: com.company.data.x).
     *
     * @return Authority string
     */
    String getAuthority();

}
