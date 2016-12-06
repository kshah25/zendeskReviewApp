package com.example.kandarp.zendesktest;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kandarp.zendesktest.webservice.SatisfactionRatings;
import com.example.kandarp.zendesktest.webservice.SatisfactionRatingsApi;
import com.example.kandarp.zendesktest.webservice.UserListApi;
import com.example.kandarp.zendesktest.webservice.Users;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    //constants
    public final static String sHeaderAccept = "Accept: application/json";
    public final static int AGENT = 0;
    public final static int USER = 1;
    AlertDialog.Builder builder;
    int timerValue = 10;//time defaults to 10 seconds
    int commentDisplayOrder = 0; //0 = newest to oldest, 1 = random
    TimerTask getReviewTask;
    Timer timer;
    String authToken;
    Users allAgents, allEndUsers;
    SatisfactionRatings allSatisFactionRatings;
    HashMap<String,String> agentList = new HashMap<>();
    HashMap<String,String> endUserList = new HashMap<>();
    public static Retrofit adapter = null;
    int review_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //prepare the auth token
        String namepass = getResources().getString(R.string.auth_email)+"/token:"+getResources().getString(R.string.api_key);
        authToken = "Basic "+Base64.encodeToString(namepass.getBytes(), Base64.NO_WRAP);
        //get all good satisfaction ratings
        getAllSatisfactionRatings();
    }

    /*This function first calls satisfaction_ratings API and gets all 'good' rated reviews.
      But because the agent and end user ids are given instead of names, we need to then
      fetch the names of the agents and end users. Once we have that, the timer will start and
       show the reviews.
     */

    public void getAllSatisfactionRatings(){
        SatisfactionRatingsApi signUp = getRestAdapter(this).create(SatisfactionRatingsApi.class);
        Call<SatisfactionRatings> response = signUp.getSatisfactionRatings(authToken);
        response.enqueue(new Callback<SatisfactionRatings>() {

            @Override
            public void onResponse(Call<SatisfactionRatings> call, Response<SatisfactionRatings> response) {
                //successful response
                if (response.isSuccessful()) {
                    allSatisFactionRatings = response.body();
                    //get only unique agent and user ids
                    for(SatisfactionRatings.review r : allSatisFactionRatings.getReviewsList()){
                        if(!agentList.containsKey(String.valueOf(r.getAgentId())))
                            agentList.put(String.valueOf(r.getAgentId()),"");
                        if(!endUserList.containsKey(String.valueOf(r.getRequesterId())))
                            endUserList.put(String.valueOf(r.getRequesterId()),"");
                    }
                    //get the names of all of the agents
                    populateAgentAndUserNames(AGENT);


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("errors"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<SatisfactionRatings> call, Throwable t) {
               System.out.println("fdsf");
            }
        });
    }

    public void populateAgentAndUserNames(final int AGENT_OR_USER){
        //create a query parameter made of user id's to send up to the API. It will return an array of user objects
        String commaSeparatedUList = "";
        if(AGENT_OR_USER == AGENT) {
            for (String key : agentList.keySet()) {
                commaSeparatedUList += key + ",";
            }
        }
        else{
            for (String key : endUserList.keySet()) {
                commaSeparatedUList += key + ",";
            }
        }
        //sanity check; make sure we have a non-empty list
        if(!commaSeparatedUList.isEmpty()){
            UserListApi userListApi = getRestAdapter(this).create(UserListApi.class);
            Call<Users> response = userListApi.getAllUsers(authToken,commaSeparatedUList);
            response.enqueue(new Callback<Users>() {

                @Override
                public void onResponse(Call<Users> call, Response<Users> response) {
                    //successful response
                    if (response.isSuccessful()) {
                        if(AGENT_OR_USER == AGENT) {
                            allAgents = response.body();
                            //get all agent names and pair them with their ids
                            for (Users.user r : allAgents.getUserList()) {
                                agentList.put(String.valueOf(r.getId()), r.getName());
                            }
                            //now populate the end user names
                            populateAgentAndUserNames(USER);
                        }
                        else{
                            allEndUsers = response.body();
                            for (Users.user r : allEndUsers.getUserList()) {
                                endUserList.put(String.valueOf(r.getId()), r.getName());
                            }
                            //once we have the agents and users complete, start the timer and show reviews
                            setUpReviews();
                        }


                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.getString("errors"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Users> call, Throwable t) {
                    System.out.println("fdsf");
                }
            });

        }
    }

    public void setUpReviews(){
        reScheduleTimer(timerValue);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Settings")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EditText txt = (EditText) ((AlertDialog) dialog).findViewById(R.id.timerValueTxt);
                            if(!txt.getText().toString().isEmpty())
                                timerValue = Integer.valueOf(txt.getText().toString());
                            Spinner spn = (Spinner)((AlertDialog) dialog).findViewById(R.id.spinnerCommentOrder);
                            commentDisplayOrder = spn.getSelectedItemPosition();
                            if(commentDisplayOrder == 0)
                                review_counter = 0;//reset the counter so it starts with the latest review
                            Toast.makeText(MainActivity.this,"Settings changed.", Toast.LENGTH_SHORT).show();
                            reScheduleTimer(timerValue);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    });
            builder.setView(R.layout.settings_layout);
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void reScheduleTimer(int duration) {
        if(timer != null)
            timer.cancel();
        timer = new Timer("alertTimer",true);
        getReviewTask = new MyTimerTask();
        timer.schedule(getReviewTask,0, duration * 1000L);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    //if reviews are set for random order, chooose a random number and get the review based on it.
                    //if reviews are ordered newest to oldest, count down then timer and rollover if necessary
                    if(commentDisplayOrder == 1) {
                        Random generator = new Random();
                        review_counter = generator.nextInt(allSatisFactionRatings.getReviewsList().size());
                    }else{
                        if(review_counter == 0)
                            review_counter = allSatisFactionRatings.getReviewsList().size()-1;
                        else
                            review_counter--;
                    }
                    //get review
                    ((TextView)findViewById(R.id.ticketReviewBodyTxt)).setText("\""+allSatisFactionRatings.getReviewsList().get(review_counter).getComment()+"\"");

                    //get reviewer and date
                    ((TextView)findViewById(R.id.ticketReviewAuthorTxt)).setText("-"+endUserList.get(String.valueOf(allSatisFactionRatings.getReviewsList().get(review_counter).getRequesterId()))+
                            ", "+allSatisFactionRatings.getReviewsList().get(review_counter).getDate());

                    //for the agent name, need to split the full name into 2 parts and keep only the 1st letter of the last name.
                    //we can get the agent's picture through their fname_l name. If none is found, the image disappears
                    String[] modAgentNameSplit = agentList.get(String.valueOf(allSatisFactionRatings.getReviewsList().get(review_counter).getAgentId())).split(" ");
                    String modAgentName = modAgentNameSplit[0] + " "+ (modAgentNameSplit[1]).substring(0,1)+".";
                    String modAgentPicName = (modAgentNameSplit[0] + "_"+(modAgentNameSplit[1]).substring(0,1)).toLowerCase();
                    ((TextView)findViewById(R.id.ticketReviewAgentTxt)).setText(modAgentName);
                    int resourceIdAgent = getResources().getIdentifier(modAgentPicName, "drawable", getPackageName());
                    if(resourceIdAgent != 0)
                        ((ImageView)findViewById(R.id.ticketReviewAgenPhototTxt)).setBackgroundResource(resourceIdAgent);
                    else
                        ((ImageView)findViewById(R.id.ticketReviewAgenPhototTxt)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }});

        }
    }

    public static Retrofit getRestAdapter(Context ctx)
    {
        if(adapter == null) {
            OkHttpClient client = new OkHttpClient();

            adapter = new Retrofit.Builder()
                    .baseUrl(ctx.getResources().getString(R.string.server_env))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return adapter;
    }

}
