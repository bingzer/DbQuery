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

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.Util;
import com.bingzer.android.dbv.sqlite.*;

import java.net.CacheRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * Created by Ricky on 8/20/13.
 */
public class Resolver implements IResolver {

    final ContentConfig config;
    final Uri uri;
    final ContentResolver contentResolver;

    public Resolver(ContentConfig config, Uri uri, Context context){
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
    public ContentConfig getConfig() {
        return config;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

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
            return delete("1 = 1");
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
    public IQuery.Delete delete(IEntity entity) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public <E extends IEntity> IQuery.Delete delete(IEntityList<E> entityList) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public IQuery.Insert insert(String[] columns, Object[] values) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentUtils.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }

        return insert(contentValues);
    }

    @Override
    public IQuery.InsertWith insert(String... columns) {
        return new ContentInsertWithImpl(new ContentInsertWithImpl.ContentSet() {
            private ContentValues contentValues;
            private ContentInsertWithImpl query;
            private int value = -1;

            @Override
            public void onContentValuesSet(ContentInsertWithImpl query, ContentValues contentValues) {
                this.query = query;
                this.contentValues = contentValues;
                this.query.setUri(contentResolver.insert(uri, contentValues));
            }

            @Override
            public Integer query() {
                if(contentValues == null)
                    throw new IllegalArgumentException("ContentValues are not specified. Use IQuery.InsertWith.setUri()");
                // return
                return value;
            }
        }, columns);
    }

    @Override
    public IQuery.Insert insert(ContentValues contents) {
        return new ContentInsertImpl().setUri(contentResolver.insert(uri, contents));
    }

