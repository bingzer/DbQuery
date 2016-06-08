package com.bingzer.android.dbv.content;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.bingzer.android.dbv.content.contracts.IResolver;

/**
 * Basic resolve setter/getter test
 */
@Suppress
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
        assertNotNull(resolver.getContentConfig());
    }

    public void testGetDefaultProjections(){
        assertNotNull(resolver.getContentConfig().getDefaultProjections());
        assertTrue(resolver.getContentConfig().getDefaultProjections().length > 0);
        assertEquals(resolver.getContentConfig().getDefaultProjections()[0], resolver.getContentConfig().getIdNamingConvention());
    }

    public void testGetAuthority(){
        // defaults
        assertNull(resolver.getContentConfig().getDefaultAuthority());

        resolver.getContentConfig().setDefaultAuthority("MyAuthority");
        assertNotNull(resolver.getContentConfig().getDefaultAuthority());
        assertEquals("MyAuthority", resolver.getContentConfig().getDefaultAuthority());
    }

    /////////////////////// config /////////////////////////////////

    public void test_ConfigGetIdNamingConvention(){
        // default
        assertEquals("_id", resolver.getContentConfig().getIdNamingConvention());

        resolver.getContentConfig().setIdNamingConvention("XXID");
        assertEquals("XXID", resolver.getContentConfig().getIdNamingConvention());
    }
}
