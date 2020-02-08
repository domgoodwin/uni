package com.goodwind.coursework;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class HolidayFile {

    private String fileDir;
    private Context ctx;
    private Activity activity;

    public HolidayFile(String dir, Context context, Activity act){
        fileDir = dir;
        ctx = context;
        activity = act;
    }

    public JSONObject getHolidayByIndex(int index){
        try{
            return getHolidaysArray().getJSONObject(index);
        } catch (JSONException e){
            Log.e("add fragment", "JSON Array Parse error: " + e.getMessage());
        }
        return null;
    }

    public JSONArray getHolidaysArray(){
        JSONArray jArr = new JSONArray();
        try{
            jArr = getHolidays().getJSONArray("holidays");
        } catch (JSONException e){
            Log.e("add fragment", "JSON Array Parse error: " + e.getMessage());
        }
        return jArr;

    }


    public JSONObject getHolidays(){
        String holidaysRaw = readHolidaysFile();
        Log.i("add fragment", "Holidays file input: " + holidaysRaw);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(holidaysRaw);
        } catch (JSONException e ){
            Log.e("add fragment", "JSON Parse error: " + e.getMessage());
        }
        try{
            if (! jObj.has("holidays")){
                jObj.put("holidays", new JSONArray());
            }
        } catch (JSONException e){
            Log.e("add fragment", "JSON Array Parse error: " + e.getMessage());
        }


        return jObj;
    }

    public String readHolidaysFile(){
        File file = new File(activity.getFilesDir(), fileDir);
        String out = "";
        if (!file.exists()){
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                out = "{}";
                bw.write(out);
                bw.close();
            } catch (IOException e){
                Log.e("add fragment", "Initial file creation error: "+ e.getMessage());
            }
        } else {
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(ctx.openFileInput(fileDir)));
                StringBuffer output = new StringBuffer();
                String line;
                while ((line = bReader.readLine()) != null){
                    output.append(line + "\n");
                }
                out = output.toString();
                bReader.close();


            }catch (FileNotFoundException e) {
                Log.e("add fragment", "File not found: " + e.getMessage());
            } catch ( IOException e){
                Log.e("add fragment", "File read exception: " + e.getMessage());
            }
        }
        if (out.equals("")|| out == null){
            out = "{}";
        }

        return out;
    }
}
