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

import com.bingzer.android.dbv.IFunction;

/**
 * Created by Ricky Tobing on 7/20/13.
 */
public interface Function {

    /**
     * Returns average for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.IFunction.Average}
     */
    IFunction.Average avg(String columnName);

    /**
     * Returns the total for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.IFunction.Sum}
     */
    IFunction.Sum sum(String columnName);

    /**
     * Returns the maximum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.IFunction.Max}
     */
    IFunction.Max max(String columnName);


    /**
     * Returns the minimum value for the specified <code>columnName</code>
     * @param columnName column name
     * @return {@link com.bingzer.android.dbv.IFunction.Min}
     */
    IFunction.Min min(String columnName);
}
