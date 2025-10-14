package com.example.tpimagerandrianalisoa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        TextView uriTextView = findViewById(R.id.uriView);
                        uriTextView.setText(uri.toString());

                        ChargerImage(uri);
                    }
                }
        );
    }

    // ====================================================================================
    // Méthode appelée lorsque l'utilisateur clique sur le bouton de chargement d'une image
    // ====================================================================================
    public void onUploadImage(View view) {
        imagePickerLauncher.launch("image/*");
    }

    private void ChargerImage(Uri imageUri) {
        try {
            // Préparer les options de chargement
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inMutable = true; // l’image pourra être modifiée

            // Chargement du Bitmap à partir de l’URI
            Bitmap bm = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imageUri),
                    null,
                    option
            );

            if (bm != null) {
                ImageView imageView = findViewById(R.id.imageView3); // adapte l’ID si besoin
                imageView.setImageBitmap(bm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}