package com.example.tpimagerandrianalisoa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Bitmap loadedBitmap;

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

        // Lanceur pour la sélection d'image depuis la galerie
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

        ImageView imageView = findViewById(R.id.imageView3);

        // Enregistrement du menu contextuel pour le clic long
        registerForContextMenu(imageView);
    }

    // Méthode déclenchée par le bouton "Upload Image"
    public void onUploadImage(View view) {
        imagePickerLauncher.launch("image/*");
    }

    // Méthode déclenchée par le bouton "Reverse"
    public void onReverse(View view) {
        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setImageBitmap(loadedBitmap);
    }

    // Charge un bitmap mutable depuis une URI et l'affiche
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
                loadedBitmap = bm;
                ImageView imageView = findViewById(R.id.imageView3);
                imageView.setImageBitmap(bm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///  Menu d'options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_mirror_horizontal) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = mirrorHorizontal(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        }
        else if(id==R.id.action_mirror_vertical) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = mirrorVertical(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        }

        else if(id==R.id.action_rotate_clockwise) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = rotateClockwise(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        }

        else if(id==R.id.action_rotate_counterclockwise) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = rotateCounterClockwise(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        }

        return false;
    }

    /// Menu contextuel
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_invert_colors) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = invertColors(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        }
        else if(id==R.id.action_grayscale) {

            ImageView image = findViewById(R.id.imageView3);
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

            bitmap = grayscale(bitmap);
            image.setImageBitmap(bitmap);

            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    // =========================================================
    // Miroir horizontal : échange les pixels gauche/droite
    // =========================================================
    private Bitmap mirrorHorizontal(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap mirrored = src.copy(Objects.requireNonNull(src.getConfig()), true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                int leftPixel = src.getPixel(x, y);
                int rightPixel = src.getPixel(width - 1 - x, y);

                mirrored.setPixel(x, y, rightPixel);
                mirrored.setPixel(width - 1 - x, y, leftPixel);
            }
        }
        return mirrored;
    }

    // =========================================================
    // Miroir vertical : échange les pixels haut/bas
    // =========================================================
    private Bitmap mirrorVertical(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap mirrored = src.copy(Objects.requireNonNull(src.getConfig()), true);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height / 2; y++) {
                int topPixel = src.getPixel(x, y);
                int bottomPixel = src.getPixel(x, height - 1 - y);

                mirrored.setPixel(x, y, bottomPixel);
                mirrored.setPixel(x, height - 1 - y, topPixel);
            }
        }
        return mirrored;
    }

    // =========================================================
    // Inverser les couleurs pixel par pixel
    // =========================================================
    private Bitmap invertColors(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap inverted = src.copy(Objects.requireNonNull(src.getConfig()), true);

        // Parcours de chaque pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = src.getPixel(x, y);

                // Extraction des composantes ARGB
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // Inversion des couleurs
                int invertedColor = (alpha << 24) | ((255 - red) << 16) | ((255 - green) << 8) | (255 - blue);

                inverted.setPixel(x, y, invertedColor);
            }
        }

        return inverted;
    }


    // =========================================================
    // Convertir en un niveau de gris
    // =========================================================
    private Bitmap grayscale(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap toGrayscale = src.copy(Objects.requireNonNull(src.getConfig()), true);

        // Parcours de chaque pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = src.getPixel(x, y);

                // Extraction des composantes ARGB
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int m = (red + green + blue) / 3;

                int grayPixel = (alpha << 24) | (m << 16) | (m << 8) | m;

                toGrayscale.setPixel(x, y, grayPixel);
            }
        }

        return toGrayscale;
    }


    // =========================================================
    // Effectuer une rotation à 90 degrés de l’image dans le sens horaire
    // =========================================================
    private Bitmap rotateClockwise(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap rotated = Bitmap.createBitmap(height, width, Objects.requireNonNull(src.getConfig()));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Place chaque pixel à sa nouvelle position
                rotated.setPixel(height - 1 - y, x, src.getPixel(x, y));
            }
        }
        return rotated;
    }

    // =========================================================
    // Effectuer une rotation à 90 degrés de l’image dans le sens anti-horaire
    // =========================================================
    private Bitmap rotateCounterClockwise(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap rotated = Bitmap.createBitmap(height, width, Objects.requireNonNull(src.getConfig()));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rotated.setPixel(y, width - 1 - x, src.getPixel(x, y));
            }
        }
        return rotated;
    }
}