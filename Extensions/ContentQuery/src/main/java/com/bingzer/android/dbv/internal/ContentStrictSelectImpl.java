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
package com.bingzer.android.dbv.internal;

import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.content.IBaseResolver;
import com.bingzer.android.dbv.contracts.ContentStrictSelectable;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
public abstract class ContentStrictSelectImpl implements ContentStrictSelectable.Select, ContentStrictSelectable.Select.OrderBy{

    final IBaseResolver resolver;
    StringBuilder columnString;
    String orderByString;
    String whereString;
    Object[] whereArgs;

    public ContentStrictSelectImpl(IBaseResolver resolver){
        this.resolver = resolver;
        this.columnString = new StringBuilder();
        this.columnString.append(Util.join(", ", generateDefaultProjections()));
    }

    @Override
    public ContentStrictSelectImpl columns(String... columns) {
        columnString.delete(0, columnString.length());
        if(columns != null){
            columnString.append(Util.join(", ", columns));
        }
        else{
            columnString.append(Util.join(", ", generateDefaultProjections()));
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
        return orderByString;
    }

    String[] generateDefaultProjections(){
        String[] projections = resolver.getConfig().getDefaultProjections();
        for(int i = 0; i < projections.length; i++){
            if(projections[i].equals(resolver.getConfig().getIdNamingConvention())){
                projections[i] = resolver.generateIdString();
            }
        }

        return projections;
    }
}
