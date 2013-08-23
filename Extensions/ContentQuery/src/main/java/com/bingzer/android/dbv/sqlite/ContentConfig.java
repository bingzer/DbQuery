package com.bingzer.android.dbv.sqlite;

import com.bingzer.android.dbv.IConfig;

/**
 * Created by Ricky on 8/22/13.
 */
public class ContentConfig extends Config {

    public ContentConfig(){
        setIdNamingConvention("_id");
    }

    public ContentConfig(IConfig config){
        if(config != null){
            setAppendTableNameForId(config.getAppendTableNameForId());
            setDebug(config.getDebug());
            setForeignKeySupport(config.getForeignKeySupport());
            setIdNamingConvention(config.getIdNamingConvention());
            setReadOnly(config.isReadOnly());
        }
    }
}
