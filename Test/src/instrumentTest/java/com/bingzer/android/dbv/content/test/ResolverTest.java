package com.bingzer.android.dbv.content.test;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

/**
 * Basic resolve setter/getter test
 */
public class ResolverTest extends AndroidTestCase {

    IResolver resolver;

    @Override
    public void setUp(){
        resolver = ContentQuery.resolve("content://fake", getContext());
    }

    ////////////////////////////////////////////////////////

    public void testGetUri(){
        assertNotNull(resolver.getUri());
        assertEquals(resolver.getUri(), Uri.parse("content://fake"));
    }

    public void testGetConfig(){
        assertNotNull(resolver.getConfig());
    }

    public void testGetDefaultProjections(){
        assertNotNull(resolver.getDefaultProjections());
        assertTrue(resolver.getDefaultProjections().length > 0);
        assertEquals(resolver.getDefaultProjections()[0], resolver.getConfig().getIdNamingConvention());
    }

    public void testGetAuthority(){
        // defaults
        assertNull(resolver.getDefaultAuthority());

        resolver.setDefaultAuthority("MyAuthority");
        assertNotNull(resolver.getDefaultAuthority());
        assertEquals("MyAuthority", resolver.getDefaultAuthority());
    }

    /////////////////////// config /////////////////////////////////

    public void test_ConfigGetIdNamingConvention(){
        // default
        assertEquals("_id", resolver.getConfig().getIdNamingConvention());

        resolver.getConfig().setIdNamingConvention("XXID");
        assertEquals("XXID", resolver.getConfig().getIdNamingConvention());
    }
}
