package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Ankur Gupta on 12/8/17.
 * guptaankur.gupta0@gmail.com
 */

/**
 * Database helper. Manages database creation and version control.
 */
public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String TAG = ProductDbHelper.class.getSimpleName();

    //Name of the database file
    private static final String DATABASE_NAME = "inventory.db";
    //Database version
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs new instance of {@link ProductDbHelper}
     *
     * @param context of the app
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates database if it doesn't exist
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Query string to create table
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_BRAND + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_CATEGORY + " INTEGER NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT + " REAL, " +
                ProductEntry.COLUMN_PRODUCT_STOCK + " INTEGER NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL" +
                ");";
        //Execute query
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);

        //Log the query to double-check
        Log.d(TAG, SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
