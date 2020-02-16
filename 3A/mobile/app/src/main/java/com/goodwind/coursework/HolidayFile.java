package com.goodwind.coursework;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayFile {

    private String fileDir;
    private Context ctx;
    private Activity activity;
    static public String holidaySaveLocation = "holidays.json";

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

    public JSONObject getHolidayPlaceByIndex(int holIndex, int placeIndex){
        JSONObject holiday = getHolidayByIndex(holIndex);
        try{
            return holiday.getJSONArray("places").getJSONObject(placeIndex);
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

    public Map<Integer, String> getPlaces(int holidayIndex){
        HashMap<Integer, String> places = new HashMap<Integer, String>();
        try {
            JSONObject holiday = getHolidayByIndex(holidayIndex);
            JSONArray placesJSON = holiday.getJSONArray("places");
            for (int i = 0; i < placesJSON.length(); i++) {
                JSONObject place = placesJSON.getJSONObject(i);
                places.put(i, place.getString("name"));
            }
        } catch (JSONException e) {
            System.out.println("Exception getting places: "+ e.getMessage());
        }
        return places;
    }

    public JSONObject getImage(int holidayIndex, int placeIndex, int imageIndex) {
        try {
            if (placeIndex == -1){
                return getHolidayByIndex(holidayIndex).getJSONArray("images").getJSONObject(imageIndex);
            } else {
                return getHolidayPlaceByIndex(holidayIndex, placeIndex).getJSONArray("images").getJSONObject(imageIndex);
            }
        }
        catch (JSONException e) {
            Log.e("file", e.getMessage());
            return null;
        }
    }

    public void updateHoliday(JSONObject holiday, int holidayIndex, Activity act){
        Log.d("file", "Updating holiday for index: "+holidayIndex);
        JSONObject holidays = getHolidays();
        try {
            JSONArray holidaysArr = holidays.getJSONArray("holidays");
            holidaysArr.put(holidayIndex, holiday);
            holidays.put("holidays", holidaysArr);
        } catch (JSONException e) {
            System.out.println("Exception updating holiday: "+ e.getMessage());
        }

        saveHolidays(holidays, act);

    }

    public void deleteHoliday(int holidayIndex, Activity act){
        JSONObject holidays = getHolidays();
        try {
            JSONArray holidaysArr = holidays.getJSONArray("holidays");
            holidaysArr.remove(holidayIndex);

        } catch (JSONException e) {
            System.out.println("Exception deleting holiday: "+ e.getMessage());
        }
        saveHolidays(holidays, act);
    }

    public void deletePlace(int holidayIndex, int placeIndex, Activity act){
        JSONObject holidays = getHolidays();
        JSONObject holiday = getHolidayByIndex(holidayIndex);
        try {
            JSONArray placesArr = holiday.getJSONArray("places");
            placesArr.remove(placeIndex);

        } catch (JSONException e) {
            System.out.println("Exception deleting place: "+ e.getMessage());
        }

        saveHolidays(holidays, act);
    }

    public boolean saveHolidays(JSONObject holidaysJSON, Activity act){
        String fileName = holidaySaveLocation;

        boolean successCreate = false;
        try {

            FileOutputStream fileOut = act.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outStream = new OutputStreamWriter(fileOut);
            outStream.write(holidaysJSON.toString());
            outStream.flush();
            outStream.close();
            successCreate = true;
        } catch (java.io.IOException e) {
            System.out.println("Exception creating file: "+ e.getMessage());
        }
        return successCreate;
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


    public List<Bitmap> getImages(){
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        try {
            JSONArray allHolidays = getHolidaysArray();
            ArrayList<String> filesToGet = new ArrayList<>();
            // For all holidays
            for (int i = 0; i < allHolidays.length(); i++) {
                JSONArray imagesToGet = allHolidays.getJSONObject(i).getJSONArray("images");
                // get all holiday images
                for (int j = 0; j < imagesToGet.length(); j++) {
                    filesToGet.add(imagesToGet.getJSONObject(j).getString("file"));
                }
                JSONArray places = allHolidays.getJSONObject(i).getJSONArray("places");
                for (int j = 0; j < places.length(); j++) {
                    JSONArray placesImagesToGet = places.getJSONObject(j).getJSONArray("images");
                    for (int k = 0; k < placesImagesToGet.length(); k++) {
                        filesToGet.add(placesImagesToGet.getJSONObject(k).getString("file"));
                    }
                }
            }


            for (int i = 0; i < filesToGet.size(); i++) {
                String filePath = filesToGet.get(i);
                Bitmap b = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath), 200, 200);
                images.add(b);
            }
        } catch (JSONException e) {
            Log.e("getImages()", e.getMessage());
        }
        return images;
    }

    public List<String> getImageFilePaths(){
        ArrayList<String> images = new ArrayList<String>();
        try {
            JSONArray allHolidays = getHolidaysArray();
            ArrayList<String> filesToGet = new ArrayList<>();
            // For all holidays
            for (int i = 0; i < allHolidays.length(); i++) {
                JSONArray imagesToGet = allHolidays.getJSONObject(i).getJSONArray("images");
                // get all holiday images
                for (int j = 0; j < imagesToGet.length(); j++) {
                    filesToGet.add(imagesToGet.getJSONObject(j).getString("file"));
                }
                JSONArray places = allHolidays.getJSONObject(i).getJSONArray("places");
                for (int j = 0; j < places.length(); j++) {
                    JSONArray placesImagesToGet = places.getJSONObject(j).getJSONArray("images");
                    for (int k = 0; k < placesImagesToGet.length(); k++) {
                        filesToGet.add(placesImagesToGet.getJSONObject(k).getString("file"));
                    }
                }
            }


            for (int i = 0; i < filesToGet.size(); i++) {
                images.add(filesToGet.get(i));
            }
        } catch (JSONException e) {
            Log.e("getImageFilePaths", e.getMessage());
        }
        return images;
    }

    public List<Bitmap> getImages(JSONObject holiday, int placeIndex){
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        try {
            JSONArray imagesToGet;
            if (placeIndex == -1) {
                imagesToGet = holiday.getJSONArray("images");
            } else {
                imagesToGet = holiday.getJSONArray("places").getJSONObject(placeIndex).getJSONArray("images");
            }
            for (int i = 0; i < imagesToGet.length(); i++) {
                JSONObject imageJSON = imagesToGet.getJSONObject(i);
                Bitmap b = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageJSON.getString("file")), 200, 200);
                images.add(b);
            }
        } catch (JSONException e) {
            Log.e("", e.getMessage());
        }
        return images;
    }

    public List<String> getImageFilePaths(JSONObject holiday, int placeIndex){
        ArrayList<String> imageFilePaths = new ArrayList<String>();
        try {
            JSONArray imagesToGet;
            if (placeIndex == -1) {
                imagesToGet = holiday.getJSONArray("images");
            } else {
                imagesToGet = holiday.getJSONArray("places").getJSONObject(placeIndex).getJSONArray("images");
            }
            for (int i = 0; i < imagesToGet.length(); i++) {
                JSONObject imageJSON = imagesToGet.getJSONObject(i);
                imageFilePaths.add(imageJSON.getString("file"));
            }
        } catch (JSONException e) {
            Log.e("", e.getMessage());
        }
        return imageFilePaths;
    }

    public  HashMap<LatLng,String> getAllPlaces(){
        JSONArray allHolidays = getHolidaysArray();
        HashMap<LatLng, String> placePoints = new HashMap<>();
        try{
            // For all holidays
            for (int i = 0; i < allHolidays.length(); i++) {
                JSONArray places = allHolidays.getJSONObject(i).getJSONArray("places");
                // Get all locations from places
                for (int j = 0; j < places.length(); j++) {
                    JSONObject loc = places.getJSONObject(j).getJSONObject("location");
                    placePoints.put(new LatLng(loc.getDouble("lat"), loc.getDouble("long")), places.getJSONObject(j).getString("name"));
                }
            }
        } catch (JSONException e) {
            Log.d("file", e.getMessage());
        }
        return placePoints;

    }
}
