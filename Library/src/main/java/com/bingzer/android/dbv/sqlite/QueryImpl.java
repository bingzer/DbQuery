/**
 * Copyright 2013 Ricky Tobing
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

package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.Selectable;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
abstract class QueryImpl<T> implements IQuery<T> {

    IConfig config;

    QueryImpl(IConfig config){
        this.config = config;
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    @Override
    public abstract T query();

    ////////////////////////////////////////////
    ////////////////////////////////////////////


    static abstract class SelectImpl extends QueryImpl<Cursor> implements IQuery.Select, Select.OrderBy, GroupBy, Having{

        final Table table;
        StringBuilder selectString;
        StringBuilder columnString;
        StringBuilder fromString;
        StringBuilder limitString;
        StringBuilder orderByString;
        StringBuilder groupByString;
        StringBuilder havingString;
        StringBuilder whereString;

        SelectImpl(IConfig config, Table table){
            this(config, table, false);
        }

        SelectImpl(IConfig config, Table table, boolean distinct){
            this(config, table, -1, distinct);
        }

        SelectImpl(IConfig config, Table table, int top, boolean distinct){
            super(config);

            this.table = table;
            this.selectString = new StringBuilder("SELECT ");
            this.columnString = new StringBuilder("* ");
            this.fromString = new StringBuilder("FROM ").append(table);
            this.limitString = new StringBuilder();
            this.whereString = new StringBuilder();

            if(distinct) selectString.append("DISTINCT ");
            if(top > 0) {
                limitString = new StringBuilder();
                limitString.append(" LIMIT ").append(top);
            }
        }

        SelectImpl where(String whereClause, Object... args){
            if(whereClause != null){
                // append where if necessary
                if(!whereClause.toLowerCase().startsWith("where"))
                    whereString.append(" WHERE ");
                // safely prepare the where part
                whereString.append(Util.bindArgs(whereClause, args));
            }
            return this;
        }

        @Override
        public Select columns(String... columns) {
            columnString.delete(0, columnString.length());
            if(columns != null){
                columnString.append(Util.join(", ", columns));
            }
            else{
                columnString.append("*");
            }

            return this;
        }

        @Override
        public OrderBy orderBy(String... columns) {
            if(orderByString == null) orderByString = new StringBuilder();
            else orderByString.delete(0, orderByString.length());
            if(columns != null){
                orderByString.append("ORDER BY ").append(Util.join(",", columns));
            }
            return this;
        }

        @Override
        public void query(IEntity entity) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(table);

            ContentUtil.mapEntityFromCursor(mapper, entity, cursor);

            cursor.close();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(table);

            ContentUtil.mapEntityListFromCursor(mapper, entityList, cursor);

            cursor.close();
        }

        @Override
        public Paging paging(int row) {
            return new PagingImpl(config, this, row);
        }

        @Override
        public GroupBy groupBy(String... columns) {
            if(groupByString == null) groupByString = new StringBuilder();
            else groupByString.delete(0, groupByString.length());
            if(columns != null){
                groupByString.append("GROUP BY ").append(Util.join(",", columns));
            }

            return this;
        }

        @Override
        public Having having(String condition) {
            return having(condition, (Object) null);
        }

        @Override
        public Having having(String clause, Object... args) {
            if(havingString == null) havingString = new StringBuilder();
            else havingString.delete(0, havingString.length());
            if(clause != null){
                havingString.append("HAVING ").append(Util.bindArgs(clause, args));
            }

            return this;
        }

        @Override
        public String toString(){
            StringBuilder sql = new StringBuilder();
            sql.append(selectString);
            sql.append(columnString).append(Database.SPACE);
            sql.append(fromString).append(Database.SPACE);
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
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static class InsertImpl implements IQuery.Insert {
        Integer value;

        @Override
        public Integer query() {
            return value;
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static class InsertWithImpl extends InsertImpl implements InsertWith {

        IQuery<Integer> query;
        String[] columnNames;

        InsertWithImpl(ContentSet query, String... columnNames){
            this.query = query;
            this.columnNames = columnNames;
        }

        @Override
        public IQuery<Integer> val(Object... values) {
            ContentValues contentValues = new ContentValues();
            for(int i = 0; i < columnNames.length; i++){
                ContentUtil.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
            }

            ((ContentSet) query).onContentValuesSet(this, contentValues);

            return this;
        }

        static interface ContentSet extends IQuery<Integer> {

            void onContentValuesSet(InsertWithImpl query, ContentValues contentValues);

        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static class UpdateImpl implements IQuery.Update {
        Integer value;

        @Override
        public Integer query() {
            return value;
        }
    }


    static class DeleteImpl implements IQuery.Delete {
        Integer value;

        @Override
        public Integer query() {
            return value;
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static class DropImpl implements IQuery<Boolean>{
        Boolean value;

        @Override
        public Boolean query() {
            return value;
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static abstract class InnerJoinImpl extends Join implements IQuery.InnerJoin {
        InnerJoinImpl(IConfig config, Table table, String tableNameToJoin, String onClause) {
            super(config, table, "INNER JOIN", tableNameToJoin, onClause);
        }
    }

    static abstract class OuterJoinImpl extends Join implements IQuery.OuterJoin {
        OuterJoinImpl(IConfig config, Table table, String tableNameToJoin, String onClause) {
            super(config, table, "OUTER JOIN", tableNameToJoin, onClause);
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    private static abstract class Join extends SelectImpl implements IQuery.InnerJoin, IQuery.OuterJoin, Selectable {

        protected final Table table;
        protected StringBuilder joinBuilder;

        Join(IConfig config, Table table, String joinType, String tableNameToJoin, String onClause){
            super(config, table);
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
        public IQuery.Select select(int top, String condition) {
            consume(table.select(top, condition));
            return this;
        }

        @Override
        public IQuery.Select select(String condition) {
            consume(table.select(condition));
            return this;
        }

        @Override
        public IQuery.Select select(int id) {
            consume(table.select(id));
            return this;
        }

        @Override
        public IQuery.Select select(int... ids) {
            consume(table.select(ids));
            return this;
        }

        @Override
        public Select selectDistinct(int top) {
            consume(table.select(top));
            return this;
        }

        @Override
        public Select selectDistinct(int top, String condition) {
            consume(table.select(top, condition));
            return this;
        }

        @Override
        public Select selectDistinct(int top, String whereClause, Object... args) {
            consume(table.select(top, whereClause, args));
            return this;
        }

        @Override
        public IQuery.Select select(String whereClause, Object... args) {
            consume(table.select(whereClause, args));
            return this;
        }

        @Override
        public IQuery.Select select(int top, String whereClause, Object... args) {
            consume(table.select(top, whereClause, args));
            return this;
        }

        @Override
        public Select selectDistinct() {
            consume(table.selectDistinct(null));
            return this;
        }

        @Override
        public IQuery.Select selectDistinct(String condition) {
            consume(table.selectDistinct(condition));
            return this;
        }

        @Override
        public IQuery.Select selectDistinct(String whereClause, Object... args) {
            consume(table.selectDistinct(whereClause, args));
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

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static abstract class UnionImpl extends SelectImpl implements Union {
        Select firstSelect;
        Select secondSelect;
        boolean unionAll;

        UnionImpl(Select firstSelect, Table table){
            this(firstSelect, table, false);
        }

        UnionImpl(Select firstSelect, Table table, boolean unionAll){
            super(table.getConfig(), table);
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

            final StringBuilder sql = new StringBuilder();
            sql.append(firstSelect);
            sql.append(" UNION ").append(unionAll ? " ALL " : "");
            sql.append(secondSelect);

            return sql.toString();
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    static class PagingImpl extends QueryImpl<Cursor> implements Paging{

        private final int rowLimit;
        private final SelectImpl select;
        private int pageNumber = 0;

        PagingImpl(IConfig config, SelectImpl select, int rowLimit){
            super(config);
            this.select = select;
            this.rowLimit = rowLimit;
        }

        @Override
        public int getRowLimit() {
            return rowLimit;
        }

        @Override
        public int getPageNumber() {
            return pageNumber;
        }

        @Override
        public void setPageNumber(int pageNumber) {
            ensurePageNumberValid(pageNumber);
        }

        @Override
        public int getTotalPage() {
            // we can't count row when orderBy or having is included
            // TODO: there's gotta be a way to do this
            if(select.groupByString != null || select.havingString != null)
                throw new UnsupportedOperationException("Cannot count row when querying using 'GroupBy' or 'Having'");

            float row = -1;

            String sql = generateSql(true);
            Cursor cursor = null;
            try{
                cursor = select.table.raw(sql).query();
                if(cursor.moveToFirst()){
                    row = cursor.getInt(0);
                }
            }
            finally {
                if(cursor != null) cursor.close();
            }

            // calculate total page
            return  (int) Math.ceil(row / (float) rowLimit);
        }

        @Override
        public Cursor query(){
            Cursor cursor = null;
            try{
                cursor = select.table.raw(toString()).query();
                return cursor;
            }
            finally {
                if (cursor != null && cursor.getCount() > 0)
                    pageNumber++;
            }
        }

        @Override
        public Cursor query(int pageNumber) {
            ensurePageNumberValid(pageNumber);
            return query();
        }

        @Override
        public void query(IEntity entity) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(select.table);

            ContentUtil.mapEntityFromCursor(mapper, entity, cursor);

            cursor.close();
        }

        @Override
        public <E extends IEntity> void query(IEntityList<E> entityList) {
            final Cursor cursor = query();
            final EntityMapper mapper = new EntityMapper(select.table);

            ContentUtil.mapEntityListFromCursor(mapper, entityList, cursor);

            cursor.close();
        }

        @Override
        public String toString(){
            return generateSql(false);
        }

        String generateSql(boolean asRowCount){
            StringBuilder sql = new StringBuilder();

            sql.append(select.selectString).append(Database.SPACE);

            // columns
            if(asRowCount) sql.append(" COUNT(*) AS FN ");
            else sql.append(select.columnString).append(Database.SPACE);
            // from
            sql.append(select.fromString).append(Database.SPACE);
            // join builder
            if(select instanceof Join){
                sql.append(((Join)select).joinBuilder).append(Database.SPACE);
            }
            // where
            sql.append(Database.SPACE).append(select.whereString);

            // group by + having (Only when not to count)
            if(select.groupByString != null) sql.append(Database.SPACE).append(select.groupByString);
            if(select.havingString != null) sql.append(Database.SPACE).append(select.havingString);

            // order by
            if(select.orderByString != null) sql.append(Database.SPACE).append(select.orderByString);

            // pagination only when it's not a row count
            if(!asRowCount){
                sql.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
            }

            return sql.toString();
        }

        int getOffset(){
            return pageNumber * rowLimit;
        }

        void ensurePageNumberValid(int pageNumber){
            if(pageNumber < 0)
                throw new IllegalArgumentException("PageNumber must be over 0");
            this.pageNumber = pageNumber;
        }
    }
}
