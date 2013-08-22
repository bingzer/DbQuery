package com.bingzer.android.dbv.content.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

import java.util.ArrayList;
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
        resolver.setDefaultProjections("_id", "word");
        resolver.setDefaultAuthority(UserDictionary.AUTHORITY);
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

    public void testSelectId(){
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

    public void testSelect_GroupBy(){
        boolean foundBaloteli = false;
        boolean foundPirlo = false;
        boolean foundKaka = false;
        boolean foundMessi = false;
        boolean foundRonaldo = false;
        Cursor cursor = resolver
                .select()
                .columns("word")
                .orderBy("word")
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

    /////////////////////////////////////////////////////////////////////////////////

    static class Word implements IEntity {

        private int id;
        private String word;

        Word(){
            this(null);
        }

        Word(String word){
            this.word = word;
        }

        void setId(int id) {
            this.id = id;
        }

        String getWord() {
            return word;
        }

        void setWord(String word) {
            this.word = word;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void map(Mapper mapper) {
            mapper.mapId(new Action<Integer>(Integer.class) {
                @Override
                public void set(Integer value) {
                    setId(value);
                }

                @Override
                public Integer get() {
                    return getId();
                }
            });

            mapper.map("word", new Action<String>(String.class){

                @Override
                public void set(String value) {
                    setWord(value);
                }

                @Override
                public String get() {
                    return getWord();
                }
            });
        }
    }

    static class WordList extends ArrayList<Word> implements IEntityList<Word>{

        @Override
        public List<Word> getEntityList() {
            return this;
        }

        @Override
        public Word newEntity() {
            return new Word();
        }
    }





}
