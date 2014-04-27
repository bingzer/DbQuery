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
import com.bingzer.android.dbv.internal.Database;
import com.bingzer.android.dbv.internal.Table;
import com.bingzer.android.dbv.queries.Distinguishable;
import com.bingzer.android.dbv.queries.Selectable;

/**
* Created by Ricky on 4/26/2014.
*/
public abstract class Join extends SelectImpl implements IQuery.InnerJoin, IQuery.OuterJoin, Selectable, Distinguishable {

    protected final Table table;
    protected StringBuilder joinBuilder;

    public Join(Table table, String joinType, String tableNameToJoin, String onClause){
        super( table);
        this.table = table;
        this.joinBuilder = new StringBuilder();

        if(onClause.toLowerCase().startsWith("on "))
            this.joinBuilder.append(Database.SPACE).append(joinType).append(Database.SPACE)
                    .append(tableNameToJoin).append(Database.SPACE).append(onClause);
        else
            this.joinBuilder.append(Database.SPACE).append(joinType).append(Database.SPACE)
                    .append(tableNameToJoin).append(" ON ").append(onClause);
    }

    @Override
    public Select select(int top, String condition) {
        consume(table.select(top, condition));
        return this;
    }

    @Override
    public Select select(String condition) {
        consume(table.select(condition));
        return this;
    }

    @Override
    public Select select(int id) {
        consume(table.select(id));
        return this;
    }

    @Override
    public Select select(int... ids) {
        consume(table.select(ids));
        return this;
    }

    @Override
    public Select select(String whereClause, Object... args) {
        consume(table.select(whereClause, args));
        return this;
    }

    @Override
    public Select select(int top, String whereClause, Object... args) {
        consume(table.select(top, whereClause, args));
        return this;
    }

    @Override
    public Select selectDistinct() {
        consume(table.selectDistinct(null));
        return this;
    }

    @Override
    public Select selectDistinct(String condition) {
        consume(table.selectDistinct(condition));
        return this;
    }

    @Override
    public Select selectDistinct(String whereClause, Object... args) {
        consume(table.selectDistinct(whereClause, args));
        return this;
    }

    @Override
    public Select selectDistinct(int top) {
        consume(table.selectDistinct(top));
        return this;
    }

    @Override
    public Select selectDistinct(int top, String condition) {
        consume(table.selectDistinct(top, condition));
        return this;
    }

    @Override
    public Select selectDistinct(int top, String whereClause, Object... args) {
        consume(table.selectDistinct(top, whereClause, args));
        return this;
    }

    @Override
    public String toString(){
        StringBuilder sql = new StringBuilder();
        sql.append(selectString);
        sql.append(columnString).append(Database.SPACE);
        sql.append(fromString).append(Database.SPACE);
        // join builder
        sql.append(joinBuilder).append(Database.SPACE);
        // where
        sql.append(whereString).append(Database.SPACE);

        // group by + having
        if(groupByString != null) sql.append(Database.SPACE).append(groupByString);
        if(havingString != null) sql.append(Database.SPACE).append(havingString);
        // order by
        if(orderByString != null) sql.append(Database.SPACE).append(orderByString);
        // limit
        if(limitString != null) sql.append(Database.SPACE).append(limitString);

        return sql.toString();
    }

    @Override
    public InnerJoin join(String tableName, String onClause) {
        if(onClause.toLowerCase().startsWith("on "))
            this.joinBuilder.append(Database.SPACE).append("INNER JOIN").append(Database.SPACE)
                    .append(tableName).append(Database.SPACE).append(onClause);
        else
            this.joinBuilder.append(Database.SPACE).append("INNER JOIN").append(Database.SPACE)
                    .append(tableName).append(" ON ").append(onClause);
        return this;
    }

    @Override
    public InnerJoin join(String tableName, String column1, String column2) {
        return join(tableName, column1 + " = " + column2);
    }

    @Override
    public OuterJoin outerJoin(String tableName, String onClause) {
        if(onClause.toLowerCase().startsWith("on "))
            this.joinBuilder.append(Database.SPACE).append("OUTER JOIN").append(Database.SPACE)
                    .append(tableName).append(Database.SPACE).append(onClause);
        else
            this.joinBuilder.append(Database.SPACE).append("OUTER JOIN").append(Database.SPACE)
                    .append(tableName).append(" ON ").append(onClause);
        return this;
    }

    @Override
    public OuterJoin outerJoin(String tableName, String column1, String column2) {
        return outerJoin(tableName, column1 + " = " + column2);
    }

    private void consume(Select select){
        // consume
        selectString = ((SelectImpl)select).selectString;
        columnString = ((SelectImpl)select).columnString;
        fromString = ((SelectImpl)select).fromString;
        orderByString = ((SelectImpl)select).orderByString;
        limitString = ((SelectImpl)select).limitString;
        groupByString = ((SelectImpl)select).groupByString;
        havingString = ((SelectImpl)select).havingString;
        whereString = ((SelectImpl)select).whereString;
    }
}