    @SuppressWarnings("unchecked")
    @Override
    public IQuery.Insert insert(IEntity entity) {
        // build content values..
        final EntityMapper mapper = new EntityMapper(config);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        Iterator<String> keys = mapper.keySet().iterator();
        String idString = generateIdString();
        IEntity.Action<Integer> idSetter = null;
        while(keys.hasNext()){
            String key = keys.next();
            IEntity.Action action = mapper.get(key);

            // ignore if column = "Id"
            if(key.equalsIgnoreCase(idString)) {
                idSetter = action;
            }
            else if(action != null){
                ContentUtils.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        IQuery.Insert insert = insert(contentValues);
        // assign the newly inserted id
        if(idSetter != null){
            idSetter.set(insert.query());
        }

        return insert;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends IEntity> IQuery.Insert insert(IEntityList<E> entityList) {
        if(config.getDefaultAuthority() == null)
            throw new IllegalArgumentException("Authority has not been set. Use ContentConfig.setDefaultAuthority() to set");

        final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        final IEntity.Action[] idSetters = new IEntity.Action[entityList.getEntityList().size()];
        final String[] uriStrings = new String[entityList.getEntityList().size()];

        for(int i = 0; i < entityList.getEntityList().size(); i++){
            final IEntity entity = entityList.getEntityList().get(i);
            // build content values..
            final EntityMapper mapper = new EntityMapper(config);
            final ContentValues contentValues = new ContentValues();
            entity.map(mapper);

            Iterator<String> keys = mapper.keySet().iterator();
            String idString = generateIdString();
            while(keys.hasNext()){
                String key = keys.next();
                IEntity.Action action = mapper.get(key);

                // ignore if column = "Id"
                if(key.equalsIgnoreCase(idString)) {
                    idSetters[i] = action;
                }
                else if(action != null){
                    ContentUtils.mapContentValuesFromAction(contentValues, key, action);
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
                idSetters[i].set(UriUtil.parseIdFromUri(results[i].uri));
                uriStrings[i] = results[i].uri.toString();
            }
        }
        catch (Exception e){
            throw new Error(e);
        }

        return new ContentInsertImpl(){

            @Override
            public Integer query(){
                return uriStrings.length;
            }

            @Override
            public String toString(){
                return Util.join(";", uriStrings);
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int selectId(String condition) {
        return selectId(condition, (Object)null);
    }

    @Override
    public int selectId(String whereClause, Object... args) {
        int id = -1;
        Cursor cursor = null;
        try{
            cursor = select(whereClause, args).query();
            if(cursor.moveToNext()){
                int index = cursor.getColumnIndex(config.getIdNamingConvention());
                id = cursor.getInt(index);
            }
        }
        finally {
            if(cursor != null) cursor.close();
        }

        return id;
    }

    @Override
    public ContentQuery.Select select(int id) {
        return select(generateParamId(id));
    }

    @Override
    public ContentQuery.Select select(String condition) {
        return select(-1, condition);
    }

    @Override
    public ContentQuery.Select select(int top, String condition) {
        return select(top, condition, (Object)null);
    }

    @Override
    public ContentQuery.Select select(int... ids) {
        if(ids != null && ids.length > 0){
            StringBuilder whereClause = new StringBuilder();
            whereClause.append(generateIdString()).append(" ");
            whereClause.append(" IN (");
            for(int i = 0; i < ids.length; i++){
                whereClause.append(ids[i]);
                if(i < ids.length - 1){
                    whereClause.append(",");
                }
            }
            whereClause.append(")");

            return select(whereClause.toString());
        }
        else{
            // select all
            return select((String)null);
        }
    }

    @Override
    public ContentQuery.Select select(String whereClause, Object... args) {
        return select(-1, whereClause, args);
    }

    @Override
    public ContentQuery.Select select(final int top, final String whereClause, final Object... args) {
        return new ContentSelectImpl(config, top) {
            @Override
            public Cursor query() {
                String[] projections = getProjections();
                String selection = getSelection();
                String[] selectionArgs = getSelectionArgs();
                String sortOrder = getSortingOrder();
                return contentResolver.query(uri, projections, selection, selectionArgs, sortOrder);
            }

            @Override
            public void query(IEntity entity) {
                final Cursor cursor = query();
                final EntityMapper mapper = new EntityMapper(config);

                ContentUtils.mapEntityFromCursor(mapper, entity, cursor);

                cursor.close();
            }

            @Override
            @SuppressWarnings("unchecked")
            public <E extends IEntity> void query(IEntityList<E> entityList) {
                final Cursor cursor = query();
                final EntityMapper mapper = new EntityMapper(config);

                ContentUtils.mapEntityListFromCursor(mapper, entityList, cursor, config.getIdNamingConvention());

                cursor.close();
            }
        }.where(whereClause, args);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IQuery.Update update(String column, Object value, int id) {
        return update(column, value, generateParamId(id));
    }

    @Override
    public IQuery.Update update(String column, Object value, String condition) {
        return update(column, value, condition, (Object)null);
    }

    @Override
    public IQuery.Update update(String column, Object value, String whereClause, Object... whereArgs) {
        return update(new String[]{column}, new Object[]{ value }, whereClause, whereArgs);
    }

    @Override
    public IQuery.Update update(String[] columns, Object[] values, String condition) {
        return update(columns, values, condition, (Object)null);
    }

    @Override
    public IQuery.Update update(String[] columns, Object[] values, String whereClause, Object... whereArgs) {
        final ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columns.length; i++){
            ContentUtils.mapContentValuesFromGenericObject(contentValues, columns[i], values[i]);
        }
        return update(contentValues, whereClause, whereArgs);
    }

    @Override
    public IQuery.Update update(ContentValues contents, int id) {
        return update(contents, generateParamId(id), (Object)null);
    }

    @Override
    public IQuery.Update update(ContentValues contents, String whereClause, Object... whereArgs) {
        ContentUpdateImpl query = new ContentUpdateImpl();
        String[] args = Util.toStringArray(whereArgs);
        query.val(contentResolver.update(uri, contents, whereClause, args));
        return query;
    }

    @Override
    public IQuery.Update update(IEntity entity) {
        if(entity.getId() < 0) throw new IllegalArgumentException("Id has to be over than 0");

        final EntityMapper mapper = new EntityMapper(config);
        final ContentValues contentValues = new ContentValues();
        entity.map(mapper);

        for (String key : mapper.keySet()) {
            // ignore if "Id"
            if (key.equalsIgnoreCase(generateIdString())) continue;

            IEntity.Action action = mapper.get(key);
            if (action != null) {
                ContentUtils.mapContentValuesFromAction(contentValues, key, action);
            }
        }

        return update(contentValues, entity.getId());
    }

    @Override
    public <E extends IEntity> IQuery.Update update(IEntityList<E> entityList) {
        if(getConfig().getDefaultAuthority() == null)
            throw new IllegalArgumentException("Authority has not been set. Use IResolver.setDefaultAuthority() to set");

        final ContentUpdateImpl query = new ContentUpdateImpl();
        final ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        for(int i = 0; i < entityList.getEntityList().size(); i++){
            final IEntity entity = entityList.getEntityList().get(i);
            // build content values..
            final EntityMapper mapper = new EntityMapper(config);
            final ContentValues contentValues = new ContentValues();
            entity.map(mapper);

            Iterator<String> keys = mapper.keySet().iterator();
            String idString = generateIdString();
            while(keys.hasNext()){
                String key = keys.next();
                IEntity.Action action = mapper.get(key);

                // ignore if column = "Id"
                if(!key.equalsIgnoreCase(idString)) {
                    ContentUtils.mapContentValuesFromAction(contentValues, key, action);
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
            query.val(count);
        }
        catch (Exception e){
            throw new Error(e);
        }

        return query;
    }

    @Override
    public boolean has(int id) {
        return has(generateParamId(id));
    }

    @Override
    public boolean has(String condition) {
        return has(condition, (Object) null);
    }

    @Override
    public boolean has(String whereClause, Object... whereArgs) {
        Cursor cursor = null;
        try{
            cursor = select(whereClause, whereArgs).columns(config.getIdNamingConvention()).query();
            return cursor.getCount() > 0;
        }
        finally {
            if(cursor != null) cursor.close();
        }
    }

    @Override
    public int count() {
        return count(null);
    }

    @Override
    public int count(String condition) {
        return count(condition, (Object)null);
    }

    @Override
    public int count(String whereClause, Object... whereArgs) {
        int count = -1;
        Cursor cursor = null;
        try{
            cursor = select(whereClause, whereArgs).columns(config.getIdNamingConvention()).query();
            count = cursor.getCount();
        }
        finally {
            if(cursor != null)
                cursor.close();
        }

        return count;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    String generateParamId(int id){
        return generateIdString() + " = " + id;
    }

    String generateIdString(){
        return config.getIdNamingConvention();
    }
}
