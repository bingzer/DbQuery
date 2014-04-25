package com.bingzer.android.dbv.content.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky on 8/21/13.
 */
public class ContentTest extends AndroidTestCase {

    IResolver resolver;
    int baloteliId, pirloId, kakaId, messiId, ronaldoId;

    @Override
    public void setUp(){
        resolver = ContentQuery.resolve(UserDictionary.Words.CONTENT_URI, getContext());
        resolver.getConfig().setDefaultProjections("_id", "word");
        resolver.getConfig().setDefaultAuthority(UserDictionary.AUTHORITY);
        resolver.delete("word IN (?,?,?,?,?)", "Baloteli", "Pirlo", "Kaka", "Messi", "Ronaldo").query();

        baloteliId = insertToDictionary("Baloteli");
        pirloId = insertToDictionary("Pirlo");
        kakaId = insertToDictionary("Kaka");
        messiId = insertToDictionary("Messi");
        ronaldoId = insertToDictionary("Ronaldo");
    }

    int insertToDictionary(String word){
        int id = resolver.insert("word").val(word).query();
        assertTrue(id > 0);
        return id;
    }

    @Override
    public void tearDown(){
        resolver.delete(baloteliId, pirloId, kakaId, messiId, ronaldoId);
        resolver.delete("word like ?", "%-Updated");
    }

    ///////////////////////////////////////////////////////////////////////////////////


    public void testHas_Id(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(pirloId));
        assertTrue(resolver.has(kakaId));
        assertTrue(resolver.has(messiId));
        assertTrue(resolver.has(ronaldoId));

