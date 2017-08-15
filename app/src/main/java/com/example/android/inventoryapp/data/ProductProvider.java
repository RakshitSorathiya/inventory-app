package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Ankur Gupta on 13/8/17.
 * guptaankur.gupta0@gmail.com
 */

/**
 * {@link ContentProvider} for the app
 */
public class ProductProvider extends ContentProvider {
    private static final String TAG = ProductProvider.class.getSimpleName();

    //URI matcher for the content URI of products table
    private static final int PRODUCTS = 69;

    //URI matcher for single product in products table
    private static final int PRODUCTS_ID = 666;

    //URI matcher object and adding patterns in it
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    //Database helper object
    private ProductDbHelper mProductDbHelper;

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Method to read database. First URI is matched and particular case gets executed
     *
     * @param uri           of the products or single product
     * @param projection    which column you want
     * @param selection     where clause
     * @param selectionArgs arguments of where clause. Prevents SQL Injection
     * @param sortOrder     order in which result should appear
     * @return Cursor object which can given to {@link com.example.android.inventoryapp.ProductCursorAdapter}
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mProductDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("CAnnot query unknown URI" + uri);
        }

        //To listen for database changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported");
        }
    }

    /**
     * Delete products or single product
     *
     * @param uri           of the product to be deleted
     * @param selection     where clause
     * @param selectionArgs arguments of where clause
     * @return integer indicating number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //Notify to Content Resolver that the data has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Helper method for Insertion of record in databse
     *
     * @param uri    of product table
     * @param values key-value pair of data to be inserted
     * @return Uri object pointing to the row just inserted
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        //Check id data is valid for insertion. Prevent bad data
        sanityCheck(values);

        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.d(TAG, "Failed to insert row for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Helper method for updating a record in database
     *
     * @param uri           of the product
     * @param values        updated ke-value pair
     * @param selection     where clause
     * @param selectionArgs arguments of where clause
     * @return integer indicating number of rows updated
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Check for bad data
        sanityCheck(values);

        //Return early if no data is changed
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mProductDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Helper method for {@link ProductProvider#insertProduct(Uri, ContentValues)}
     * and {@link ProductProvider#updateProduct(Uri, ContentValues, String, String[])}
     * Prevents bad data to be inserted in databse
     *
     * @param values key-value pairs of data
     */
    private void sanityCheck(ContentValues values) {
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String brand = values.getAsString(ProductEntry.COLUMN_PRODUCT_BRAND);
            if (brand == null) {
                throw new IllegalArgumentException("Product requires brand");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            Integer category = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_CATEGORY);
            if (category == null || !ProductEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("Invalid category");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            Float price = values.getAsFloat(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            Integer stock = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_STOCK);
            if (stock != null && stock < 0) {
                throw new IllegalArgumentException("Invalid stock");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String image = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Product requires image");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String suppName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (suppName == null) {
                throw new IllegalArgumentException("Product requires supplier name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String suppEmail = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            if (suppEmail == null) {
                throw new IllegalArgumentException("Product requires supplier email");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String suppPhone = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            if (suppPhone == null) {
                throw new IllegalArgumentException("Product requires supplier phone");
            }
        }
    }
}
