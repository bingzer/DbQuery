/**
 * Copyright 2014 Ricky Tobing
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
package com.bingzer.android.dbv.internal.impl;

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.internal.Table;
import com.bingzer.android.dbv.queries.Distinguishable;

/**
* Created by Ricky on 4/26/2014.
*/
public abstract class UnionImpl extends SelectImpl implements IQuery.Union, Distinguishable {
    Select firstSelect;
    Select secondSelect;
    boolean unionAll;

    public UnionImpl(Select firstSelect, Table table){
        this(firstSelect, table, false);
    }

    public UnionImpl(Select firstSelect, Table table, boolean unionAll){
        super(table);
        this.unionAll = unionAll;
        this.firstSelect = firstSelect;
    }

    @Override
    public Select columns(String... columns) {
        secondSelect.columns(columns);
        return this;
    }

    @Override
    public Select select(int top, String condition) {
        secondSelect = table.select(top, condition);
        return this;
    }

    @Override
    public Select select(String condition) {
        secondSelect = table.select(condition);
        return this;
    }

    @Override
    public Select select(int id) {
        secondSelect = table.select(id);
        return this;
    }

    @Override
    public Select select(int... ids) {
        secondSelect = table.select(ids);
        return this;
    }

    @Override
    public Select select(String whereClause, Object... args) {
        secondSelect = table.select(whereClause, args);
        return this;
    }

    @Override
    public Select select(int top, String whereClause, Object... args) {
        secondSelect = table.select(top, whereClause, args);
        return this;
    }

    @Override
    public Select selectDistinct() {
        secondSelect = table.selectDistinct();
        return this;
    }

    @Override
    public Select selectDistinct(String condition) {
        secondSelect = table.selectDistinct(condition);
        return this;
    }

    @Override
    public Select selectDistinct(String whereClause, Object... args) {
        secondSelect = table.selectDistinct(whereClause, args);
        return this;
    }

    @Override
    public Select selectDistinct(int top) {
        secondSelect = table.selectDistinct(top);
        return this;
    }

    @Override
    public Select selectDistinct(int top, String condition) {
        secondSelect = table.selectDistinct(top, condition);
        return this;
    }

    @Override
    public Select selectDistinct(int top, String whereClause, Object... args) {
        secondSelect = table.selectDistinct(top, whereClause, args);
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(firstSelect) + " UNION " + (unionAll ? " ALL " : "") + secondSelect;
    }
}
