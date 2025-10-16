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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Bitmap INITIAL_BITMAP;
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

    // Méthode déclenchée par le bouton de téléversement
    public void onUpload(View view) {
        imagePickerLauncher.launch("image/*");
    }

    // Méthode déclenchée par le bouton retour
    public void onReverse(View view) {
        ImageView imageView = findViewById(R.id.imageView3);

        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Aucune image chargée", Toast.LENGTH_SHORT).show();
            return;
        }

        imageView.setImageBitmap(INITIAL_BITMAP);
    }

    // Charge un bitmap mutable depuis une URI et l'affiche
    private void ChargerImage(Uri imageUri) {
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inMutable = true;

            Bitmap bm = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imageUri),
                    null,
                    option
            );

            if (bm != null) {
                INITIAL_BITMAP = bm;
                ImageView imageView = findViewById(R.id.imageView3);
                imageView.setImageBitmap(bm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////  MENU OPTIONS ///////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.action_mirror_horizontal) { // miroir horizontal
            applyBitmapTransformation(this::mirrorHorizontal);
            return true;
        }

        else if (id==R.id.action_mirror_vertical) { // miroir vertical
            applyBitmapTransformation(this::mirrorVertical);
            return true;
        }

        else if (id==R.id.action_rotate_clockwise) { // rotation horaire
            applyBitmapTransformation(this::rotateClockwise);
            return true;
        }

        else if (id==R.id.action_rotate_counterclockwise) { // rotation anti-horaire
            applyBitmapTransformation(this::rotateCounterClockwise);
            return true;
        }

        return false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////  MENU CONTEXTUEL ///////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.action_invert_colors) { // couleur inversée
            applyBitmapTransformation(this::invertColors);
            return true;
        }

        else if (id==R.id.action_grayscale) { // niveau de gris
            applyBitmapTransformation(this::grayscale);
            return true;
        }

        else return super.onContextItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    // =========================================================
    // Application transformation au bitmap actuel
    // =========================================================
    private void applyBitmapTransformation(BitmapTransformer transformer) {
        ImageView image = findViewById(R.id.imageView3);

        if (image.getDrawable() == null) {
            Toast.makeText(this, "Aucune image chargée", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        if (bitmap == null) {
            Toast.makeText(this, "Impossible de récupérer l'image", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap newBitmap = transformer.transform(bitmap);
        image.setImageBitmap(newBitmap);
    }

    // Interface pour les transformations
    private interface BitmapTransformer {
        Bitmap transform(Bitmap src);
    }

    // =========================================================
    // Miroir horizontal
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
    // Miroir vertical
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
    // Inverser les couleurs
    // =========================================================
    private Bitmap invertColors(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap inverted = src.copy(Objects.requireNonNull(src.getConfig()), true);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = src.getPixel(x, y);

                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int invertedColor = (alpha << 24) | ((255 - red) << 16) | ((255 - green) << 8) | (255 - blue);
                inverted.setPixel(x, y, invertedColor);
            }
        }

        return inverted;
    }

    // =========================================================
    // Gris
    // =========================================================
    private Bitmap grayscale(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap toGrayscale = src.copy(Objects.requireNonNull(src.getConfig()), true);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = src.getPixel(x, y);

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
    // Rotation horaire
    // =========================================================
    private Bitmap rotateClockwise(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap rotated = Bitmap.createBitmap(height, width, Objects.requireNonNull(src.getConfig()));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rotated.setPixel(height - 1 - y, x, src.getPixel(x, y));
            }
        }
        return rotated;
    }

    // =========================================================
    // Rotation anti-horaire
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
