package com.bingzer.android.dbv;

/**
 * Migraton mode
 *
 * Created by 11856 on 7/16/13.
 */
public enum MigrationMode {
    DropIfExists,
    ErrorIfExists,
    UpgradeIfExists
}
