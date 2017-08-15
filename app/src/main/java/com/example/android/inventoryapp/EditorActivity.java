package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Activity to create new product or update/edit the existing product
 * {@link CatalogActivity} send here to create new product
 * {@link DetailsActivity} send here to update existing product
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Objects of required views
    private TextInputEditText mNameEditText;
    private TextInputEditText mBrandEditText;
    private TextInputEditText mPriceEditText;
    private TextInputEditText mDiscountEditText;
    private TextInputEditText mStockEditText;
    private TextInputEditText mSupplierNameEditText;
    private TextInputEditText mSupplierEmailEditText;
    private TextInputEditText mSupplierPhoneEditText;
    private Spinner mCategorySpinner;
    private ImageView mProductImageView;

    //Default category in spinner to be selected
    private int mCategory = 0;

    //Stores the URI of Image picked
    private Uri mPickedImage;
    //Stores the URI of product which is selected for editing
    private Uri mCurrentProductUri;

    //Touch listener to detect whether the user has changed some fields
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Get the URI of selected product.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //URI is null when CatalogActivity send here to create new product.
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            //Hide actions not required for this mode
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            //start loader to get data of lareasy existing product
            getLoaderManager().initLoader(1, null, this);
        }

        //Reference all views
        mNameEditText = (TextInputEditText) findViewById(R.id.edit_name);
        mBrandEditText = (TextInputEditText) findViewById(R.id.edit_brand);
        mPriceEditText = (TextInputEditText) findViewById(R.id.edit_price);
        mDiscountEditText = (TextInputEditText) findViewById(R.id.edit_discount);
        mStockEditText = (TextInputEditText) findViewById(R.id.edit_stock);
        mSupplierNameEditText = (TextInputEditText) findViewById(R.id.edit_supp_name);
        mSupplierEmailEditText = (TextInputEditText) findViewById(R.id.edit_supp_email);
        mSupplierPhoneEditText = (TextInputEditText) findViewById(R.id.edit_supp_phone);
        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        mProductImageView = (ImageView) findViewById(R.id.product_image);

        //Image picker intent
        mProductImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        //Setup OnTouchListener on all fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mBrandEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mDiscountEditText.setOnTouchListener(mTouchListener);
        mStockEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);
        mProductImageView.setOnTouchListener(mTouchListener);

        setUpSpinner();

    }

    /**
     * Executes when user returns from Image picker intent
     *
     * @param requestCode
     * @param resultCode
     * @param data        contains the URI of image the user picked
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            //Return early andshow message if there was error during picking image
            if (data == null) {
                Toast.makeText(this, getString(R.string.toast_error_picking_image), Toast.LENGTH_SHORT).show();
                return;
            }
            //Store the URI of picked image
            mPickedImage = data.getData();
            //Set it on the ImageView
            mProductImageView.setImageURI(mPickedImage);
            //Change scaleType from centerInside to centerCrop
            mProductImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //Hide delete action if mode is "Create new product"
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                Utils.showDeleteConfirmationDialog(this, mCurrentProductUri);
                return true;
            case android.R.id.home:
                //Show user a warning if the data has changed user pressed back button accidentally or deliberately
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButonClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                Utils.showUnsavedChangesDialog(discardButonClickListener, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //We need all columns this time
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_BRAND,
                ProductEntry.COLUMN_PRODUCT_CATEGORY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT,
                ProductEntry.COLUMN_PRODUCT_STOCK,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };
        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            //Get column index from cursor
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int brandColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_BRAND);
            int categoryColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int discountColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT);
            int stockColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STOCK);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            int suppNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int suppEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int suppPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            //Extract data from column using column index
            String name = cursor.getString(nameColumnIndex);
            String brand = cursor.getString(brandColumnIndex);
            Integer category = cursor.getInt(categoryColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            Float discount = cursor.getFloat(discountColumnIndex);
            Integer stock = cursor.getInt(stockColumnIndex);
            String image = cursor.getString(imageColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);
            String suppEmail = cursor.getString(suppEmailColumnIndex);
            String suppPhone = cursor.getString(suppPhoneColumnIndex);

            //Present data to user
            mNameEditText.setText(name);
            mBrandEditText.setText(brand);
            mCategorySpinner.setSelection(category);
            mPriceEditText.setText(String.valueOf(price));
            mDiscountEditText.setText(String.valueOf(discount));
            mStockEditText.setText(String.valueOf(stock));
            Uri uri = Uri.parse(image);
            mProductImageView.setImageURI(uri);
            mSupplierNameEditText.setText(suppName);
            mSupplierEmailEditText.setText(suppEmail);
            mSupplierPhoneEditText.setText(suppPhone);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBrandEditText.setText("");
        mCategorySpinner.setSelection(ProductEntry.CATEGORY_MOBILE);
        mPriceEditText.setText("");
        mDiscountEditText.setText("");
        mStockEditText.setText("");
        mProductImageView.setImageURI(null);
        mSupplierNameEditText.setText("");
        mSupplierEmailEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /**
     * Helper method to setup spinner and populate data in it
     */
    private void setUpSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.category_mobile))) {
                        mCategory = ProductEntry.CATEGORY_MOBILE; // Male
                    } else if (selection.equals(getString(R.string.category_laptop))) {
                        mCategory = ProductEntry.CATEGORY_LAPTOP; // Female
                    } else {
                        mCategory = ProductEntry.CATEGORY_TELEVISION; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = 0;
            }
        });
    }

    /**
     * Helper method invoked when user clicks Done/Save button
     */
    private void saveProduct() {
        //Extract new/updated data from fields
        String name = mNameEditText.getText().toString().trim();
        String brand = mBrandEditText.getText().toString().trim();
        int category = mCategory;
        String priceString = mPriceEditText.getText().toString().trim();
        String discountString = mDiscountEditText.getText().toString().trim();
        String stockString = mStockEditText.getText().toString().trim();
        String image = null;
        if (mPickedImage != null) image = mPickedImage.toString();
        String suppName = mSupplierNameEditText.getText().toString().trim();
        String suppEmail = mSupplierEmailEditText.getText().toString().trim();
        String suppPhone = mSupplierPhoneEditText.getText().toString().trim();

        float price;
        float discount;
        int stock;
        try {
            price = Float.parseFloat(priceString);
        } catch (NumberFormatException e) {
            price = 0;
        }
        try {
            discount = Float.parseFloat(discountString);
        } catch (NumberFormatException e) {
            discount = 0;
        }
        try {
            stock = Integer.parseInt(stockString);
        } catch (NumberFormatException e) {
            stock = 0;
        }

        //Check if field is empty. And show proper message to fill them.
        if (TextUtils.isEmpty(name)) {
            mNameEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(brand)) {
            mBrandEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            mPriceEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(stockString)) {
            mStockEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(image)) {
            Toast.makeText(this, getString(R.string.toast_image_cannot_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(suppName)) {
            mSupplierNameEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(suppEmail)) {
            mSupplierEmailEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }
        if (TextUtils.isEmpty(suppPhone)) {
            mSupplierPhoneEditText.setError(getString(R.string.field_cannot_empty));
            return;
        }

        //Make key-value pair of all new/updated data
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductEntry.COLUMN_PRODUCT_BRAND, brand);
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY, category);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT, discount);
        values.put(ProductEntry.COLUMN_PRODUCT_STOCK, stock);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, image);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, suppName);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, suppEmail);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, suppPhone);

        //Decide whether the new product is created or existing is updated
        if (mCurrentProductUri == null) {
            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(this, getString(R.string.toast_error_saving_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_successfully_saved_product), Toast.LENGTH_SHORT).show();
            }
        } else {
            int numRows = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (numRows > 0) {
                Toast.makeText(this, getString(R.string.toast_successfully_updated_product), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_error_updating_product), Toast.LENGTH_SHORT).show();
            }
        }

        finish();

    }
}
