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

package com.bingzer.android.dbv.queries;

/**
 * Has something.
 */
public interface Tangible {

    /**
     * Check to see if this table has row add the specified condition
     * @param condition any condition
     * @return true if it returns any row false otherwise
     */
    boolean has(String condition);

    /**
     * has row add id
     * @param id id
     * @return true if it returns any row false otherwise
     */
    boolean has(int id);

    /**
     * Check to see if this table has row add the specified clause and condition
     * @param whereClause whereClause
     * @param whereArgs arguments
     * @return true if it returns any row false otherwise
     */
    boolean has(String whereClause, Object... whereArgs);

}
