package club.codeexpert.music.managers;


import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import club.codeexpert.music.R;
import club.codeexpert.music.data.SongRepository;
import club.codeexpert.music.data.db.Song;


public class ApiManager {
    private static final String TAG = "ApiManager";
    private static final String URL = "https://5rr4yhm404.execute-api.us-east-1.amazonaws.com/prod/"; // "http://music.codeexpert.club:8000/";

    SongRepository songRepository;

    public RequestQueue requestQueue;

    public RequestQueue requestQueueDownloads;

    List<String> requested;
    List<String> songsDownloaded;

    File directory;


    @Inject
    public ApiManager(SongRepository songRepository, RequestQueue requestQueue, RequestQueue requestQueueDownloads, File directory) {
        /*this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueueDownloads = Volley.newRequestQueue(context.getApplicationContext());

        songsDownloaded = SongRepository.getInstance().getIDs();
        requested = songsDownloaded;

        ContextWrapper contextWrapper = new ContextWrapper(context);
        directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
         */
        this.songRepository = songRepository;
        this.requestQueue = requestQueue;
        this.requestQueueDownloads = requestQueueDownloads;
        this.directory = directory;

        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... urlParams) {
                songsDownloaded = ApiManager.this.songRepository.getIDs();
                requested = songsDownloaded;
                return null;
            }
        };
        task.execute();
    }

    public File getDir() {
        return directory;
    }

    public void addRequested(String id) {
        if (!requested.contains(id)) {
            requested.add(id);
        }
    }

    public boolean isDownloaded(String id) {
        return songsDownloaded.contains(id);
    }

    public boolean addDownloaded(String id) {
        return songsDownloaded.add(id);
    }

    /*public void call(String type, String method, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = URL + method;

        if (errorListener == null) {
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonRequest, listener,  errorListener);
        jsonObjectRequest.setTag("play");

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (type.equals("download")) {
            requestQueueDownloads.add(jsonObjectRequest);
        } else {
            requestQueue.cancelAll("play");

            requestQueue.add(jsonObjectRequest);
        }
    }*/

    public void call(String type, String method, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener) {
        String url = URL + method;

        if (errorListener == null) {
            errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            };
        }

        // new api request
        if (!URL.contains("music.codeexpert")) {
            url = URL;
            String[] parts = method.split("\\?|/");
            String apiMethod = parts[0].equals("") ? "search" : parts[0];
            if (apiMethod.equals("search")) {
                apiMethod = "search2";
            } else if (type.equals("play") || type.equals("download")) {
                // TODO: fix this
                List<String> opts = Arrays.asList("", "2", "3");
                apiMethod = "play" + opts.get((new Random()).nextInt(opts.size()));
            }

            url += apiMethod;
            if (parts.length > 1) {
                try {
                    if (jsonRequest == null) {
                        jsonRequest = new JSONObject();
                    }
                    if (type.equals("play") || type.equals("download")) {
                        jsonRequest.put("video_id", parts[1]);
                        url += "?video_id=" + parts[1];
                    } else {
                        jsonRequest.put("k", parts[1].replace("k=", ""));
                        url += "?" + parts[1];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, listener,  errorListener);
        jsonObjectRequest.setTag("play");

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (type.equals("download")) {
            requestQueueDownloads.add(jsonObjectRequest);
        } else {
            requestQueue.cancelAll("play");

            requestQueue.add(jsonObjectRequest);
        }
    }

    public void call(String method, JSONObject jsonRequest, Response.Listener listener) {
        call("play", method, jsonRequest, listener, null);
    }

    public void callDownload(String method, JSONObject jsonRequest, Response.Listener listener) {
        call("download", method, jsonRequest, listener, null);
    }

    public void requestDownloadFile(final Song song) {
        final String songId = song.id;
        final String url = "play/" + song.id;

        if (!requested.contains(songId)) {
            requested.add(songId);

            callDownload(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray data = null;
                    Log.d("APP", "Request: " + url);
                    try {
                        if (response.has("url")) {
                            String play_url = response.getString("url");
                            if (!play_url.equals("")) {
                                Log.d("APP", play_url);

                                song.link = play_url;

                                new DownloadFile().execute(song);
                            } else {
                                Log.d("APP", String.valueOf(R.string.file_pending));
                            }
                        } else {
                            Log.d("APP", String.valueOf(R.string.file_invalid));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void deleteDownload(final Song song) {
        final String songId = song.id;
        if (requested.contains(songId)) {
            requested.remove(songId);
        }

        new RemoveDownload().execute(song);

        if (songsDownloaded.contains(songId)) {
            songsDownloaded.remove(songId);
        }
    }

    public class DownloadFile extends AsyncTask<Song, Integer, Song> {
        @Override
        protected Song doInBackground(Song... urlParams) {
            final Song song = urlParams[0];
            final String play_url = song.link;

            downloadFileToDevice(song, play_url);

            ApiManager.this.songRepository.insertAll(song);

            ApiManager.this.addDownloaded(song.id);

            return song;
        }

        private void downloadFileToDevice(Song song, String url_http) {
            int count;
            try {
                java.net.URL url = new URL(url_http);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream());

                String filename = song.id + ".mp3";
                File file =  new File(directory, filename);
                OutputStream output = new FileOutputStream(file);
                Log.d("APP", file.getAbsolutePath());

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class RemoveDownload extends AsyncTask<Song, Integer, String> {
        @Override
        protected String doInBackground(Song... urlParams) {
            final Song song = urlParams[0];
            // remove from db
            ApiManager.this.songRepository.delete(song);

            // remove from device
            String filename = song.id + ".mp3";
            File file =  new File(directory, filename);
            file.delete();

            return null;
        }
    }
}
