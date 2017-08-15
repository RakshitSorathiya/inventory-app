package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;

/**
 * Presents detail of product. Opens up List item is clicked in {@link CatalogActivity}
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    //Objectsof all required views
    private TextView mNameText;
    private TextView mBrandText;
    private TextView mCategoryText;
    private TextView mPriceText;
    private TextView mDiscountText;
    private TextView mStockText;
    private TextView mSupplierNameText;
    private TextView mSupplierEmailText;
    private TextView mSupplierPhoneText;

    private ImageView mImageView;
    private Button mDecStockButton;
    private Button mIncStockButton;
    private ImageButton mEmailSuppButton;
    private ImageButton mPhoneSuppButton;

    //URI of the product for which details is requested
    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get the URI of the product clicked from previous activity
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //Reference views and set OnClickListner
        mNameText = (TextView) findViewById(R.id.details_name);
        mBrandText = (TextView) findViewById(R.id.details_brand);
        mCategoryText = (TextView) findViewById(R.id.details_category);
        mPriceText = (TextView) findViewById(R.id.details_price);
        mDiscountText = (TextView) findViewById(R.id.details_discount);
        mStockText = (TextView) findViewById(R.id.details_stock);
        mSupplierNameText = (TextView) findViewById(R.id.details_supp_name);
        mSupplierEmailText = (TextView) findViewById(R.id.details_supp_email);
        mSupplierPhoneText = (TextView) findViewById(R.id.details_supp_phone);

        mImageView = (ImageView) findViewById(R.id.details_image);
        mDecStockButton = (Button) findViewById(R.id.details_button_dec_stock);
        mIncStockButton = (Button) findViewById(R.id.details_button_inc_stock);
        mEmailSuppButton = (ImageButton) findViewById(R.id.details_button_email_supp);
        mPhoneSuppButton = (ImageButton) findViewById(R.id.details_button_phone_supp);

        mDecStockButton.setOnClickListener(this);
        mIncStockButton.setOnClickListener(this);
        mEmailSuppButton.setOnClickListener(this);
        mPhoneSuppButton.setOnClickListener(this);

        //Start loader
        getLoaderManager().initLoader(2, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                openEditorActivtiy();
                return true;
            case R.id.action_delete:
                Utils.showDeleteConfirmationDialog(this, mCurrentProductUri);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //We need all columns this time
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND,
                ProductContract.ProductEntry.COLUMN_PRODUCT_CATEGORY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT,
                ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };
        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //If cursor has data move to first and read
        if (cursor.moveToFirst()) {
            //Get index of column in cursor
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int brandColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_BRAND);
            int categoryColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int discountColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE_DISCOUNT);
            int stockColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
            int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            int suppNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int suppEmailColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int suppPhoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            //Get data from column using column index and store them in variables
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

            //Present the data to user
            mNameText.setText(name);
            mBrandText.setText(brand);
            String categoryString;
            if (category == ProductContract.ProductEntry.CATEGORY_MOBILE)
                categoryString = getString(R.string.category_mobile);
            else if (category == ProductContract.ProductEntry.CATEGORY_LAPTOP)
                categoryString = getString(R.string.category_laptop);
            else categoryString = getString(R.string.category_television);
            mCategoryText.setText(categoryString);
            mPriceText.setText(String.valueOf(price));
            mDiscountText.setText(String.valueOf(discount));
            mStockText.setText(String.valueOf(stock));
            Uri uri = Uri.parse(image);
            mImageView.setImageURI(uri);
            mSupplierNameText.setText(suppName);
            mSupplierEmailText.setText(suppEmail);
            mSupplierPhoneText.setText(suppPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameText.setText("");
        mBrandText.setText("");
        mCategoryText.setText("");
        mPriceText.setText("");
        mDiscountText.setText("");
        mStockText.setText("");
        mImageView.setImageURI(null);
        mSupplierNameText.setText("");
        mSupplierEmailText.setText("");
        mSupplierPhoneText.setText("");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.details_button_dec_stock:
                changeStock(-1);
                break;
            case R.id.details_button_inc_stock:
                changeStock(1);
                break;
            case R.id.details_button_email_supp:
                emailSupplier();
                break;
            case R.id.details_button_phone_supp:
                callSupplier();
                break;
        }
    }

    /**
     * Helper method to increment and decrement stock
     * @param value
     */
    private void changeStock(int value) {
        //Get the previous value first
        int prevValue = Integer.valueOf(mStockText.getText().toString());

        //Detemine if previous value is >0(for decrement) & previous value >=0 (for increment)
        if ((prevValue > 0) || (prevValue >= 0 && value > 0)) {
            //Add the new value. Make key-pair data and update
            value += prevValue;
            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, value);
            getContentResolver().update(mCurrentProductUri, contentValues, null, null);
        }
    }

    /**
     * Helper method to contact supplier via E-Mail
     * Starts email app if available
     */
    private void emailSupplier() {
        String email = mSupplierEmailText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.toast_no_email_app), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to contact Supplier via Call
     */
    private void callSupplier() {
        String phoneNumber = mSupplierPhoneText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Helper method to open {@link EditorActivity} when "Edit" icon in action bar is tapped
     */
    private void openEditorActivtiy() {
        Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
        intent.setData(mCurrentProductUri);
        startActivity(intent);
    }
}
