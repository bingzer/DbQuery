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
package com.bingzer.android.dbv.content.resolvers;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.content.queries.Config;
import com.bingzer.android.dbv.content.queries.DeleteImpl;
import com.bingzer.android.dbv.content.queries.InsertImpl;
import com.bingzer.android.dbv.content.queries.InsertIntoImpl;
import com.bingzer.android.dbv.content.queries.UpdateImpl;
import com.bingzer.android.dbv.content.utils.UriUtils;
import com.bingzer.android.dbv.queries.Delete;
import com.bingzer.android.dbv.queries.InsertInto;
import com.bingzer.android.dbv.utils.CollectionUtils;
import com.bingzer.android.dbv.utils.ContentValuesUtils;
import com.bingzer.android.dbv.utils.Utils;
import com.bingzer.android.dbv.queries.Insert;
import com.bingzer.android.dbv.queries.Update;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
abstract class BaseResolver implements IBaseResolver {

    final Config config;
    final Uri uri;
    final ContentResolver contentResolver;

    BaseResolver(Config config, Uri uri, Context context){
        this.contentResolver = context.getContentResolver();
        this.uri = uri;
        this.config = config;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public String getPrimaryKeyColumn(){
        if(config.getAppendTableNameForId()){
            String tableName = "";
            Matcher m = getUriPattern().matcher(uri.toString());
            if(m.find()){
                tableName = m.group(3);
            }
            return tableName + config.getIdNamingConvention();
        }
        return config.getIdNamingConvention();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public long selectId(String condition) {
        return selectId(condition, (Object)null);
    }

    @Override
    public abstract long selectId(String whereClause, Object... args);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Delete delete(long id) {
        return delete(generateParamId(id));
    }

    @Override
    public Delete delete(Iterable<Long> ids) {
        long[] idz = new long[CollectionUtils.size(ids)];
        int counter = 0;
        for (long id : ids) {
            idz[counter++] = id;
        }

        return delete(idz);
    }

    @Override
    public Delete delete(long... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();

            whereClause.append(getPrimaryKeyColumn()).append(" ");
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
            return delete("1 = 1");
        }
    }

    @Override
    public Delete delete(String condition) {
        return delete(condition, (Object)null);
    }

    @Override
    public Delete delete(String whereClause, Object... whereArgs) {
        return new DeleteImpl() {
            @Override
            public Integer query() {
                return value();
            }
        }.val(contentResolver.delete(uri, whereClause, Utils.toStringArray(whereArgs)));
    }

    @Override
    public Delete delete(IEntity entity) {
        return delete(entity.getId());
    }

    @Override
    public <E extends IEntity> Delete delete(IEntityList<E> entityList) {
        long[] ids = new long[CollectionUtils.size(entityList)];
        int counter = 0;
        for(E entity : entityList){
            ids[counter++] = entity.getId();
        }
        return delete(ids);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Insert insert(String[] columns, Object[] values) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return insert(contentValues);
    }

    @Override
    public Insert insert(String columns, Object values) {
        final ContentValues contentValues = new ContentValues();
        ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, columns, values);

        return insert(contentValues);
    }

    @Override
    public InsertInto insertInto(String... columns) {
        return new InsertIntoImpl(new InsertIntoImpl.ContentSet() {
            @Override
            public void onContentValuesSet(InsertIntoImpl query, ContentValues contentValues) {
                query.setUri(contentResolver.insert(uri, contentValues));
            }
        }, columns);
    }

