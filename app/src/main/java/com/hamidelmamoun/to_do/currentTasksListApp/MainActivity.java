package com.hamidelmamoun.to_do.currentTasksListApp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hamidelmamoun.to_do.LoginActivity;
import com.hamidelmamoun.to_do.ProfileActivity;
import com.hamidelmamoun.to_do.R;
import com.hamidelmamoun.to_do.SplashActivity;
import com.hamidelmamoun.to_do.adapter.TasksAdapter;
import com.hamidelmamoun.to_do.data.DBHelper;
import com.hamidelmamoun.to_do.data.SPHelper;
import com.hamidelmamoun.to_do.finishedTasksApp.FinishedTasksActivity;
import com.hamidelmamoun.to_do.model.Task;
import com.hamidelmamoun.to_do.overdueTaskApp.OverdueActivity;
import com.hamidelmamoun.to_do.utils.Helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    LinearLayout noListLo;

    DBHelper mydb;
    RecyclerView todayTaskRv, tomorrowTaskRv, upcomingTaskRv;
    NestedScrollView scrollView;
    ProgressBar loader;
    TextView todayText,tomorrowText,upcomingText;
    ArrayList<Task> todayList = new ArrayList<>();
    ArrayList<Task> tomorrowList = new ArrayList<>();
    ArrayList<Task> upcomingList = new ArrayList<>();

    boolean isShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("DAAAAMN","UFT: " + SPHelper.userFirstTime(this));
        Log.v("DAAAAMN","AFT: " + SPHelper.addedFirstTask(this));

        if(SPHelper.userFirstTime(this)){
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }else{
            if(!SPHelper.addedFirstTask(this)){
                mydb = new DBHelper(this);
                addFirstTaskEver();
                SPHelper.setAddedFirstTaskTrue(this,true);
            }

        }
        setContentView(R.layout.activity_main);


        initViews();
        listeners();
  }


  void addFirstTaskEver(){
      Date c = Calendar.getInstance().getTime();
      System.out.println("Current time => " + c);

      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      String formattedDate = df.format(c);

      Log.v("DAAAAMN",formattedDate);

      mydb.insertContact("Setup an Interview with Hamid", formattedDate, "I can call him on his phone: +971 54 351 1510 or email him in: hehamid97@gmail.com",
              "1",
              SPHelper.getAccountId(getApplicationContext()), SPHelper.getAccountType(getApplicationContext()));
  }



    void initViews(){
        fab = findViewById(R.id.fab);
//        recyclerView = findViewById(R.id.tasks_rv);
        noListLo = findViewById(R.id.empty_db_lo);

        mydb = new DBHelper(MainActivity.this);
        scrollView = findViewById(R.id.scrollView);
        loader = findViewById(R.id.loader);

        todayTaskRv = findViewById(R.id.today_task_rv);
        tomorrowTaskRv = findViewById(R.id.tomorrow_task_rv);
        upcomingTaskRv = findViewById(R.id.upcoming_task_rv);

        todayText = findViewById(R.id.todayText);
        tomorrowText = findViewById(R.id.tomorrowText);
        upcomingText = findViewById(R.id.upcomingText);
    }

    void listeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTaskFragment newTaskFragment = NewTaskFragment.newInstance();
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", false);
                newTaskFragment.setArguments(args);
                newTaskFragment.show(getSupportFragmentManager(),"");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViews();
    }


    public void setViews(){

        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(SPHelper.getAccountType(getApplicationContext()) == 0){
            menu.findItem(R.id.action_profile).setVisible(false);
            menu.findItem(R.id.action_sign_in).setVisible(true);
        }else{
            menu.findItem(R.id.action_profile).setVisible(true);
            menu.findItem(R.id.action_sign_in).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_overdue) {
            startActivity(new Intent(this, OverdueActivity.class));
            return true;
        }else if (id == R.id.action_finished) {
            startActivity(new Intent(this, FinishedTasksActivity.class));
            return true;
        }else if (id == R.id.action_sign_in) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            todayList.clear();
            tomorrowList.clear();
            upcomingList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /* ===== TODAY ========*/
            Cursor today = mydb.getDataToday(getApplicationContext());
            loadDataList(today, todayList,0);
            /* ===== TODAY ========*/

            /* ===== TOMORROW ========*/
            Cursor tomorrow = mydb.getDataTomorrow(getApplicationContext());
            loadDataList(tomorrow, tomorrowList,1);
            /* ===== TOMORROW ========*/

            /* ===== UPCOMING ========*/
            Cursor upcoming = mydb.getDataUpcoming(getApplicationContext());
            loadDataList(upcoming, upcomingList,2);
            /* ===== UPCOMING ========*/
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int dataChecker = 0;


            loadListView(todayTaskRv,todayList);
            loadListView(tomorrowTaskRv,tomorrowList);
            loadListView(upcomingTaskRv,upcomingList);


            if(todayList.size()>0)
            {
                todayText.setVisibility(View.VISIBLE);
            }else{
                dataChecker++;
                todayText.setVisibility(View.GONE);
            }

            if(tomorrowList.size()>0)
            {
                tomorrowText.setVisibility(View.VISIBLE);
            }else{
                dataChecker++;
                tomorrowText.setVisibility(View.GONE);
            }

            if(upcomingList.size()>0)
            {
                upcomingText.setVisibility(View.VISIBLE);
            }else{
                dataChecker++;
                upcomingText.setVisibility(View.GONE);
            }

            if(dataChecker==3){
                noListLo.setVisibility(View.VISIBLE);
            }else{
                noListLo.setVisibility(View.GONE);
            }


            loader.setVisibility(View.GONE);
            if(upcomingList.size()==0 && tomorrowList.size()==0 && todayList.size() == 0){
                scrollView.setVisibility(View.GONE);
            }else {
                scrollView.setVisibility(View.VISIBLE);
            }



        }

    }



    public void loadDataList(Cursor cursor, ArrayList<Task> tasks, int day)
    {
        if(cursor!=null ) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

//                ArrayList<Task> tasks = new ArrayList<>();
                String date;
                if(day==0 || day==1){
                    date = Helpers.EpochToDateString(cursor.getString(2).toString(), "EEE d");
                }else{
                    date = Helpers.EpochToDateString(cursor.getString(2).toString(), "EEE d MMM");
                }

//                String date = Helpers.EpochToDateString(cursor.getString(2).toString(), "dd-MM-yyyy");
                String name = cursor.getString(1);
                String test = cursor.getString(3);
                Log.i("TASKSESD", test);
                tasks.add(new Task(cursor.getString(0), name,cursor.getString(4),date,cursor.getString(3),0, false));
//                HashMap<String, String> mapToday = new HashMap<String, String>();
//                mapToday.put(KEY_ID, cursor.getString(0).toString());
//                mapToday.put(KEY_TASK, cursor.getString(1).toString());
//                mapToday.put(KEY_DATE, Helpers.EpochToDateString(cursor.getString(2).toString(), "dd-MM-yyyy"));
//                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }


    public void loadListView(final RecyclerView recyclerView, final ArrayList<Task> dataList)
    {
        TasksAdapter adapter = new TasksAdapter(MainActivity.this, dataList,"MainActivity");
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Logger.log("Call");
                if(!isShown){
                    try {
                    CheckBox checkBox = recyclerView.getChildAt(0).findViewById(R.id.task_check_cb);
                    LinearLayout linearLayout = recyclerView.getChildAt(0).findViewById(R.id.task_lo);
                        if(!SPHelper.showcaseOver(MainActivity.this)) {
                            BubbleShowCaseBuilder b1 = Helpers.addMultipleShowCases(linearLayout,MainActivity.this,"My Task", "Press and hold to edit or delete a task",null, BubbleShowCase.ArrowPosition.TOP);
                            BubbleShowCaseBuilder b2 = Helpers.addMultipleShowCases(checkBox,MainActivity.this,"Check box", "Mark the task as completed by clicking here",null, BubbleShowCase.ArrowPosition.BOTTOM);
                            BubbleShowCaseBuilder b3 = Helpers.addMultipleShowCases(fab,MainActivity.this,"To do button", "Create a new task from here",null, BubbleShowCase.ArrowPosition.BOTTOM);

                            new BubbleShowCaseSequence()
                                    .addShowCase(b1) //First BubbleShowCase to show
                                    .addShowCase(b2) // This one will be showed when firstShowCase is dismissed
                                    .addShowCase(b3) // This one will be showed when secondShowCase is dismissed
                                    .show();

                            SPHelper.setShowcaseOver(getApplicationContext());
                        }
                    }catch (Exception e){

                    }
                isShown = true;
                }
                // unregister listener (this is important)
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


}



