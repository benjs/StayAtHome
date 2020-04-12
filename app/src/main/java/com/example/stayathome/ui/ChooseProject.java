package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.MainAdapter;
import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.server.JsonTreeApi;
import com.example.stayathome.server.RealTree;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChooseProject extends AppCompatActivity {

    RecyclerView locationsRecView;
    RecyclerView.LayoutManager locationsLayout;
    RecyclerView.Adapter locationsAdapter;
    ArrayList<String> locations;
    public static String selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_project);

        //get ssid
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //connect to database
        final ArrayList<Tree> plantableTrees = new ArrayList<>();
        final TreeViewModel treeViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TreeViewModel.class);
        treeViewModel.getTrees().observe(this, new Observer<List<Tree>>() {
            @Override
            public void onChanged(List<Tree> trees) {
                Log.i("Choose Project: ", "DB intreaction");
                if (wifiManager != null) {
                    for (Tree tree : trees) {
                        if (tree.getWifi().equals(wifiManager.getConnectionInfo().getSSID())) {
                            plantableTrees.add(tree);
                        }
                    }
                }
            }
        });

        //locations for user to choose
        locations = new ArrayList<>();
        locations.add("Karlsruhe");
        locations.add("Jena");
        locations.add("Weimar");
        locations.add("Frankfurt");
        locations.add("Berlin");

        buildRecyclerView();

        Button confirmProjectBtn = (Button) findViewById(R.id.confirmProjectBtn);
        confirmProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation == null) {
                    Toast.makeText(getApplicationContext(), "You need to select a location", Toast.LENGTH_LONG).show();
                } else {
                    //TO DO: store user name at first launch
                    //TO DO: send selection to server

                    //update # grown virtual trees
                    int virtualTreesLimit = 2;
                    for (int i = 0; i < virtualTreesLimit; i++) {
                        plantableTrees.get(i).setPlantable(false);
                        treeViewModel.update(plantableTrees.get(i));
                    }
                    //return to grwon trees activity
                    Toast.makeText(getApplicationContext(), "Tree planted in " + selectedLocation, Toast.LENGTH_LONG).show();
                    Intent confirmProject = new Intent(ChooseProject.this, GrownTrees.class);
                    startActivity(confirmProject);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });
    }

    //send tree to server
    private void sendTree() {
        //TO DO: get base URL
        //create retrofit instance
        String baseURL = "";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonTreeApi jsonTreeApi = retrofit.create(JsonTreeApi.class);

        //create tree to send
        SharedPreferencesHelper prefHelper = new SharedPreferencesHelper(getApplicationContext());
        RealTree realTree = new RealTree(prefHelper.retrieveString("user_name"), selectedLocation);
        //call object for the request
        Call<RealTree> call = jsonTreeApi.creratePost(realTree);
        call.enqueue(new Callback<RealTree>() {
            @Override
            public void onResponse(Call<RealTree> call, Response<RealTree> response) {
                Toast.makeText(ChooseProject.this, "plant instruction successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<RealTree> call, Throwable t) {
                Toast.makeText(ChooseProject.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    //locations are displayed here
    public void buildRecyclerView() {
        locationsRecView = (RecyclerView) findViewById(R.id.locationsRecView);
        locationsRecView.setHasFixedSize(true);
        locationsLayout = new LinearLayoutManager(this);
        locationsAdapter = new MainAdapter(locations); //use strings stored in locations

        locationsRecView.setLayoutManager(locationsLayout);
        locationsRecView.setAdapter(locationsAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
