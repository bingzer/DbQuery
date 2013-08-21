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

package com.bingzer.android.dbv.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.sqlite.ContentDeleteImpl;
import com.bingzer.android.dbv.sqlite.ContentInsertImpl;
import com.bingzer.android.dbv.sqlite.Utils;

import java.util.Collection;

/**
 *
 * Created by Ricky on 8/20/13.
 */
class Resolver implements IResolver {

    final IConfig config;
    final Uri uri;
    final ContentResolver contentResolver;

    Resolver(IConfig config, String uriString, Context context){
        this.contentResolver = context.getContentResolver();
        this.uri = Uri.parse(uriString);
        this.config = config;
    }

    @Override
    public IQuery.Delete delete(int id) {
        return delete(generateParamId(id));
    }

    @Override
    public IQuery.Delete delete(Collection<Integer> ids) {
        int[] idz = new int[ids.size()];
        int counter = 0;
        for (Integer id : ids) {
            idz[counter++] = id;
        }

        return delete(idz);
    }

    @Override
    public IQuery.Delete delete(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();

            whereClause.append(generateIdString()).append(" ");
            whereClause.append("IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return delete(whereClause.toString(), (Object)null);
        }
        else{
            // delete all
            return deleteAll();
        }
    }

    @Override
    public IQuery.Delete delete(String condition) {
        return delete(condition, (Object)null);
    }

    @Override
    public IQuery.Delete delete(String whereClause, Object... whereArgs) {
        return new ContentDeleteImpl() {
            @Override
            public Integer query() {
                return value();
            }
        }.val(contentResolver.delete(uri, whereClause, Util.toStringArray(whereArgs)));
    }

    @Override
    public IQuery.Delete deleteAll() {
        return delete("1 = 1");
    }

    @Override
    public IQuery.Delete delete(IEntity entity) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public <E extends IEntity> IQuery.Delete delete(IEntityList<E> entityList) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public IQuery.Insert insert(ContentValues contents) {
        return new ContentInsertImpl().val(contentResolver.insert(uri, contents));
    }

    @Override
    public IQuery.Insert insert(String[] columns, Object[] values) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            Utils.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return insert(contentValues);
    }

    @Override
    public IQuery.InsertWith insert(String... columns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IQuery.Insert insert(IEntity entity) {
        return null;
    }

    @Override
    public <E extends IEntity> IQuery.Insert insert(IEntityList<E> entityList) {
        return null;
    }

    @Override
    public IQuery.Select select(int top, String condition) {
        return null;
    }

    @Override
    public IQuery.Select select(String condition) {
        return null;
    }

    @Override
    public IQuery.Select select(int id) {
        return null;
    }

    @Override
    public IQuery.Select select(int... ids) {
        return null;
    }

    @Override
    public IQuery.Select select(String whereClause, Object... args) {
        return null;
    }

    @Override
    public IQuery.Select select(int top, String whereClause, Object... args) {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct() {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct(String condition) {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct(String whereClause, Object... args) {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct(int top) {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct(int top, String condition) {
        return null;
    }

    @Override
    public IQuery.Select selectDistinct(int top, String whereClause, Object... args) {
        return null;
    }

    @Override
    public IQuery.Update update(String column, Object value, int id) {
        return null;
    }

    @Override
    public IQuery.Update update(String column, Object value, String condition) {
        return null;
    }

    @Override
    public IQuery.Update update(String column, Object value, String whereClause, Object... whereArgs) {
        return null;
    }

    @Override
    public IQuery.Update update(String[] columns, Object[] values, String condition) {
        return null;
    }

    @Override
    public IQuery.Update update(String[] columns, Object[] values, String whereClause, Object... whereArgs) {
        return null;
    }

    @Override
    public IQuery.Update update(IEntity entity) {
        return null;
    }

    @Override
    public <E extends IEntity> IQuery.Update update(IEntityList<E> entityList) {
        return null;
    }

    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return null;
    }

    @Override
    public IQuery.Update update(ContentValues contents, String whereClause, Object... whereArgs) {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    IConfig getConfig(){
        return config;
    }

    String generateParamId(int id){
        return generateIdString() + " = " + id;
    }

    String generateIdString(){
        return config.getIdNamingConvention();
    }
}
