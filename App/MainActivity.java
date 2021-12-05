package com.example.plantdisease;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  static final int CAMERA_REQUEST_CODE = 0;
    private ImageView takenphoto;
    private Button takephoto;
    private Button uploadphoto;
    private boolean photosetstatus=false;
    private  String url="http://192.168.43.38:8000/index/";
    Bitmap imageBitmap;
    private  static final  int PICK_IMAGE=1;
    Uri imageuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takephoto = findViewById(R.id.photobutton);
        uploadphoto = findViewById(R.id.uploadbutton);

        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });

        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadphotofunc(v);
            }
        });

        takenphoto = findViewById(R.id.image);

        takenphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagefromgallery(v);
            }
        });
    }

    public void imagefromgallery(View view){
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Crop Image"),PICK_IMAGE);
    }

    public void takePhoto(View view){
        Intent camera = new Intent();
        camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
    }
    protected void  onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==CAMERA_REQUEST_CODE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            assert extras != null;
            imageBitmap = (Bitmap)extras.get("data");
            takenphoto.setImageBitmap(imageBitmap);
            photosetstatus=true;
        }
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imageuri=data.getData();
            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageuri);
                takenphoto.setImageBitmap(imageBitmap);
                photosetstatus=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadphotofunc(View v){
        if(photosetstatus) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("response "+response);
                    Intent output = new Intent(getApplicationContext(),PlantDisease.class);
                    output.putExtra("transfer",response);
                    startActivity(output);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"error "+error.toString(),Toast.LENGTH_LONG).show();
                    System.out.println("error "+error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String encoded = imagetostring(imageBitmap);
                    params.put("image",encoded);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(this,"You cannot upload now!!",Toast.LENGTH_LONG).show();
        }
    }

    public String imagetostring(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytearray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(bytearray,Base64.DEFAULT);
        return encoded;
    }
}
