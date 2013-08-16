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

package com.bingzer.android.dbv.queries;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public interface Countable {

    /**
     * Returns the count of the specified condition
     * @param condition the condition
     * @return the count
     */
    int count(String condition);

    /**
     * Returns the count of row from the whereClause
     * (condition)
     * @param whereClause whereClause
     * @param whereArgs arguments
     * @return the count
     */
    int count(String whereClause, Object... whereArgs);

    /**
     * Returns the total row available in this table
     * @return the count of all rows
     */
    int count();

}
