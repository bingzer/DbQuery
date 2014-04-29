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

package com.bingzer.android.dbv.contracts;

import com.bingzer.android.dbv.queries.Average;
import com.bingzer.android.dbv.queries.Max;
import com.bingzer.android.dbv.queries.Min;
import com.bingzer.android.dbv.queries.Sum;
import com.bingzer.android.dbv.queries.Total;

/**
 * Provides a basic contract commonly known function found in SQL
 */
public interface Function {

    /**
     * Returns average for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Average avg(String columnName);

    /**
     * Returns average for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Average avg(String columnName, String condition);

    /**
     * Returns average for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param whereClause where
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Average avg(String columnName, String whereClause, Object... args);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.queries.Sum}
     */
    Sum sum(String columnName);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Sum sum(String columnName, String condition);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param whereClause where
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Sum sum(String columnName, String whereClause, Object... args);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.queries.Sum}
     */
    Total total(String columnName);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Total total(String columnName, String condition);

    /**
     * Returns the sum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param whereClause where
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Total total(String columnName, String whereClause, Object... args);

    /**
     * Returns the maximum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.queries.Max}
     */
    Max max(String columnName);

    /**
     * Returns the maximum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Max max(String columnName, String condition);

    /**
     * Returns the maximum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param whereClause where
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Max max(String columnName, String whereClause, Object... args);

    /**
     * Returns the minimum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.queries.Min}
     */
    Min min(String columnName);

    /**
     * Returns the minimum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param condition the condition
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Min min(String columnName, String condition);

    /**
     * Returns the minimum value for the specified <code>columnName</code>
     * with condition
     * @param columnName column name
     * @param whereClause where
     * @param args arguments
     * @return {@link com.bingzer.android.dbv.queries.Average}
     */
    Min min(String columnName, String whereClause, Object... args);
}
