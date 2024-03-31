package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.UtilsService.SharedPreferenceClass;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private TextView user_name, user_email;
    private CircleImageView userImage;
    private MenuItem item;

    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferenceClass = new SharedPreferenceClass(this);
        Log.d("success", "XXXXXXXXXXXX in onCreate line : 60");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferenceClass = new SharedPreferenceClass(getApplicationContext());

        View hdView = navigationView.getHeaderView(0);
        user_name = (TextView) hdView.findViewById(R.id.username);
        user_email = (TextView) hdView.findViewById(R.id.user_email);
        userImage = (CircleImageView) hdView.findViewById(R.id.avatar);


        // call feature

        // call feature


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                setDrawerClick(item.getItemId());
                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        initDrawer();

        getUserProfile();
    }

    private void getUserProfile() {

        String url = "https://kirayedar-com-android-node-api-97lb.onrender.com/api/kirayedar.com/auth/";
        final String token = sharedPreferenceClass.getValue_string("token");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        Log.d("success", "XXXXXXXXXXXX in JSON line 100");
                        JSONObject userObj = response.getJSONObject("user");
                        user_name.setText(userObj.getString("username"));
                        user_email.setText(userObj.getString("email"));

                        Picasso.with(getApplicationContext())
                                .load(userObj.getString("avatar"))
                                .placeholder(R.drawable.ic_account)
                                .error(R.drawable.ic_account)
                                .into(userImage);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params  = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy( socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


    private void initDrawer() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, new HomeFragment());
        ft.commit();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

     @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setDrawerClick(int itemId) {
        switch (itemId) {
            case 2131231218:   // Rent
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new FinishedTaskFragment()).commit();
                break;
            case 2131231217:   // Explore
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                break;
            case 2131231219:   // Logout
//                sharedPreferenceClass.clear();
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();

                // calling feature
                logoutUser();



        }
    }

    private void logoutUser() {
        int PERMISSION_CODE = 100;

        if(ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Log.d("success", "In calling feature : PERMISSION");
            ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);
        }else {
            Log.d("success", "In calling feature");
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:9130678858"));
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("success", "XXXXXXXXXXXX before Switch case of onOptnsItmsSelctd linke 215"+item.getItemId());
        switch (item.getItemId()) {
            case 1000014:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBody = "Hey try this to do app, it uses permanent saving of your task.";

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Via"));

                return true;

            case 1000029:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}