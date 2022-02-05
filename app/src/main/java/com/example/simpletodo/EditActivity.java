package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    EditText edItem;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edItem = findViewById(R.id.edItem);
        btnSave = findViewById(R.id.btnSave);

        getSupportActionBar().setTitle("Edit item");

        edItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an intent which will contain the results of user modeification
                Intent intent = new Intent();
                // pass the results of editing
                intent.putExtra(MainActivity.KEY_ITEM_TEXT, edItem.getText().toString());
                intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));
                // set the result of the intent
                setResult(RESULT_OK, intent);
                // finish the activity, close the current screen and go back
                finish();
            }
        });
    }
}