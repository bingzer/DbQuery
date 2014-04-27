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

import com.bingzer.android.dbv.queries.Paging;

/**
 * Represents a pagination on a query
 *
 * Created by Ricky on 8/11/13.
 */
public interface Pagination {

    /**
     * Added paging to a query.
     *
     * @see com.bingzer.android.dbv.queries.Paging
     * @param row the row number
     * @return paging
     */
    Paging paging(int row);

}
