package com.hcr2bot.statussaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hcr2bot.statussaver.databinding.ActivityInstagramBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Instagram extends AppCompatActivity {

    private ActivityInstagramBinding binding;
    private Instagram activity;
    String videoUrl, video;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_instagram);

        activity = this;

        dialog = new ProgressDialog(this);
        dialog.setMessage("working on your request...");
        dialog.setCancelable(false);

        binding.instaDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();


                if (binding.instaurl.getText().toString().contains("instagram")) {

                    if (TextUtils.isEmpty(binding.instaurl.getText().toString())) {
                        Toast.makeText(activity, "Enter the url of video", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        RequestQueue requestQueue;
                        requestQueue = Volley.newRequestQueue(Instagram.this);

                        if (binding.instaurl.getText().toString().contains("?utm_source=ig_web_copy_link")) {
                            String partToRemove = "?utm_source=ig_web_copy_link";
                            video = binding.instaurl.getText().toString().replace(partToRemove, "");
                        } else if (binding.instaurl.getText().toString().contains("?utm_source=ig_web_button_share_sheet")) {
                            String partToRemove = "?utm_source=ig_web_button_share_sheet";
                            video = binding.instaurl.getText().toString().replace(partToRemove, "");
                        } else {
                            video = binding.instaurl.getText().toString();
                        }


                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                "https://instagram-unofficial-api.herokuapp.com/unofficial/api/video?link=" + video, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                                try {
                                    JSONArray jsonArray = response.getJSONArray("info");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject url = jsonArray.getJSONObject(i);
                                        Log.d("myapp", "" + url.getString("video_url"));
                                        videoUrl = url.getString("video_url");
                                        String shortID = url.getString("shortcode");
                                        Util.download(videoUrl, Util.RootDirectoryInstagram, activity, "Instagram " + shortID + ".mp4");

                                        binding.instaurl.setText("");

                                        dialog.dismiss();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("myapp", "Something went wrong" + error.getMessage());
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                binding.instaurl.setText("");
                                dialog.dismiss();

                            }
                        });

                        requestQueue.add(jsonObjectRequest);

                    }
                }
                else {
                    Toast.makeText(activity, "Please enter only Instagram video URL", Toast.LENGTH_SHORT).show();
                    binding.instaurl.setText("");
                    dialog.dismiss();
                }
            }
        });

        binding.paste.setOnClickListener(new View.OnClickListener() {
            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData pasteData = manager.getPrimaryClip();
            ClipData.Item item = pasteData.getItemAt(0);
            String paste = item.getText().toString();

            @Override
            public void onClick(View v) {
                binding.instaurl.setText(paste);
            }
        });


    }


}