package View.Add;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cookingmagic.R;

import java.io.FileInputStream;
import java.io.IOException;

public class DishInformationAddDish extends AppCompatActivity {

    TextView tv_add_dishname;
    TextView tv_add_description;
    TextView tv_add_measurement;
    TextView tv_add_procedure;
    ImageView img_add_dishImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_dishinformation);

        tv_add_dishname = findViewById(R.id.tv_add_dishname);
        tv_add_description = findViewById(R.id.tv_add_description);
        tv_add_measurement = findViewById(R.id.tv_add_measurement);
        tv_add_procedure = findViewById(R.id.tv_add_procedure);
        img_add_dishImage = findViewById(R.id.img_add_dishImage);


        String dishName = getIntent().getStringExtra("dishName");
        String dishImage = getIntent().getStringExtra("dishImage");
        String description = getIntent().getStringExtra("description");
        String descriptionTrim = description.trim();
        String measurement = getIntent().getStringExtra("measurement");
        String procedure = getIntent().getStringExtra("procedure");


        tv_add_dishname.setText(dishName);
        tv_add_description.setText(descriptionTrim);
        tv_add_measurement.setText(measurement);
        tv_add_procedure.setText(procedure + "\n\n\n");

        System.out.println(dishImage);
        loadImageFromInternalStorage(img_add_dishImage, dishImage);

    }
    private void loadImageFromInternalStorage(ImageView imageView, String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}