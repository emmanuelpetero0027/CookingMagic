package Model.Generate;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.cookingmagic.ml.Asin;
import com.example.cookingmagic.ml.Bawang;
import com.example.cookingmagic.ml.ModelUnquant;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import BottomNavigationBar.Generate;
import Model.Database.BookmarkDatabaseOpenHelper;

public class MachineLearning {

    Context context;

    int imageSize = 224;

    public List<String> captureImage = new ArrayList<>();

    public MachineLearning(Context context){
        this.context = context;

    }

    ///////////////////////////////////////Asin////////////////////////////////////////////
    public void imageRecognition(Bitmap image){
        try {

            ModelUnquant model = ModelUnquant.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0 ,image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);
            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidence.length; i++){
                if(confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Asin (Salt)", "Bawang (Garlic)", "Carrots", "Pecho ng Manok (Chicken Breast)",
            "Pakpak (Chicken Wings)", "Beef Cubes", "Chicken drumstick", "Ginisa mix", "Dahon ng Laurel (Bay Leaves)",
            "Magic sarap", "Cooking oil", "Msg", "Paminta (Ground black pepper)", "Repolyo (Cabbage)", "Sayote (Vegetable pear)",
            "Sinigang Sampalok Mix", "Sotanghon Noodles", "Suka (Vinegar)", "Toyo (Soy sauce)", "Buong Manok (Whole Chicken)"};
            //System.out.println(classes[maxPos]);
            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidence[i] * 100);
                if(confidence[i] * 100 > 30){
                    captureImage.add(classes[i]);
                }
                else{
                }
            }
            System.out.println(s);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

        System.out.println(captureImage);
    }


    public List<String> getCaptureImage(){
        return this.captureImage;
    }

}
