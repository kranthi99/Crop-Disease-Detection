package com.example.plantdisease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PlantDisease extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_disease);

        Intent intent = getIntent();
        String response = intent.getStringExtra("transfer");
        try {
            JSONObject jsonObject = new JSONObject(response);
            TextView tv1 = findViewById(R.id.plantname);
            tv1.setText("  "+jsonObject.getString("plantName"));
            TextView tv2 = findViewById(R.id.diseasename);
            tv2.setText("  "+jsonObject.getString("diseaseNameReadable"));

            TextView tv3 = findViewById(R.id.causativeOrganism);
            JSONArray col = jsonObject.getJSONArray("causativeorganism_list");
            String col_string="";
            for (int i=0;i<col.length();i++)
                col_string += (i+1)+") "+col.getString(i) + "\n";
            tv3.setText(col_string);

            TextView tv4 = findViewById(R.id.irrigation);
            tv4.setText("  "+jsonObject.getString("irrigation"));

            TextView tv5 = findViewById(R.id.treatment_list);
            JSONArray treat = jsonObject.getJSONArray("treatment_list");
            String treat_string="";
            for (int i=0;i<treat.length();i++)
                treat_string += (i+1)+") "+treat.getString(i) + "\n";
            tv5.setText(treat_string);

            TextView tv6 = findViewById(R.id.symptoms_list);
            JSONArray sym = jsonObject.getJSONArray("symptoms_list");
            String sym_string="";
            for (int i=0;i<sym.length();i++)
                sym_string += (i+1)+") "+sym.getString(i) + "\n";
            tv6.setText(sym_string);

            TextView tv7 = findViewById(R.id.resistantVarients);
            JSONArray rvl = jsonObject.getJSONArray("symptoms_list");
            String rvl_string="";
            for (int i=0;i<rvl.length();i++)
                rvl_string += (i+1)+") "+rvl.getString(i) + "\n";
            tv7.setText(rvl_string);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
