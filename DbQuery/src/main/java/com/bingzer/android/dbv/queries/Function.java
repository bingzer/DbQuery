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
     * Returns average
     * @param columnName
     * @return
     */
    IFunction.Average avg(String columnName);

    /**
     * Sum
     * @param columnName
     * @return
     */
    IFunction.Sum sum(String columnName);

    /**
     * Max
     * @param columnName
     * @return
     */
    IFunction.Max max(String columnName);

    /**
     * Min
     * @param columnName
     * @return
     */
    IFunction.Min min(String columnName);
}
