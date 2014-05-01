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

import com.bingzer.android.dbv.queries.Select;
import com.bingzer.android.dbv.queries.Union;

/**
 * Created by Ricky Tobing on 8/16/13.
 */
public interface Unionable {

    /**
     * Union with other <code>SELECT</code> statement
     * @param select {@link com.bingzer.android.dbv.queries.Select} object
     * @return {@link com.bingzer.android.dbv.queries.Union} object
     */
    Union union(Select select);

    /**
     * Union All with other <code>SELECT</code> statement
     * @param select {@link com.bingzer.android.dbv.queries.Select} object
     * @return {@link com.bingzer.android.dbv.queries.Union} object
     */
    Union unionAll(Select select);
}
