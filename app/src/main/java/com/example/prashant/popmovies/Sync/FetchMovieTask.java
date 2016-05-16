package com.example.prashant.popmovies.Sync;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.PreferenceChangeListener;

public class FetchMovieTask extends Fragment {

    static GridView gridview;
    static int width;
    static boolean sortByPop = true;
    static String api_key = "b7f57ee32644eb6ddfdca9ca38b5513e";
    static ArrayList<String> overviews;
    static ArrayList<String> titles;
    static ArrayList<String> dates;
    static ArrayList<String> ratings;
    static ArrayList<String> youtubes1;
    static ArrayList<String> youtubes2;
    static ArrayList<String> ids;
    static ArrayList<String> posters;
    static ArrayList<Boolean> favorited;
    static ArrayList<ArrayList<String>> comments;

    public static class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private final String LOG_TAG = ImageLoadTask.class.getSimpleName();
        private Context mContext;

        public ImageLoadTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    //storing posters into an arraylist poster
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortByPop)));
                    return posters;
                } catch (Exception e) {
                    continue;
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null && mContext != null) {
                ImageAdapter adapter = new ImageAdapter(mContext, result, width);
                gridview.setAdapter(adapter);

            }
        }

        public String[] getPathsFromAPI(boolean sortbypop)
        {
            while(true)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    if (sortbypop) {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + api_key;
                    } else {
                        urlString = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + api_key;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();

                    try {
                        overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"overview")));
                        titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"original_title")));
                        ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"vote_average")));
                        dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"release_date")));
                        ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONResult,"id")));
                        while(true)
                        {
                            youtubes1 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,0)));
                            youtubes2 = new ArrayList<String>(Arrays.asList(getYoutubesFromIds(ids,1)));
                            int nullCount = 0;
                            for(int i = 0; i< youtubes1.size(); i++)
                            {
                                if(youtubes1.get(i) == null)
                                {
                                    nullCount++;
                                    youtubes1.set(i, "no video found");
                                }
                            }
                            for(int i = 0; i < youtubes2.size(); i++)
                            {
                                if(youtubes2.get(i) == null)
                                {
                                    nullCount++;
                                    youtubes2.set(i, "no video found");
                                }
                            }
                            if(nullCount > 2) continue;
                            break;
                        }
                        comments = getReviewsFromIds(ids);
                        return getPathsFromJSON(JSONResult);

                    } catch (JSONException e) {
                        return null;
                    }
                }catch(Exception e)
                {
                    continue;
                }finally {
                    if(urlConnection != null)
                    {
                        urlConnection.disconnect();
                    }
                    if(reader != null)
                    {
                        try{
                            reader.close();
                        }catch(final IOException e)
                        {
                        }
                    }
                }


            }

        }
        public String[] getYoutubesFromIds(ArrayList<String> ids, int position)
        {
            String[] results = new String[ids.size()];
            for(int i =0; i<ids.size(); i++)
            {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String JSONResult;

                try {
                    String urlString = null;
                    urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/videos?api_key=" + api_key;


                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    //Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();
                    try {
                        results[i] = getYoutubeFromJSON(JSONResult, position);
                    } catch (JSONException E) {
                        results[i] = "no video found";
                    }
                } catch (Exception e) {

                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                        }
                    }
                }
            }
            return results;

        }
        public ArrayList<ArrayList<String>> getReviewsFromIds(ArrayList<String> ids)
        {
            outerloop:
            while(true)
            {
                ArrayList<ArrayList<String>> results = new ArrayList<>();
                for(int i =0; i<ids.size(); i++)
                {
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String JSONResult;

                    try {
                        String urlString = null;
                        urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/reviews?api_key=" + api_key;
                        URL url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        //Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }
                        if (buffer.length() == 0) {
                            return null;
                        }
                        JSONResult = buffer.toString();
                        try {
                            results.add(getCommentsFromJSON(JSONResult));
                        } catch (JSONException E) {
                            return null;
                        }
                    } catch (Exception e) {
                        continue outerloop;

                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                            }
                        }
                    }
                }
                return results;

            }
        }
        public ArrayList<String> getCommentsFromJSON(String JSONStringParam)throws JSONException{
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray reviewsArray = JSONString.getJSONArray("results");
            ArrayList<String> results = new ArrayList<>();
            if(reviewsArray.length()==0)
            {
                results.add("No reviews found for this movie.");
                return results;
            }
            for(int i = 0; i<reviewsArray.length(); i++)
            {
                JSONObject result = reviewsArray.getJSONObject(i);
                results.add(result.getString("content"));
            }
            return results;

        }
        public String getYoutubeFromJSON(String JSONStringParam, int position) throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);
            JSONArray youtubesArray = JSONString.getJSONArray("results");
            JSONObject youtube;
            String result = "no videos found";
            if(position ==0)
            {
                youtube = youtubesArray.getJSONObject(0);
                result = youtube.getString("key");
            }
            else if(position==1)
            {
                if(youtubesArray.length()>1)
                {
                    youtube = youtubesArray.getJSONObject(1);
                }
                else{
                    youtube = youtubesArray.getJSONObject(0);
                }
                result = youtube.getString("key");
            }
            return result;
        }
        public String[] getStringsFromJSON(String JSONStringParam, String param)  throws JSONException
        {
            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                if(param.equals("vote_average"))
                {
                    Double number = movie.getDouble("vote_average");
                    String rating =Double.toString(number)+"/10";
                    result[i]=rating;
                }
                else {
                    String data = movie.getString(param);
                    result[i] = data;
                }
            }
            return result;
        }
        public String[] getPathsFromJSON(String JSONStringParam) throws JSONException{

            JSONObject JSONString = new JSONObject(JSONStringParam);

            JSONArray moviesArray = JSONString.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for(int i = 0; i<moviesArray.length();i++)
            {
                JSONObject movie = moviesArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i] = moviePath;
            }
            return result;
        }
    }


}
