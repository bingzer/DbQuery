package com.bingzer.android.dbv.sqlite;


import android.database.Cursor;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.ContentQuery;

public class PagingImpl implements IQuery.Paging, IQuery<Cursor> {

    private final int rowLimit;
    private final ContentQuery.Select select;
    private int pageNumber = 0;
    final IConfig config;

    PagingImpl(IConfig config, ContentQuery.Select select, int rowLimit){
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
    public Cursor query(){
        throw new UnsupportedOperationException();
    }

    @Override
    public Cursor query(int pageNumber) {
        ensurePageNumberValid(pageNumber);
        return query();
    }

    @Override
    public void query(IEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends IEntity> void query(IEntityList<E> entityList) {
        throw new UnsupportedOperationException();
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