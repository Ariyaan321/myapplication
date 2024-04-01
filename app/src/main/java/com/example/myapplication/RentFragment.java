
package com.example.myapplication;

import static java.lang.Integer.valueOf;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapters.PropertyListAdapter;
import com.example.myapplication.UtilsService.SharedPreferenceClass;
import com.example.myapplication.interfaces.RecyclerViewClickListener;
import com.example.myapplication.model.PropertyModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RentFragment extends Fragment implements RecyclerViewClickListener {
    FloatingActionButton floatingActionButton;
    SharedPreferenceClass sharedPreferenceClass;
    String token;
    PropertyListAdapter propertyListAdapter;
    RecyclerView recyclerView;
    TextView empty_tv;
    ProgressBar progressBar;
    ArrayList<PropertyModel> arrayList;

    public RentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent, container, false);

        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_string("token");

        floatingActionButton = view.findViewById(R.id.add_task_btn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
            // take input for new property to rent
        });

        recyclerView = view.findViewById(R.id.recycler_view_rent);
        empty_tv = view.findViewById(R.id.empty_tv);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        getProperties();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            showDeleteDialog(arrayList.get(position).getId(), position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    public void showAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null);

        final EditText title_field = alertLayout.findViewById(R.id.title);
        final  EditText city_field = alertLayout.findViewById(R.id.city);
        final  EditText locality_field = alertLayout.findViewById(R.id.locality);
        final  EditText img_url_field = alertLayout.findViewById(R.id.image_url);
        final  EditText price_field = alertLayout.findViewById(R.id.price);
        final EditText description_field = alertLayout.findViewById(R.id.description);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("Rent property")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInter) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = title_field.getText().toString();
                        String city =  city_field.getText().toString().toLowerCase();
                        String locality = locality_field.getText().toString().toLowerCase();
                        String image_url = img_url_field.getText().toString();
                        String price = price_field.getText().toString();
                        String description = description_field.getText().toString();
                        if(!TextUtils.isEmpty(title)) {
                            addProperty(title,city,locality,image_url,price,description);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Please enter title...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }
    public void showUpdateDialog(final  String  id, String title,String city, String locality, String img_url,String price ,String description)  {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null);

        final EditText title_field = alertLayout.findViewById(R.id.title);
        final  EditText city_field = alertLayout.findViewById(R.id.city);
        final  EditText locality_field = alertLayout.findViewById(R.id.locality);
        final  EditText img_url_field = alertLayout.findViewById(R.id.image_url);
        final  EditText price_field = alertLayout.findViewById(R.id.price);
        final EditText description_field = alertLayout.findViewById(R.id.description);

        title_field.setText(title);
        city_field.setText(city);
        locality_field.setText(locality);
        img_url_field.setText(img_url);
        price_field.setText(price);
        description_field.setText(description);


        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("Update Property")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = title_field.getText().toString();
                        String city = city_field.getText().toString().toLowerCase();
                        String locality = locality_field.getText().toString().toLowerCase();
                        String img_url = img_url_field.getText().toString();
                        String price = price_field.getText().toString();
                        String description = description_field.getText().toString();

                        updateProperty(id, title,city,locality,img_url,price, description);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }
    public void showDeleteDialog(final String id, final  int position) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Do you want to delete the task ?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getProperties();
                    }
                })
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProperty(id, position);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }




    // Get all property task method and update propertyListAdapter to change list
    public void getProperties() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/";

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
                            // adding property to list
                            propertyListAdapter = new PropertyListAdapter(getActivity(), arrayList, RentFragment.this);
                            recyclerView.setAdapter(propertyListAdapter);
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


                NetworkResponse response = error.networkResponse;

                if (error == null || error.networkResponse == null) {
                    return;
                }

                String body;

//               final String statusCode = String.valueOf(error.networkResponse.statusCode);

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
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    // Delete Property Method
    private void deleteProperty(final String id, final  int position) {
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/"+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        propertyListAdapter.notifyItemRemoved(position);
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
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
    // Add Property Task Method
    private void addProperty(String title,String city, String locality, String img_url,String price ,String description ) {
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/";

        HashMap<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("city", city);
        body.put("locality",locality);
        body.put("imageUrl",img_url);
        body.put("price",price);
        body.put("description", description);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        getProperties();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,  "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | UnsupportedEncodingException je) {
                        je.printStackTrace();
                    }
                }
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
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    // Update Property Task Method
    private  void  updateProperty(String id, String title,String city, String locality, String img_url,String price ,String description) {
        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/"+id;
        HashMap<String, String> body = new HashMap<>();
        body.put("title", title);
        body.put("city", city);
        body.put("locality",locality);
        body.put("imageUrl",img_url);
        body.put("price",price);
        body.put("description", description);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                getProperties();
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
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }



    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Position "+ position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        showUpdateDialog(arrayList.get(position).getId(), arrayList.get(position).getTitle(), arrayList.get(position).getCity(),
                arrayList.get(position).getLocality(),arrayList.get(position).getImageUrl(),arrayList.get(position).getPrice(),arrayList.get(position).getDescription());
        Toast.makeText(getActivity(), "Position "+ position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEditButtonClick(int position) {
        showUpdateDialog(arrayList.get(position).getId(), arrayList.get(position).getTitle(), arrayList.get(position).getCity(),
                arrayList.get(position).getLocality(),arrayList.get(position).getImageUrl(),arrayList.get(position).getPrice(),arrayList.get(position).getDescription());
    }

    @Override
    public void onDeleteButtonClick(int position) {
        showDeleteDialog(arrayList.get(position).getId(), position);

    }

    @Override
    public void onDoneButtonClick(int position) {

    }
}

