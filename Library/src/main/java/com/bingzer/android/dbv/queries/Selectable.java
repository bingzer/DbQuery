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

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Selectable {

    /**
     * Select top (x) add the specified condition
     * @param top
     * @param condition
     * @return
     */
    IQuery.Select select(int top, String condition);

    /**
     * Select some condition
     * @param condition
     * @return
     */
    IQuery.Select select(String condition);

    /**
     * Select add id
     * @param id
     * @return
     */
    IQuery.Select select(int id);

    /**
     * Select multiple ids
     * @param ids
     * @return
     */
    IQuery.Select select(int... ids);

    /**
     * Select add whereClause
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select select(String whereClause, Object... args);

    /**
     * Select
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select select(int top, String whereClause, Object... args);

    /**
     * Select distinct all.
     * Equivalent of calling <code>selectDistinct(null)</code>
     * @return
     */
    IQuery.Select selectDistinct();

    /**
     * Select distinct
     * @param condition
     * @return
     */
    IQuery.Select selectDistinct(String condition);

    /**
     * Select distinct add condition
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select selectDistinct(String whereClause, Object... args);

}
