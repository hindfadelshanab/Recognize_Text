package com.example.textrecognitionfromcamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView ;
    Button buttonCaptureImage , buttonDetectText;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.text);
        buttonCaptureImage = findViewById(R.id.btn_capture_image);
        buttonDetectText = findViewById(R.id.btn_detect_text);
        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                textView.setText("");

            }
        });
        
        buttonDetectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTextFromImage();
            }
        });
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
             imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
          displyTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error"+e.getMessage() ,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displyTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0){
            Toast.makeText(getApplicationContext(),"No Text Found in Image" ,Toast.LENGTH_LONG).show();

        }else {
         for (FirebaseVisionText.Block bb : firebaseVisionText.getBlocks()){
             String text = bb.getText();
             textView.setText(text);
         }
        }
    }
}