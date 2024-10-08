package com.example.atha2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editName, editEmail, editId;
    Button btnAddData, btnViewAll, btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        editName = findViewById(R.id.editText_name);
        editEmail = findViewById(R.id.editText_email);
        editId = findViewById(R.id.editText_id);
        btnAddData = findViewById(R.id.button_add);
        btnViewAll = findViewById(R.id.button_view);
        btnUpdate = findViewById(R.id.button_update);
        btnDelete = findViewById(R.id.button_delete);

        addData();
        viewAll();
        updateData();
        deleteData();
    }

    public void addData() {
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();

                if (!isValidId(id)) {
                    Toast.makeText(MainActivity.this, "ID harus 10 digit angka", Toast.LENGTH_LONG).show();
                    return;
                }

                if (myDb.idExists(id)) {
                    Toast.makeText(MainActivity.this, "ID sudah ada. Gunakan ID lain.", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isInserted = myDb.insertData(id, name, email);
                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                    clearInputFields();
                } else {
                    Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void viewAll() {
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    showMessage("Error", "No data found");
                    return;
                }

                StringBuilder buffer = new StringBuilder();
                while (res.moveToNext()) {
                    buffer.append("NRP: ").append(res.getString(0)).append("\n");
                    buffer.append("Name: ").append(res.getString(1)).append("\n");
                    buffer.append("Email: ").append(res.getString(2)).append("\n\n");
                }

                showMessage("Data", buffer.toString());
                clearInputFields();
            }
        });
    }

    public void updateData() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();

                if (!isValidId(id)) {
                    Toast.makeText(MainActivity.this, "NRP harus 10 digit angka", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean isUpdate = myDb.updateData(id, name, email);
                if (isUpdate) {
                    Toast.makeText(MainActivity.this, "Data Updated", Toast.LENGTH_LONG).show();
                    clearInputFields();
                } else {
                    Toast.makeText(MainActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteData() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();

                if (!isValidId(id)) {
                    Toast.makeText(MainActivity.this, "NRP harus 10 digit angka", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialogStyle);

                // Set title with custom color
                SpannableString spannableTitle = new SpannableString("Konfirmasi Penghapusan");
                spannableTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#D5A021")), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(spannableTitle);

                // Set message with custom color
                SpannableString spannableMessage = new SpannableString("Apakah Anda yakin ingin menghapus data dengan NRP: " + id + "?");
                spannableMessage.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setMessage(spannableMessage);

                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer deletedRows = myDb.deleteData(id);
                        if (deletedRows > 0) {
                            Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                            clearInputFields();
                        } else {
                            Toast.makeText(MainActivity.this, "Data not Found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
        builder.setCancelable(true);

        // Set title with custom color
        SpannableString spannableTitle = new SpannableString(title);
        spannableTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#D5A021")), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setTitle(spannableTitle);

        // Set message with custom color
        SpannableString spannableMessage = new SpannableString(Message);
        spannableMessage.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setMessage(spannableMessage);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button color after showing the dialog
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#D5A021"));
    }

    private boolean isValidId(String id) {
        return id.matches("\\d{10}");
    }

    private void clearInputFields() {
        editId.setText("");
        editName.setText("");
        editEmail.setText("");
    }
}