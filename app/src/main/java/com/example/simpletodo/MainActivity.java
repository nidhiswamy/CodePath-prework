package com.example.simpletodo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    ArrayList items;
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etitem);
        rvItems = findViewById(R.id.rvItems);

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
                            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
                            items.set(position, itemText);
                            itemsAdapter.notifyItemChanged(position);
                            saveItems();
                            Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.w("MainActivity", "Unknown call to onActivityResult");
                        }
                    }
                });

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        ActivityResultLauncher<Intent> EditActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    String itemText = data.getStringExtra(KEY_ITEM_TEXT);
                    int position = data.getExtras().getInt(KEY_ITEM_POSITION);
                    items.set(position, itemText);
                    itemsAdapter.notifyItemChanged(position);
                    saveItems();
                    Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.w("MainActivity", "Unknown call to onActivityResult");
                }
            }
        });

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at position " + position);
                // create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass the relevant data
                i.putExtra(KEY_ITEM_TEXT, (String) items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                EditActivityLauncher.launch(i);
            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that we have inserted an item
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }
/*
    // handle the result of the edit activity
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // Extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            // Update the model at the right position with new item text
            items.set(position, itemText);
            // Notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // Persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }
*/
    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }
    // This function will load items by reading every line of the .txt file
    private void loadItems() {
        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }
        catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    
    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            org.apache.commons.io.FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}