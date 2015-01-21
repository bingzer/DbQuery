package com.bingzer.android.dbv.content.contracts;

import com.bingzer.android.dbv.IConfig;

public interface IContentQueryConfig extends IConfig {

    /**
     * Sets the default projections (columns) unless if otherwise
     * specified with {@link com.bingzer.android.dbv.queries.Select#columns(String...)}
     * in a select statement.
     * By default the default projections is
     * {@link com.bingzer.android.dbv.IConfig#getIdNamingConvention()}
     * @param columns the columns to set
     */
    void setDefaultProjections(String... columns);

    /**
     * Returns the default projections
     * @return projections
     */
    String[] getDefaultProjections();

    /**
     * Sets the default <code>Authority</code>
     * @param authority authority to set
     */
    void setDefaultAuthority(String authority);

    /**
     * Returns the <code>authority</code>
     * @return Authority
     */
    String getDefaultAuthority();

}
