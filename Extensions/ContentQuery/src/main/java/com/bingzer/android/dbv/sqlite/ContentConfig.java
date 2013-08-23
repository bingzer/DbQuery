package com.bingzer.android.dbv.sqlite;

/**
 * Created by Ricky on 8/22/13.
 */
public class ContentConfig extends Config {

    String[] defaultProjections;
    String authority;

    public ContentConfig(){
        setIdNamingConvention("_id");
        setDefaultProjections();
    }

    public ContentConfig(ContentConfig config){
        if(config != null){
            setAppendTableNameForId(config.getAppendTableNameForId());
            setDebug(config.getDebug());
            setForeignKeySupport(config.getForeignKeySupport());
            setIdNamingConvention(config.getIdNamingConvention());
            setReadOnly(config.isReadOnly());
            setDefaultAuthority(config.getDefaultAuthority());
            setDefaultProjections(config.getDefaultProjections());
        }
    }

    /**
     * Sets the default projections (columns) unless if otherwise
     * specified with {@link com.bingzer.android.dbv.IQuery.Select#columns(String...)}
     * in a select statement.
     * By default the default projections is
     * {@link com.bingzer.android.dbv.IConfig#getIdNamingConvention()}
     * @param columns the columns to set
     */
    public void setDefaultProjections(String... columns){
        if(columns == null || columns.length == 0)
            defaultProjections = new String[] { idNamingConvention };
        else defaultProjections = columns;
    }

    /**
     * Returns the default projections
     * @return projections
     */
    public String[] getDefaultProjections(){
        return defaultProjections;
    }

    /**
     * Sets the default <code>Authority</code>
     * @param authority authority to set
     */
    public void setDefaultAuthority(String authority){
        this.authority = authority;
    }

    /**
     * Returns the <code>authority</code>
     * @return Authority
     */
    public String getDefaultAuthority(){
        return authority;
    }
}
