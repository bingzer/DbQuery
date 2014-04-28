package com.bingzer.android.dbv.content.test;

import com.bingzer.android.dbv.IEntityList;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
public class WordList extends LinkedList<Word> implements IEntityList<Word>{

    @Override
    public Word newEntity() {
        return new Word();
    }
}
