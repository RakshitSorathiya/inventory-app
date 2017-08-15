package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Ankur Gupta on 13/8/17.
 * guptaankur.gupta0@gmail.com
 */

public class ProductCursorAdapter extends CursorAdapter {


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        float price = cursor.getFloat(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_STOCK));
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        TextView nameText = view.findViewById(R.id.name);
        TextView priceText = view.findViewById(R.id.price);
        TextView quantityText = view.findViewById(R.id.quantity);

        nameText.setText(name);
        priceText.setText(context.getString(R.string.currency).replace("#", String.valueOf(price)));
        quantityText.setText(String.valueOf(quantity));

        ImageButton saleButton = (ImageButton) view.findViewById(R.id.sell);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_STOCK, newQuantity);
                    context.getContentResolver().update(productUri, values, null, null);
                    Toast.makeText(context, context.getString(R.string.congratulations), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.toast_no_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