    @Override
    public Insert insert(ContentValues contents) {
        return new InsertImpl().setUri(contentResolver.insert(uri, contents));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Insert insert(IEntity entity) {
        // build content values..
        final Delegate.Mapper mapper = new Delegate.Mapper(this);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        Iterator<String> keys = mapper.keySet().iterator();
        String idString = getPrimaryKeyColumn();
        Delegate<Long> idSetter = null;
        while(keys.hasNext()){
            String key = keys.next();
            Delegate delegate = mapper.get(key);

            // ignore if column = "Id"
            if(key.equalsIgnoreCase(idString)) {
                idSetter = delegate;
            }
            else if(delegate != null){
                ContentValuesUtils.mapContentValuesFromDelegate(contentValues, key, delegate);
            }
        }

        Insert insert = insert(contentValues);
        // assign the newly inserted id
        if(idSetter != null){
            idSetter.set(insert.query());
        }

        return insert;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends IEntity> Insert insert(IEntityList<E> entityList) {
        if(config.getDefaultAuthority() == null)
            throw new IllegalArgumentException("Authority has not been set. Use ContentConfig.setDefaultAuthority() to set");

        final int listSize = CollectionUtils.size(entityList);
        final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        final Delegate[] idSetters = new Delegate[listSize];
        final String[] uriStrings = new String[listSize];

        int counter = 0;
        for(E entity : entityList){
            // build content values..
            final Delegate.Mapper mapper = new Delegate.Mapper(this);
            final ContentValues contentValues = new ContentValues();
            entity.map(mapper);

            Iterator<String> keys = mapper.keySet().iterator();
            String idString = getPrimaryKeyColumn();
            while(keys.hasNext()){
                String key = keys.next();
                Delegate delegate = mapper.get(key);

                // ignore if column = "Id"
                if(key.equalsIgnoreCase(idString)) {
                    idSetters[counter++] = delegate;
                }
                else if(delegate != null){
                    ContentValuesUtils.mapContentValuesFromDelegate(contentValues, key, delegate);
                }
            }

            ContentProviderOperation operation =
                    ContentProviderOperation.newInsert(uri)
                            .withValues(contentValues)
                            .build();
            operationList.add(operation);
        }

        try{
            ContentProviderResult[] results =
                    contentResolver.applyBatch(getConfig().getDefaultAuthority(), operationList);
            for(int i = 0; i < results.length; i++){
                idSetters[i].set(UriUtils.parseIdFromUri(results[i].uri));
                uriStrings[i] = results[i].uri.toString();
            }
        }
        catch (Exception e){
            throw new Error(e);
        }

        return new InsertImpl(){

            @Override
            public Long query(){
                return (long) uriStrings.length;
            }

            @Override
            public String toString(){
                return Utils.join(";", uriStrings);
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Update update(long id) {
        return update(generateParamId(id));
    }

    @Override
    public Update update(long... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(getPrimaryKeyColumn()).append(" ");
            whereClause.append(" IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return update(whereClause.toString());
        }
        else{
            // select all
            return update((String) null);
        }
    }

    @Override
    public Update update(String condition) {
        return update(condition, (Object) null);
    }

    @Override
    public Update update(final String whereClause, final Object... whereArgs) {
        return new UpdateImpl(new UpdateImpl.ContentSet(){
            @Override
            public void onContentValuesSet(UpdateImpl query, ContentValues contentValues) {
                query.setValue(update(contentValues, whereClause, whereArgs).query());
            }
        });
    }

    @Override
    public Update update(ContentValues contents, long id) {
        return update(contents, generateParamId(id), (Object)null);
    }

    @Override
    public Update update(ContentValues contents, String whereClause, Object... whereArgs) {
        UpdateImpl query = new UpdateImpl();
        String[] args = Utils.toStringArray(whereArgs);
        query.setValue(contentResolver.update(uri, contents, whereClause, args));
        return query;
    }

    @Override
    public Update update(IEntity entity) {
        if(entity.getId() < 0) throw new IllegalArgumentException("Id has to be over than 0");

        final Delegate.Mapper mapper = new Delegate.Mapper(this);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        for (String key : mapper.keySet()) {
            // ignore if "Id"
            if (key.equalsIgnoreCase(getPrimaryKeyColumn())) continue;

            Delegate delegate = mapper.get(key);
            if (delegate != null) {
                ContentValuesUtils.mapContentValuesFromDelegate(contentValues, key, delegate);
            }
        }

        return update(contentValues, entity.getId());
    }

    @Override
    public <E extends IEntity> Update update(IEntityList<E> entityList) {
        if(getConfig().getDefaultAuthority() == null)
            throw new IllegalArgumentException("Authority has not been set. Use IResolver.setDefaultAuthority() to set");

        final UpdateImpl query = new UpdateImpl();
        final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        for(E entity : entityList){
            // build content values..
            final Delegate.Mapper mapper = new Delegate.Mapper(this);
            final ContentValues contentValues = new ContentValues();
            entity.map(mapper);

            Iterator<String> keys = mapper.keySet().iterator();
            String idString = getPrimaryKeyColumn();
            while(keys.hasNext()){
                String key = keys.next();
                Delegate delegate = mapper.get(key);

                // ignore if column = "Id"
                if(!key.equalsIgnoreCase(idString)) {
                    ContentValuesUtils.mapContentValuesFromDelegate(contentValues, key, delegate);
                }
            }

            ContentProviderOperation operation =
                    ContentProviderOperation.newUpdate(uri)
                            .withValues(contentValues)
                            .withSelection(generateParamId(entity.getId()), null)
                            .build();
            operationList.add(operation);
        }

        try{
            int count = 0;
            ContentProviderResult[] results =
                    contentResolver.applyBatch(getConfig().getDefaultAuthority(), operationList);
            for(ContentProviderResult result : results){
                count += result.count;
            }
            query.setValue(count);
        }
        catch (Exception e){
            throw new Error(e);
        }

        return query;
    }

    @Override
    public boolean has(long id) {
        return has(generateParamId(id));
    }

    @Override
    public boolean has(String condition) {
        return has(condition, (Object) null);
    }

    @Override
    public abstract boolean has(String whereClause, Object... whereArgs);

    @Override
    public int count() {
        return count(null);
    }

    @Override
    public int count(String condition) {
        return count(condition, (Object)null);
    }

    @Override
    public abstract int count(String whereClause, Object... whereArgs);

    ///////////////////////////////////////////////////////////////////////////////////////////////

    String generateParamId(long id){
        return getPrimaryKeyColumn() + " = " + id;
    }

    Pattern getUriPattern(){
        return Pattern.compile("(content://)(.+)/(\\w+)/?((\\d+)|(#)|(\\*))?");
    }
}
