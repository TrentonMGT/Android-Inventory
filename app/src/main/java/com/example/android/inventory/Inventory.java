package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.util.Random;

public class Inventory extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = Inventory.class.getSimpleName();
    /**
     * Identifier for the inventory data loader
     */
    private static final int INVENTORY_LOADER = 0;
    //General Product QUERY PROJECTION
    public final String[] PRODUCT_COLS = {
            InventoryEntry._ID,
            InventoryEntry.COL_NAME,
            InventoryEntry.COL_QUANTITY,
            InventoryEntry.COL_PRICE,
            InventoryEntry.COL_DESCRIPTION,
            InventoryEntry.COL_ITEMS_SOLD,
            InventoryEntry.COL_SUPPLIER,
            InventoryEntry.COL_PICTURE
    };
    /**
     * Adapter List View
     */
    private InventoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inventory.this, InventoryEditor.class);
                startActivity(intent);
            }
        });


        ListView inventoryListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(Inventory.this, InventoryEditor.class);

                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);

            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                PRODUCT_COLS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertNewRandomData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from products database");
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertNewRandomData() {
        //randomize the data for the activity that will insert
        Random random = new Random();
        String productName = "NewProduct " + random.nextInt(40000 - 100);
        int quantity = random.nextInt(40 - 10);
        float price = random.nextInt(200 - 10);

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COL_NAME, productName);
        values.put(InventoryEntry.COL_QUANTITY, quantity);
        values.put(InventoryEntry.COL_PRICE, price);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link ItemEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri uri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

    }
}
