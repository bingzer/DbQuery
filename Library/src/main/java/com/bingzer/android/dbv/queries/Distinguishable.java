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

import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 8/21/13.
 */
public interface Distinguishable {

    /**
     * Select distinct all.
     * Equivalent of calling <code>selectDistinct(null)</code>
     * @return {@link com.bingzer.android.dbv.IQuery.Select}
     */
    IQuery.Select selectDistinct();

    /**
     * Select distinct
     * @param condition the condition
     * @return {@link IQuery.Select}
     */
    IQuery.Select selectDistinct(String condition);

    /**
     * Select distinct add condition
     * @param whereClause 'where' clause
     * @param args arguments
     * @return {@link IQuery.Select}
     */
    IQuery.Select selectDistinct(String whereClause, Object... args);

    /**
     * Select distinct with limit (top)
     * @param top the limit to return
     * @return {@link IQuery.Select}
     */
    IQuery.Select selectDistinct(int top);

    /**
     * Select distinct with limit (top) with the specified condition
     * @param top the limit to return
     * @param condition the condition
     * @return {@link IQuery.Select}
     */
    IQuery.Select selectDistinct(int top, String condition);

    /**
     * Select distinct with limit (top) with the specified condition
     * @param top the limit to return
     * @param whereClause the where clause
     * @param args arguments
     * @return {@link IQuery.Select}
     */
    IQuery.Select selectDistinct(int top, String whereClause, Object... args);
}
