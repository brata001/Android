package com.poc.evault.utils;

/**
 * Created by DASGUA2 on 4/2/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.poc.evault.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DocumentDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_FILE_NAME,
            DatabaseHelper.COLUMN_UPLOAD_DATE, DatabaseHelper.COLUMN_FILE_SIZE, DatabaseHelper.COLUMN_TYPE};

    public DocumentDataSource(Context context) {

        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

/*    public void setMockData(){
        createDocument("File1","This is an Excel file","asd987987as");
        createDocument("File2","This is an Doc file","asd987237as");
        createDocument("File3","This is an PDF file","astd227987as");
        createDocument("File4","This is an PPT file","asd987922ad");
    }*/

    public Item setItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FILE_NAME, item.getName());
        values.put(DatabaseHelper.COLUMN_UPLOAD_DATE, item.getUploadDate());
        values.put(DatabaseHelper.COLUMN_FILE_SIZE, item.getSize());
        values.put(DatabaseHelper.COLUMN_TYPE, item.getType());
        long insertId = database.insert(DatabaseHelper.TABLE_DOC_DATA, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_DOC_DATA,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Item newDocument = cursorToItem(cursor);
        cursor.close();
        return newDocument;
    }

    public void deleteItem(long id) {
        System.out.println("Document deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_DOC_DATA, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Item> getAllDocuments() {
        List<Item> items = new ArrayList<Item>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_DOC_DATA,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(0));
        item.setName(cursor.getString(1));
        item.setUploadDate(cursor.getString(2));
        item.setSize(cursor.getString(3));
        item.setType(cursor.getString(4));
        return item;
    }
}