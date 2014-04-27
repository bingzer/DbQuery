package com.bingzer.android.dbv.content.test;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
public class Word implements IEntity{
    private int id;
    private String word;

    Word(){
        this(null);
    }

    Word(String word){
        this(-1, word);
    }

    Word(int id, String word){
        this.id = id;
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
        mapper.mapId(new Delegate.TypeInteger() {
            @Override
            public void set(Integer value) {
                setId(value);
            }

            @Override
            public Integer get() {
                return getId();
            }
        });

        mapper.map("word", new Delegate.TypeString(){

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
