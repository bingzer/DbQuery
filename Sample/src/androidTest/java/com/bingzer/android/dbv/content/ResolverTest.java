package com.bingzer.android.dbv.content;

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
        assertNotNull(resolver.getConfig().getDefaultProjections());
        assertTrue(resolver.getConfig().getDefaultProjections().length > 0);
        assertEquals(resolver.getConfig().getDefaultProjections()[0], resolver.getConfig().getIdNamingConvention());
    }

    public void testGetAuthority(){
        // defaults
        assertNull(resolver.getConfig().getDefaultAuthority());

        resolver.getConfig().setDefaultAuthority("MyAuthority");
        assertNotNull(resolver.getConfig().getDefaultAuthority());
        assertEquals("MyAuthority", resolver.getConfig().getDefaultAuthority());
    }

    /////////////////////// config /////////////////////////////////

    public void test_ConfigGetIdNamingConvention(){
        // default
        assertEquals("_id", resolver.getConfig().getIdNamingConvention());

        resolver.getConfig().setIdNamingConvention("XXID");
        assertEquals("XXID", resolver.getConfig().getIdNamingConvention());
    }
}
