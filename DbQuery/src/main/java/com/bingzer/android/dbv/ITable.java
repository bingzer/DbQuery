package com.bingzer.android.dbv;

import java.util.List;

/**
 * Created by 11856 on 7/16/13.
 */
public interface ITable extends IQueryableTable {

    /**
     * Returns the name
     * @return
     */
    String getName();

    /**
     * Returns the column name
     * @return
     */
    List<String> getColumns();

    /**
     * Returns the column count
     * @return
     */
    int getColumnCount();

    /**
     * The model of this table
     */
    public static interface Model {

        /**
         * Returns the name of this table
         * @return
         */
        String getName();

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @return
         */
        Model add(String columnName, String dataType);

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @param columnDefinition
         * @return
         */
        Model add(String columnName, String dataType, String columnDefinition);

    }
}
