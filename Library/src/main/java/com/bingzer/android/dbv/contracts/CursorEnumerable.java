package com.bingzer.android.dbv.contracts;

import android.database.Cursor;

import com.bingzer.android.dbv.queries.IEnumerable;

public interface CursorEnumerable {

    void query(IEnumerable<Cursor> cursor);

}
