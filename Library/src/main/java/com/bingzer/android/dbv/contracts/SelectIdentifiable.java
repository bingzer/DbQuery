/**
 * Copyright 2014 Ricky Tobing
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

/**
 * Select and returns Id.
 * A table must follow {@link com.bingzer.android.dbv.IConfig#getIdNamingConvention()}
 * to make this work
 *
 * Created by Ricky Tobing on 7/20/13.
 *
 * @see com.bingzer.android.dbv.IConfig#getIdNamingConvention()
 */
public interface SelectIdentifiable {

    /**
     * Returns id with specified condition
     * @param condition the condition
     * @return the id that matches the <code>condition</code>
     */
    long selectId(String condition);

    /**
     * Returns id with specified condition
     * @param whereClause 'where' clause
     * @param args arguments
     * @return the id that matches the condition
     */
    long selectId(String whereClause, Object... args);

}
