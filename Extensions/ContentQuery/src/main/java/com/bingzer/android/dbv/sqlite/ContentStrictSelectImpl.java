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

import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.queries.ContentStrictSelectable;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
public abstract class ContentStrictSelectImpl implements ContentStrictSelectable.Select, ContentStrictSelectable.Select.OrderBy{

    final ContentConfig config;
    StringBuilder columnString;
    StringBuilder limitString;
    String orderByString;
    String whereString;
    Object[] whereArgs;

    public ContentStrictSelectImpl(ContentConfig config, int top){
        this.config = config;
        this.columnString = new StringBuilder();
        this.columnString.append(Util.join(", ", config.getDefaultProjections()));

        if(top > 0) {
            limitString = new StringBuilder();
            limitString.append(" LIMIT ").append(top);
        }
    }

    @Override
    public ContentStrictSelectImpl columns(String... columns) {
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
        orderByString = Util.join(",", columns);
        return this;
    }

    public ContentStrictSelectImpl where(String whereClause, Object... args){
        this.whereString = whereClause;
        this.whereArgs = args;
        return this;
    }

    /**
     * This is columns
     * @return array of columns names
     */
    public String[] getProjections(){
        String[] projections =  columnString.toString().split(",");
        for(int i = 0; i < projections.length; i++){
            projections[i] = projections[i].trim();
        }
        return projections;
    }

    /**
     * This is the where string
     * @return where clause
     */
    public String getSelection(){
        return whereString;
    }

    /**
     * Where args
     * @return selection args
     */
    public String[] getSelectionArgs(){
        return Util.toStringArray(whereArgs);
    }

    /**
     * Order by
     * @return sorting order as string
     */
    public String getSortingOrder(){
        StringBuilder sortingOrder = new StringBuilder();

        // without the 'ORDER BY'
        if(orderByString != null) sortingOrder.append(orderByString);

        // add limit
        if(limitString != null && limitString.length() > 0){
            if(orderByString == null) sortingOrder.append(config.getIdNamingConvention());
            sortingOrder.append(" ").append(limitString);
        }

        return sortingOrder.toString();
    }

}
