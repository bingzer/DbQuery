package com.bingzer.android.dbv.sqlite;


import android.database.Cursor;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.impl.EntityMapper;

class PagingImpl implements IQuery.Paging, IQuery<Cursor> {

    private final int rowLimit;
    private final ContentSelectImpl select;
    private int pageNumber = 0;
    final IConfig config;

    PagingImpl(IConfig config, ContentSelectImpl select, int rowLimit){
        this.config = config;
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
        Cursor cursor = select.query();
        float row = cursor.getCount();
        cursor.close();

        // calculate total page
        return  (int) Math.ceil(row / (float) rowLimit);
    }

    @Override
    public Cursor query(int pageNumber) {
        ensurePageNumberValid(pageNumber);
        return query();
    }

    @Override
    public Cursor query(){
        select.limitString = new StringBuilder();
        select.limitString.append(" LIMIT ").append(rowLimit).append(" OFFSET ").append(getOffset());
        return select.query();
    }

    @Override
    public void query(IEntity entity) {
        final Cursor cursor = query();
        final IEntity.Mapper mapper = new com.bingzer.android.dbv.content.impl.EntityMapper(config);

        ContentUtils.mapEntityFromCursor(mapper, entity, cursor);

        cursor.close();
    }

    @Override
    public <E extends IEntity> void query(IEntityList<E> entityList) {
        final Cursor cursor = query();
        final IEntity.Mapper mapper = new EntityMapper(config);

        ContentUtils.mapEntityListFromCursor(mapper, entityList, cursor, config.getIdNamingConvention());

        cursor.close();
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