package com.example.myapplication2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.myapplication2.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class FlickrAPI {
    // wrapper class for all of our Flickr API related members
    static final String BASE_URL = "https://api.flickr.com/services/rest";
    static final String API_KEY = BuildConfig.FLICKR_KEY; //bad practice: better to fetch from server
    static final String TAG = "FlickrTag";

    private final int numPerPage = 10;

    public static void setPage(int page) {
        FlickrAPI.page = page;
    }

    static int page = 1;

    HomeFragment homeFragment;
    String TAG_NAME = "";

    public FlickrAPI(HomeFragment homeFragment, String TAG_NAME){
        this.homeFragment = homeFragment;
        this.TAG_NAME = TAG_NAME;
    }

    public void fetchLMImages(){
        String url = constructPhotoListURL();
        Log.d(TAG, "fetchPhotos: "+ url);

        // start the background task to fetch the photos--android doesn;t
        // allow network activity on the main UI thread
        FetchPhotoListAsyncTask asyncTask = new FetchPhotoListAsyncTask();
        asyncTask.execute(url);
    }

    private String constructPhotoListURL() {
        String url = BASE_URL;
        url += "?method=flickr.photos.search";
        url += "&api_key=" + API_KEY;
        url += "&tags=" + TAG_NAME;
        url += "&per_page=" + numPerPage;
        url += "&page=" + page;

        url += "&format=json";
        url += "&nojsoncallback=1";
        url += "&extras=date_taken,url_h";


        return url;
    }

    class FetchPhotoListAsyncTask extends AsyncTask<String, Void, List<LMPhoto>> {
        // executes on the background, cannot update the UI thread unless in AsyncTask method
        // 1. open url request
        // 2. download JSON response
        // 3. parse JSON response into LMPhotos objects
        @Override
        protected List<LMPhoto> doInBackground(String... strings) {
            String url = strings[0];
            List<LMPhoto> LMPhotoList = new ArrayList<>();

            try {
                URL urlObj = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) urlObj.openConnection();

                // opened url --> download JSON response
                String jsonResult = "";
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    jsonResult += (char) data;
                    data = reader.read();

                }
                Log.d(TAG, "doInBackground: "+ jsonResult);

                // parse the JSON
                JSONObject jsonObject = new JSONObject(jsonResult);
                // grab the "root" photos jsonObj
                JSONObject photosObject = jsonObject.getJSONObject("photos");
                JSONArray photoArray = photosObject.getJSONArray("photo");
                for (int i = 0; i<photoArray.length(); i++){
                    JSONObject singlePhoto = photoArray.getJSONObject(i);
                    LMPhoto lmPhoto = parseLMPhoto(singlePhoto);
                    if (lmPhoto != null){
                        LMPhotoList.add(lmPhoto);
                    }
                }

            } catch (MalformedURLException e) {
                Utility.showDialog(homeFragment.getActivity(), "Error", "No Internet");
//                throw new RuntimeException(e);
            } catch (IOException e) {
//                throw new RuntimeException(e);
                Utility.showDialog(homeFragment.getActivity(), "Error", "No Internet");
            } catch (JSONException e) {
//                throw new RuntimeException(e);
                Utility.showDialog(homeFragment.getActivity(), "Error", "No Internet");
            }

            return LMPhotoList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = homeFragment.getActivity().findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        private LMPhoto parseLMPhoto(JSONObject singlePhoto){
            LMPhoto lmPhoto = null;
            try {
                String id = singlePhoto.getString("id");
                String title = singlePhoto.getString("title");
                String dateTaken = singlePhoto.getString("datetaken");
                String photoURL = singlePhoto.getString("url_h");
                lmPhoto = new LMPhoto(id, title, dateTaken, photoURL);
            }
            catch (JSONException e){
                // do nothing
            }
            return lmPhoto;
        }
        @Override
        protected void onPostExecute(List<LMPhoto> lmPhotos) {
            super.onPostExecute(lmPhotos);
            Log.d(TAG, "onPostExe: "+lmPhotos.size());
            homeFragment.receivedLMPhotos(lmPhotos);
            ProgressBar progressBar = homeFragment.getActivity().findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
        }
    }

//    public void fetchPhotoBitmap(String photoURL) {
//        PhotoRequestAsyncTask asyncTask = new PhotoRequestAsyncTask();
//        asyncTask.execute(photoURL);
//    }
//
//    class PhotoRequestAsyncTask extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//            Bitmap bitmap = null;
//            try {
//                URL url = new URL(strings[0]);
//                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//
//                InputStream in = urlConnection.getInputStream();
//                bitmap = BitmapFactory.decodeStream(in);
//            } catch (MalformedURLException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            if (bitmap==null){
//                Log.d(TAG, "doInBackground: bitmap is null");
//                try {
//                    URL url = new URL("https://live.staticflickr.com//65535//52789946096_f9963801b4_h.jpg");
//                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                } catch(IOException e) {
//                    System.out.println(e);
//                }
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            ProgressBar progressBar = homeFragment.getActivity().findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.GONE);
//            homeFragment.receivedPhotoBitmap(bitmap);
//        }
//
//
//    }
//
}