        assertFalse(resolver.has(-1));
        assertFalse(resolver.has(ronaldoId * -2));
    }

    public void testHas_Condition(){
        assertTrue(resolver.has("word not null"));
        assertTrue(resolver.has("word = 'Baloteli'"));
        assertTrue(resolver.has("word = 'Baloteli' or word = 'Pirlo'"));

        assertFalse(resolver.has("word = 'Baloteli' and _id = " + messiId));
        assertFalse(resolver.has("word = 'Baloteli' and _id is null"));
    }

    public void testHas_WhereClause(){
        assertTrue(resolver.has("word = ? and _id = ?", "Baloteli", baloteliId));
        assertTrue(resolver.has("word = ? or _id = ?", "Baloteli", messiId));
        assertTrue(resolver.has("_id in (?,?,?)", baloteliId, messiId, -1));
        assertTrue(resolver.has("word in (?,?,?)", "Baloteli", "NONEXSITENCE", "SDFSDF"));

        assertFalse(resolver.has("word = ? and _id = ?", "Baloteli", messiId));
        assertFalse(resolver.has("word in (?,?,?)", "OPIO", "NONEXSITENCE", "SDFSDF"));
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void testCount(){
        assertTrue(resolver.count() > 0);
    }

    public void testCount_Condition(){
        assertEquals(1, resolver.count("word = 'Baloteli'"));
        assertEquals(0, resolver.count("word = 'sdfsdfsdf'"));
        assertEquals(2, resolver.count("word in ('Baloteli', 'Messi')"));

        assertFalse(resolver.count("word not null") == -1);
        assertFalse(resolver.count("word not null") == 0);
    }

    public void testCount_WhereClause(){
        assertEquals(1, resolver.count("word = ?", "Ronaldo"));
        assertEquals(0, resolver.count("word = 'sdfsdfsdf'"));
        assertEquals(5, resolver.count("word in (?,?,?,?,?)", "Baloteli", "Pirlo", "Kaka", "Messi", "Ronaldo"));

        assertFalse(resolver.count("word not null") == -1);
        assertFalse(resolver.count("word not null") == 0);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void testSelect_Id(){
        Cursor cursor = resolver.select(baloteliId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Baloteli"));
        }
        cursor.close();

        cursor = resolver.select(pirloId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Pirlo"));
        }
        cursor.close();

        cursor = resolver.select(kakaId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Kaka"));
        }
        cursor.close();

        cursor = resolver.select(messiId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Messi"));
        }
        cursor.close();

        cursor = resolver.select(ronaldoId).query();
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) > 0);
            assertTrue(cursor.getString(1).equalsIgnoreCase("Ronaldo"));
        }
        cursor.close();
    }

    public void testSelect_Top_Condition(){
        Cursor cursor = resolver.select(2, "word not null").query();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelect_Top_WhereClause(){
        Cursor cursor = resolver.select(1, "word = ?", "Baloteli").query();
        assertTrue(cursor.getCount() == 1);
        cursor.close();

        cursor = resolver.select(2, "word in (?,?,?)", "Baloteli", "Messi", "Pirlo").query();
        assertTrue(cursor.getCount() == 2);
        cursor.close();
    }

    public void testSelect_Ids(){
        Cursor cursor = resolver.select(baloteliId, pirloId, kakaId, messiId, ronaldoId).query();
        assertTrue(cursor.getCount() == 5);
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == baloteliId);
            assertTrue(cursor.getString(1).equals("Baloteli"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == pirloId);
            assertTrue(cursor.getString(1).equals("Pirlo"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == kakaId);
            assertTrue(cursor.getString(1).equals("Kaka"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == messiId);
            assertTrue(cursor.getString(1).equals("Messi"));
        }
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == ronaldoId);
            assertTrue(cursor.getString(1).equals("Ronaldo"));
        }
        cursor.close();
    }

    public void testSelect_Condition(){
        Cursor cursor = resolver.select("word = 'Baloteli'").query();
        assertTrue(cursor.getCount() == 1);
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == baloteliId);
            assertTrue(cursor.getString(1).equals("Baloteli"));
        }
        cursor.close();
    }

    public void testSelect_WhereClause(){
        Cursor cursor = resolver.select("word = ?", "Baloteli").query();
        assertTrue(cursor.getCount() == 1);
        if(cursor.moveToNext()){
            assertTrue(cursor.getInt(0) == baloteliId);
            assertTrue(cursor.getString(1).equals("Baloteli"));
        }
        cursor.close();
    }

    public void testSelect_OrderBy(){
        boolean foundBaloteli = false;
        boolean foundPirlo = false;
        boolean foundKaka = false;
        boolean foundMessi = false;
        boolean foundRonaldo = false;
        Cursor cursor = resolver
                .select()
                .columns("word")
                .orderBy("_id")
                .query();
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(UserDictionary.Words.WORD);
            String word = cursor.getString(index);

            if(!foundBaloteli) foundBaloteli = word.equals("Baloteli");
            if(!foundPirlo) foundPirlo = word.equals("Pirlo");
            if(!foundKaka) foundKaka = word.equals("Kaka");
            if(!foundMessi) foundMessi = word.equals("Messi");
            if(!foundRonaldo) foundRonaldo = word.equals("Ronaldo");
        }

        assertTrue(foundBaloteli && foundPirlo && foundKaka && foundMessi && foundRonaldo);
    }


    public void testSelect_OrderBy2(){
        Cursor cursor = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .columns("word")
                .orderBy("word")
                .query();

        assertTrue(cursor.getCount() == 5);

        assertTrue(cursor.moveToNext());
        assertEquals("Baloteli", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Kaka", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Messi", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Pirlo", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Ronaldo", cursor.getString(0));

        cursor.close();
    }


    public void testSelect_OrderBy3(){
        Cursor cursor = resolver
                .select(baloteliId, pirloId, kakaId, messiId, ronaldoId)
                .columns("word")
                .orderBy("word desc")
                .query();

        assertTrue(cursor.getCount() == 5);

        assertTrue(cursor.moveToNext());
        assertEquals("Ronaldo", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Pirlo", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Messi", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Kaka", cursor.getString(0));

        assertTrue(cursor.moveToNext());
        assertEquals("Baloteli", cursor.getString(0));

        cursor.close();
    }

    public void selectId_Condition(){
        assertEquals(baloteliId, resolver.selectId("word = 'Baloteli'"));
        assertEquals(pirloId, resolver.selectId("word = 'Pirlo'"));
        assertEquals(kakaId, resolver.selectId("word = 'Kaka'"));
        assertEquals(messiId, resolver.selectId("word = 'Messi'"));
        assertEquals(ronaldoId, resolver.selectId("word = 'Ronaldo'"));

        assertEquals(-1, resolver.select("word = 'whatever'"));
    }

    public void selectId_WhereClause(){
        assertEquals(baloteliId, resolver.selectId("word = ?", "Balotelli"));
        assertEquals(pirloId, resolver.selectId("word = ?", "Pirlo"));
        assertEquals(kakaId, resolver.selectId("word = ?", "Kaka"));
        assertEquals(messiId, resolver.selectId("word = ?", "Messi"));
        assertEquals(ronaldoId, resolver.selectId("word = ?", "Ronaldo"));

        assertEquals(-1, resolver.select("word = ?", "yoasfsdfsdfsdf"));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    public void testInsert_ContentValues(){
        ContentValues values = new ContentValues();
        values.put("word", "OLA");

        IQuery.Insert insert = resolver.insert(values);
        assertTrue(insert.query() > 0);

        Uri actual = Uri.parse(insert.toString());
        Uri expected = Uri.withAppendedPath(resolver.getUri(), insert.query().toString());
        assertEquals(actual, expected);

        assertTrue(resolver.delete(insert.query()).query() > 0);
    }

    public void testInsert_IEntity(){
        Word word = new Word();
        word.setWord("M&M");

        IQuery.Insert insert = resolver.insert(word);
        assertTrue(insert.query() > 0);

        Uri actual = Uri.parse(insert.toString());
        Uri expected = Uri.withAppendedPath(resolver.getUri(), insert.query().toString());
        assertEquals(actual, expected);

        assertTrue(resolver.delete(insert.query()).query() > 0);
    }

    public void testInsert_IEntityList(){
        WordList list = new WordList();
        list.add(new Word("LigerIsReal"));
        list.add(new Word("NoProblemoBeHapppy"));
        list.add(new Word("Sweeth Yo"));


        IQuery.Insert insert = resolver.insert(list);
        assertTrue(insert.query() == 3); // 3 got inserted

        for(Word w : list){
            assertTrue(w.getId() > 0);
        }

        for(Word w : list){
            assertTrue(resolver.delete(w.getId()).query() == 1);
        }
    }

    public void testInsertWith_Columns(){
        assertTrue(resolver.insert("word").val("ABCDEFG").query() > 0);
        assertTrue(resolver.has("word = ?", "ABCDEFG"));

        assertTrue(resolver.delete("word = ? ", "ABCDEFG").query() > 0);
        assertFalse(resolver.has("word = ?", "ABCDEFG"));
    }

    public void testInsert_Array(){
        assertTrue(resolver.insert(new String[]{"word"}, new Object[]{"ABCDEFG"}).query() > 0);
        assertTrue(resolver.has("word = ?", "ABCDEFG"));

        assertTrue(resolver.delete("word = ? ", "ABCDEFG").query() > 0);
        assertFalse(resolver.has("word = ?", "ABCDEFG"));
    }

    /////////////////////////////// Update /////////////////////////////////////

    public void testUpdate_ContentValues_Id(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));

        ContentValues values = new ContentValues();
        values.put("word", "Baloteli-Updated");
        assertTrue(resolver.update(values, baloteliId).query() == 1);

        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
    }

    public void testUpdate_ContentValues_WhereClause(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));

        ContentValues values = new ContentValues();
        values.put("word", "Baloteli-Updated");
        assertTrue(resolver.update(values, "_id = ?", baloteliId).query() == 1);

        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
    }

    public void testUpdate_IEntity(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));

        Word word = new Word("Baloteli-Updated");
        word.setId(baloteliId);
        assertTrue(resolver.update(word).query() > 0);

        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
    }

    public void testUpdate_IEntityList(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));
        assertFalse(resolver.has("word = ?", "Messi-Updated"));
        assertFalse(resolver.has("word = ?", "Ronaldo-Updated"));

        WordList list = new WordList();
        resolver.select(baloteliId, messiId, ronaldoId).orderBy("_id").query(list);
        assertTrue(list.size() == 3);

        list.get(0).setWord("Baloteli-Updated");
        list.get(1).setWord("Messi-Updated");
        list.get(2).setWord("Ronaldo-Updated");

        assertTrue(resolver.update(list).query() == 3);
        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
        assertTrue(resolver.has("word = ?", "Messi-Updated"));
        assertTrue(resolver.has("word = ?", "Ronaldo-Updated"));
    }

    public void testUpdate_ColumnValue_Id(){
        assertFalse(resolver.has("word = ?", "Ronaldo-Updated"));

        assertTrue(resolver.update(ronaldoId).columns("word").val("Ronaldo-Updated").query() == 1);

        assertTrue(resolver.has("word = ?", "Ronaldo-Updated"));
    }

    public void testUpdate_ColumnValue_Condition(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));
        assertFalse(resolver.has("word = ?", "Messi-Updated"));

        assertTrue(resolver.update("word = 'Baloteli'").columns("word").val("Baloteli-Updated").query() == 1);
        assertTrue(resolver.update("_id  = " + messiId).columns("word").val("Messi-Updated").query() == 1);

        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
        assertTrue(resolver.has("word = ?", "Messi-Updated"));
    }

    public void testUpdate_ColumnValue_WhereClause(){
        assertFalse(resolver.has("word = ?", "Pirlo-Updated"));
        assertFalse(resolver.has("word = ?", "Ronaldo-Updated"));

        assertTrue(resolver.update("word = 'Pirlo'").columns("word").val("Pirlo-Updated").query() == 1);
        assertTrue(resolver.update("_id  = " + ronaldoId).columns("word").val("Ronaldo-Updated").query() == 1);

        assertTrue(resolver.has("word = ?", "Pirlo-Updated"));
        assertTrue(resolver.has("word = ?", "Ronaldo-Updated"));
    }

    public void testUpdate_ColumnsValues_Condition(){
        assertFalse(resolver.has("word = ?", "Baloteli-Updated"));
        assertFalse(resolver.has("word = ?", "Messi-Updated"));

        assertTrue(resolver.update("word = 'Baloteli'").columns(new String[]{"word"}).val(new Object[]{"Baloteli-Updated"}).query() == 1);
        assertTrue(resolver.update("_id = " + messiId).columns(new String[]{"word"}).val(new Object[]{"Messi-Updated"}).query() == 1);

        assertTrue(resolver.has("word = ?", "Baloteli-Updated"));
        assertTrue(resolver.has("word = ?", "Messi-Updated"));
    }

    public void testUpdate_ColumnsValues_WhereClause(){
        assertFalse(resolver.has("word = ?", "Pirlo-Updated"));
        assertFalse(resolver.has("word = ?", "Ronaldo-Updated"));

        assertTrue(resolver.update("word = ?", "Pirlo").columns(new String[]{"word"}).val(new Object[]{"Pirlo-Updated"}).query() == 1);
        assertTrue(resolver.update("_id = ?", ronaldoId).columns(new String[]{"word"}).val(new Object[]{"Ronaldo-Updated"}).query() == 1);

        assertTrue(resolver.has("word = ?", "Pirlo-Updated"));
        assertTrue(resolver.has("word = ?", "Ronaldo-Updated"));
    }

    /////////////////////////////// Delete /////////////////////////////////////

    public void testDelete_Id(){
        assertTrue(resolver.has(baloteliId));

        assertTrue(resolver.delete(baloteliId).query() == 1);

        assertFalse(resolver.has(baloteliId));
    }

    public void testDelete_Ids(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(messiId));

        assertTrue(resolver.delete(baloteliId, messiId).query() == 2);

        assertFalse(resolver.has(baloteliId));
        assertFalse(resolver.has(messiId));
    }

    public void testDelete_IdList(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(messiId));
        assertTrue(resolver.has(kakaId));

        List<Integer> list = new LinkedList<Integer>();
        list.add(baloteliId);
        list.add(messiId);
        list.add(kakaId);

        assertTrue(resolver.delete(list).query() == 3);

        assertFalse(resolver.has(baloteliId));
        assertFalse(resolver.has(messiId));
        assertFalse(resolver.has(kakaId));
    }

    public void testDelete_Condition(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(messiId));
        assertTrue(resolver.has(kakaId));

        assertTrue(resolver.delete("word = 'Baloteli' or word = 'Messi' or word = 'Kaka'").query() == 3);

        assertFalse(resolver.has(baloteliId));
        assertFalse(resolver.has(messiId));
        assertFalse(resolver.has(kakaId));
    }

    public void testDelete_WhereClause(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(messiId));
        assertTrue(resolver.has(kakaId));

        assertTrue(resolver.delete("word = ? or _id = ? or word = ?", "Baloteli", messiId, "Kaka").query() == 3);

        assertFalse(resolver.has(baloteliId));
        assertFalse(resolver.has(messiId));
        assertFalse(resolver.has(kakaId));
    }

    public void testDelete_IEntity(){
        assertTrue(resolver.has(baloteliId));
        Word word = new Word("Baloteli");
        word.setId(baloteliId);

        assertTrue(resolver.delete(word).query() == 1);

        assertFalse(resolver.has(baloteliId));
    }

    public void testDelete_IEntityList(){
        assertTrue(resolver.has(baloteliId));
        assertTrue(resolver.has(messiId));
        assertTrue(resolver.has(kakaId));

        WordList list = new WordList();
        list.add(new Word(baloteliId, "Baloteli"));
        list.add(new Word(messiId, "Messi"));
        list.add(new Word(kakaId, "Kaka"));
        assertTrue(resolver.delete(list).query() == 3);

        assertFalse(resolver.has(baloteliId));
        assertFalse(resolver.has(messiId));
        assertFalse(resolver.has(kakaId));
    }



    /////////////////////////////// IEntity & IEntityList /////////////////////////////////////

    public void testSelectId_Entity(){
        Word word = new Word();

        resolver.select(baloteliId).query(word);
        assertEquals(word.getId(), baloteliId);
        assertEquals(word.getWord(), "Baloteli");

        resolver.select(pirloId).query(word);
        assertEquals(word.getId(), pirloId);
        assertEquals(word.getWord(), "Pirlo");

        resolver.select(kakaId).query(word);
        assertEquals(word.getId(), kakaId);
        assertEquals(word.getWord(), "Kaka");

        resolver.select(messiId).query(word);
        assertEquals(word.getId(), messiId);
        assertEquals(word.getWord(), "Messi");

        resolver.select(ronaldoId).query(word);
        assertEquals(word.getId(), ronaldoId);
        assertEquals(word.getWord(), "Ronaldo");
    }

    public void testSelect_EntityList(){
        WordList list = new WordList();
        resolver.select().query(list);

        assertTrue(list.size() > 0);
        for(Word w : list){
            assertTrue(w.getId() > 0);
            assertNotNull(w.getWord());
        }
    }






}
