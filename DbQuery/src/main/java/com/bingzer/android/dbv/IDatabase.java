package com.bingzer.android.dbv;

import java.util.List;

/**
 * Represents the database
 *
 * Created by 11856 on 7/16/13.
 */
public interface IDatabase {


    /**
     * Returns the name of this database
     * @return
     */
    String getName();

    /**
     * Returns the version of this database
     * @return
     */
    int getVersion();

    /**
     * Returns all tables in this database
     * @return
     */
    List<ITable> getTables();

    /**
     * Returns the table
     * @param tableName
     * @return
     */
    ITable get(String tableName);

    /**
     * Create the database
     * @param version
     * @param builder
     */
    void create(int version, Builder builder);

    /**
     * Interface that 'builds' database
     */
    public static interface Builder {

        /**
         * Returns the mode.
         * @return
         */
        MigrationMode getMode();

        /**
         * Called when database is about to create.
         * You should define all the table models here
         * @param modeling
         */
        void onCreate(Modeling modeling);

    }

    /**
     * Modeling that's used to query tables/column definition
     */
    public static interface Modeling {

        /**
         * Adds a table model
         * @param tableName
         * @return
         */
        ITable.Model add(String tableName);
    }
}
