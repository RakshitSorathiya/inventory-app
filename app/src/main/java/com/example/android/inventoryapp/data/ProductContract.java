package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ankur Gupta on 12/8/17.
 * guptaankur.gupta0@gmail.com
 */

public class ProductContract {

    //Unique content authority name for entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    //Base Uri used by other apps
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Path to the database
    public static final String PATH_PRODUCTS = "products";

    /**
     * Class which defines constant value of Products database
     */
    public static abstract class ProductEntry implements BaseColumns {

        //Uri to access product data
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //The MIME type of the {@link #CONTENT_URI} for a list of product
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //The MIME type of the {@link #CONTENT_URI} for a single product
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        //Name of the table in database
        public static final String TABLE_NAME = "products";

        //Unique ID for product     Type:INTEGER
        public static final String _ID = BaseColumns._ID;

        //Name of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_NAME = "name";

        //Brand of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_BRAND = "brand";

        //Category of the product       Type:INTEGER
        public static final String COLUMN_PRODUCT_CATEGORY = "category";

        //Price of the product       Type:REAL
        public static final String COLUMN_PRODUCT_PRICE = "price";

        //Discount(if applicable) on the product       Type:REAL
        public static final String COLUMN_PRODUCT_PRICE_DISCOUNT = "discount";

        //Stock available       Type:INTEGER
        public static final String COLUMN_PRODUCT_STOCK = "stock";

        //Image Uri of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_IMAGE = "image";

        //Supplier name of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supp_name";

        //Supplier email of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supp_email";

        //Supplier phone of the product       Type:TEXT
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supp_phone";

        /**
         * Possible values for the category of electronic product
         */
        public static final int CATEGORY_MOBILE = 0;
        public static final int CATEGORY_LAPTOP = 1;
        public static final int CATEGORY_TELEVISION = 2;

        /**
         * Returns whether or not the given category is {@link #CATEGORY_MOBILE}, {@link #CATEGORY_LAPTOP},
         * or {@link #CATEGORY_TELEVISION }.
         */
        public static boolean isValidCategory(int category) {
            if (category == CATEGORY_MOBILE || category == CATEGORY_LAPTOP || category == CATEGORY_TELEVISION) {
                return true;
            }
            return false;
        }
    }
}
