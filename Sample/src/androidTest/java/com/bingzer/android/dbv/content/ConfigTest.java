package com.bingzer.android.dbv.content;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.*;

public class ConfigTest extends AndroidTestCase{

    IResolver resolver;

    @Override
    protected void setUp() throws Exception {
        resolver = ContentQuery.resolve("content://fake", getContext());
    }

    public void testIdNamingConvention(){
        // default
        assertTrue(resolver.getConfig().getIdNamingConvention().equals("_id"));

        resolver.getConfig().setIdNamingConvention("OID");
        assertTrue(resolver.getConfig().getIdNamingConvention().equals("OID"));
        resolver.getConfig().setIdNamingConvention("LOL");
        assertTrue(resolver.getConfig().getIdNamingConvention().equals("LOL"));
    }

    public void testAppendTableNameForId(){
        // default
        assertTrue(!resolver.getConfig().getAppendTableNameForId());

        resolver.getConfig().setAppendTableNameForId(true);
        assertTrue(resolver.getConfig().getAppendTableNameForId());
        resolver.getConfig().setAppendTableNameForId(false);
        assertTrue(!resolver.getConfig().getAppendTableNameForId());
    }

    public void testSetForeignKeySupport(){
        // default
        assertTrue(!resolver.getConfig().getForeignKeySupport());

        resolver.getConfig().setForeignKeySupport(true);
        assertTrue(resolver.getConfig().getForeignKeySupport());
        resolver.getConfig().setForeignKeySupport(false);
        assertTrue(!resolver.getConfig().getForeignKeySupport());
    }

    public void testSetDebug(){
        // default
        assertFalse(resolver.getConfig().getDebug());

        resolver.getConfig().setDebug(true);
        assertTrue(resolver.getConfig().getDebug());
        resolver.getConfig().setDebug(false);
        assertTrue(!resolver.getConfig().getDebug());
    }

    public void testReadOnly(){
        // default
        assertFalse(resolver.getConfig().isReadOnly());

        resolver.getConfig().setReadOnly(true);
        assertTrue(resolver.getConfig().isReadOnly());
        resolver.getConfig().setReadOnly(false);
        assertTrue(!resolver.getConfig().isReadOnly());
    }

    public void testSetDefaultProjections(){
        // default is idNamingConvention
        assertTrue(resolver.getConfig().getDefaultProjections().length == 1);
        assertEquals(resolver.getConfig().getIdNamingConvention(), resolver.getConfig().getDefaultProjections()[0]);

        resolver.getConfig().setDefaultProjections("A");
        assertEquals(resolver.getConfig().getDefaultProjections()[0], "A");
        resolver.getConfig().setDefaultProjections();
        assertTrue(resolver.getConfig().getDefaultProjections().length == 1);
        assertEquals(resolver.getConfig().getIdNamingConvention(), resolver.getConfig().getDefaultProjections()[0]);
    }


    public void testSetAuthority(){
        // default
        assertNull(resolver.getConfig().getDefaultAuthority());

        resolver.getConfig().setDefaultAuthority("MyAuthority");
        assertEquals(resolver.getConfig().getDefaultAuthority(), "MyAuthority");
        resolver.getConfig().setDefaultAuthority(null);
        assertNull(resolver.getConfig().getDefaultAuthority());
    }
}
