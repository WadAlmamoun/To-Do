package com.hamidelmamoun.to_do.overdueTaskApp;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamidelmamoun.to_do.R;
import com.hamidelmamoun.to_do.adapter.TasksAdapter;
import com.hamidelmamoun.to_do.data.DBHelper;
import com.hamidelmamoun.to_do.model.Task;
import com.hamidelmamoun.to_do.utils.Helpers;

import java.util.ArrayList;

public class OverdueActivity extends AppCompatActivity {

    LinearLayout noListLo;

    DBHelper myDb;
    RecyclerView overdueTaskRv;
    NestedScrollView scrollView;
    ProgressBar loader;

    TextView overdueText;
    ArrayList<Task> overdueList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overdue);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
    }

    void initViews(){
        noListLo = findViewById(R.id.empty_db_lo);

        myDb = new DBHelper(OverdueActivity.this);
        scrollView = findViewById(R.id.scrollView);
        loader = findViewById(R.id.loader);
        overdueTaskRv = findViewById(R.id.overdue_task_rv);

        overdueText = findViewById(R.id.overdueText);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setViews();
        Log.i("FUCKING","RESUMED");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setViews(){
        myDb = new DBHelper(OverdueActivity.this);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            overdueList.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor today = myDb.getOverdueData(getApplicationContext());
            loadDataList(today, overdueList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadListView(overdueTaskRv, overdueList);


            if(overdueList.size()>0)
            {
                overdueText.setVisibility(View.VISIBLE);
            }else{
                overdueText.setVisibility(View.GONE);
                noListLo.setVisibility(View.VISIBLE);
            }

            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

        }

    }



    public void loadDataList(Cursor cursor, ArrayList< Task > tasks)
    {
        if(cursor!=null ) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

//                ArrayList<Task> tasks = new ArrayList<>();
                String date = Helpers.EpochToDateString(cursor.getString(2).toString(), "EEE d MMM");


//                String date = Helpers.EpochToDateString(cursor.getString(2).toString(), "dd-MM-yyyy");
                String name = cursor.getString(1);
                String test = cursor.getString(3);
                Log.i("TASKSESD", test);
                tasks.add(new Task(cursor.getString(0), name,cursor.getString(4),date,cursor.getString(3),0, true));
                cursor.moveToNext();
            }
        }
    }


    public void loadListView(RecyclerView recyclerView, final ArrayList<Task> dataList)
    {
        TasksAdapter adapter = new TasksAdapter(OverdueActivity.this, dataList,"OverdueActivity");
//        TasksAdapter adapter = new TasksAdapter(OverdueActivity.this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(OverdueActivity.this));
        recyclerView.setAdapter(adapter);

    }
}
