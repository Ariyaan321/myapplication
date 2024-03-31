package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapters.ExplorePropertyAdapter;
import com.example.myapplication.UtilsService.SharedPreferenceClass;
import com.example.myapplication.interfaces.RecyclerViewClickListener;
import com.example.myapplication.model.PropertyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ExploreFragment extends Fragment implements RecyclerViewClickListener {

    SharedPreferenceClass sharedPreferenceClass;
    String token;
    ExplorePropertyAdapter explorePropertyListAdapter;
    RecyclerView recyclerView;
    TextView empty_tv;

    EditText et_city,et_locality;
    ProgressBar progressBar;
    ArrayList<PropertyModel> arrayList;

    ImageView searchBtn;

    public ExploreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_string("token");

        recyclerView = view.findViewById(R.id.recycler_view);
        empty_tv = view.findViewById(R.id.empty_tv);
        progressBar = view.findViewById(R.id.progress_bar);

        et_city = view.findViewById(R.id.city);
        et_locality = view.findViewById(R.id.locality);
        searchBtn = view.findViewById(R.id.searchBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = et_city.getText().toString().toLowerCase();
                String locality = et_locality.getText().toString().toLowerCase();

                if(!city.isEmpty() && !locality.isEmpty()){
                    getSpecificProperties(city,locality);
                }
            }
        });
        getProperties();

        return view;
    }


    private void getProperties() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/explore";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        JSONArray jsonArray = response.getJSONArray("propertys");

                        if(jsonArray.length() == 0) {
                            empty_tv.setVisibility(View.VISIBLE);
                        } else {
                            empty_tv.setVisibility(View.GONE);
                            for(int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                PropertyModel propertyModel = new PropertyModel(
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("city"),
                                        jsonObject.getString("locality"),
                                        jsonObject.getString("imageUrl"),
                                        jsonObject.getString("price"),
                                        jsonObject.getString("description"),
                                        jsonObject.getBoolean("booked")

                                );
                                arrayList.add(propertyModel);
                            }

                            explorePropertyListAdapter = new ExplorePropertyAdapter(getActivity(), arrayList, ExploreFragment.this);
                            recyclerView.setAdapter(explorePropertyListAdapter);
                        }

                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;

//                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    JSONObject errorObject = new JSONObject(body);


                    if(errorObject.getString("msg").equals("Token not valid")) {
                        sharedPreferenceClass.clear();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        Toast.makeText(getActivity(), "Session expired", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getActivity(), errorObject.getString("msg") , Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    // exception
                    e.printStackTrace();
                }


                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        // set retry policy
        int socketTime = 6000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    private void getSpecificProperties(String city,String locality) {

        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> body = new HashMap<>();
        body.put("city", "aa");
        body.put("locality","aaa");
        Log.d("inputs",body.toString());
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/explore/search";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("inputs",response.toString());
                try {
                    if(response.getBoolean("success")) {
                        JSONArray jsonArray = response.getJSONArray("propertys");
                        Log.d("specific property",response.getString("propertys"));
                        Log.d("token",token);
                        if(jsonArray.length() == 0) {
                            empty_tv.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            empty_tv.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            for(int i = 0; i < jsonArray.length(); i ++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                PropertyModel propertyModel = new PropertyModel(
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("city"),
                                        jsonObject.getString("locality"),
                                        jsonObject.getString("imageUrl"),
                                        jsonObject.getString("price"),
                                        jsonObject.getString("description"),
                                        jsonObject.getBoolean("booked")

                                );
                                arrayList.add(propertyModel);
                            }

                            explorePropertyListAdapter = new ExplorePropertyAdapter(getActivity(), arrayList, ExploreFragment.this);
                            recyclerView.setAdapter(explorePropertyListAdapter);
                        }

                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;

//                final String statusCode = String.valueOf(error.networkResponse.statusCode);

                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    JSONObject errorObject = new JSONObject(body);


                    if(errorObject.getString("msg").equals("Token not valid")) {
                        sharedPreferenceClass.clear();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        Toast.makeText(getActivity(), "Session expired", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getActivity(), errorObject.getString("msg") , Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException | JSONException e) {
                    // exception
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        // set retry policy
        int socketTime = 6000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    private void BookProperty(String id,final int position) {
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/"+id;
        HashMap<String, String> body = new HashMap<>();
        body.put("booked", "true");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                arrayList.remove(position);
                                getProperties();

                                explorePropertyListAdapter.notifyItemRemoved(position);
                                Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public void showContactDialog(final String id, final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Contact Broker")
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BookProperty(id, position);
                        dialog.dismiss();
                        CallBroker();
                    }
                });
            }
        });

        dialog.show();
    }


    private void CallBroker() {
        int PERMISSION_CODE = 100;
        Context context = getContext();
        Activity activity = getActivity();
        if(ContextCompat.checkSelfPermission( context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Log.d("success", "In calling feature : PERMISSION");
            ActivityCompat.requestPermissions( activity , new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);
        }else {
            Log.d("success", "In calling feature");
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:7448192752"));
            startActivity(i);
        }
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Position " + arrayList.get(position).getId() , Toast.LENGTH_SHORT).show();
        showContactDialog(arrayList.get(position).getId(), position);
    }

    @Override
    public void onLongItemClick(int position) {

    }

    @Override
    public void onEditButtonClick(int position) {

    }

    @Override
    public void onDeleteButtonClick(int position) {

       //
    }

    @Override
    public void onDoneButtonClick(int position) {

    }
}