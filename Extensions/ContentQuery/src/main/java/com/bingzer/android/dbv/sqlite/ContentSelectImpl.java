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

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.Util;

/**
 * Created by Ricky on 8/20/13.
 */
public abstract class ContentSelectImpl extends QueryImpl.SelectImpl {

    String whereClause;
    Object[] whereArgs;

    public ContentSelectImpl(IConfig config, int top, String... returnedColumns) {
        super(config, null, top, false);
        this.columnString.delete(0, columnString.length());
        this.columnString.append(Util.join(", ", returnedColumns));
    }

    @Override
    public ContentSelectImpl where(String whereClause, Object... args){
        this.whereClause = whereClause;
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
        return whereClause;
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
     * @return
     */
    public String getSortingOrder(){
        StringBuilder sortingOrder = new StringBuilder();

        // without the 'ORDER BY'
        if(orderByString != null)
            sortingOrder.append(orderByString.substring(orderByString.indexOf("ORDER BY") + "ORDER BY".length()).trim());
        // add limit
        if(limitString != null && limitString.length() > 0){
            if(orderByString == null) sortingOrder.append(config.getIdNamingConvention());
            sortingOrder.append(" ").append(limitString);
        }

        return sortingOrder.toString();
    }
}
