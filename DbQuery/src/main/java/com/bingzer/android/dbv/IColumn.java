package com.bingzer.android.dbv;

/**
 * Represents a column
 *
 * Created by 11856 on 7/16/13.
 */
public interface IColumn {

    /**
     * Model to create a column
     */
    public static interface Model {

        /**
         * Returns the name of this column
         * @return
         */
        String getName();

        /**
         * Returns the data type
         * @return
         */
        String getDataType();

        /**
         * Returns the definition if any
         * @return
         */
        String getDefinition();
    }
}
