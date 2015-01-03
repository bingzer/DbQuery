package com.bingzer.android.dbv.content;

import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.contracts.IResolver;

public class ContentConfigTest extends AndroidTestCase{

    IResolver resolver;

    @Override
    protected void setUp() throws Exception {
        resolver = ContentQuery.resolve("content://fake", getContext());
    }

    public void testIdNamingConvention(){
        // default
        assertTrue(resolver.getContentConfig().getIdNamingConvention().equals("_id"));

        resolver.getContentConfig().setIdNamingConvention("OID");
        assertTrue(resolver.getContentConfig().getIdNamingConvention().equals("OID"));
        resolver.getContentConfig().setIdNamingConvention("LOL");
        assertTrue(resolver.getContentConfig().getIdNamingConvention().equals("LOL"));
    }

    public void testAppendTableNameForId(){
        // default
        assertTrue(!resolver.getContentConfig().getAppendTableNameForId());

        resolver.getContentConfig().setAppendTableNameForId(true);
        assertTrue(resolver.getContentConfig().getAppendTableNameForId());
        resolver.getContentConfig().setAppendTableNameForId(false);
        assertTrue(!resolver.getContentConfig().getAppendTableNameForId());
    }

    public void testSetForeignKeySupport(){
        // default
        assertTrue(!resolver.getContentConfig().getForeignKeySupport());

        resolver.getContentConfig().setForeignKeySupport(true);
        assertTrue(resolver.getContentConfig().getForeignKeySupport());
        resolver.getContentConfig().setForeignKeySupport(false);
        assertTrue(!resolver.getContentConfig().getForeignKeySupport());
    }

    public void testSetDebug(){
        // default
        assertFalse(resolver.getContentConfig().getDebug());

        resolver.getContentConfig().setDebug(true);
        assertTrue(resolver.getContentConfig().getDebug());
        resolver.getContentConfig().setDebug(false);
        assertTrue(!resolver.getContentConfig().getDebug());
    }

    public void testReadOnly(){
        // default
        assertFalse(resolver.getContentConfig().isReadOnly());

        resolver.getContentConfig().setReadOnly(true);
        assertTrue(resolver.getContentConfig().isReadOnly());
        resolver.getContentConfig().setReadOnly(false);
        assertTrue(!resolver.getContentConfig().isReadOnly());
    }

    public void testSetDefaultProjections(){
        // default is idNamingConvention
        assertTrue(resolver.getContentConfig().getDefaultProjections().length == 1);
        assertEquals(resolver.getContentConfig().getIdNamingConvention(), resolver.getContentConfig().getDefaultProjections()[0]);

        resolver.getContentConfig().setDefaultProjections("A");
        assertEquals(resolver.getContentConfig().getDefaultProjections()[0], "A");
        resolver.getContentConfig().setDefaultProjections();
        assertTrue(resolver.getContentConfig().getDefaultProjections().length == 1);
        assertEquals(resolver.getContentConfig().getIdNamingConvention(), resolver.getContentConfig().getDefaultProjections()[0]);
    }


    public void testSetAuthority(){
        // default
        assertNull(resolver.getContentConfig().getDefaultAuthority());

        resolver.getContentConfig().setDefaultAuthority("MyAuthority");
        assertEquals(resolver.getContentConfig().getDefaultAuthority(), "MyAuthority");
        resolver.getContentConfig().setDefaultAuthority(null);
        assertNull(resolver.getContentConfig().getDefaultAuthority());
    }
}
