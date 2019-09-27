package com.hamidelmamoun.to_do.finishedTasksApp;

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
import java.util.Objects;

public class FinishedTasksActivity extends AppCompatActivity {

    LinearLayout noListLo;

    DBHelper mydb;
    RecyclerView finishedTaskRv;
    NestedScrollView scrollView;
    ProgressBar loader;

    TextView finishedText;
    ArrayList<Task> finishedList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_tasks);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initViews();

    }


        void initViews(){
            noListLo = findViewById(R.id.empty_db_lo);

            mydb = new DBHelper(FinishedTasksActivity.this);
            scrollView = findViewById(R.id.scrollView);
            loader = findViewById(R.id.loader);
            finishedTaskRv = findViewById(R.id.finished_task_rv);

            finishedText = findViewById(R.id.finishedText);
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
            mydb = new DBHelper(FinishedTasksActivity.this);
            scrollView.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

            LoadTask loadTask = new LoadTask();
            loadTask.execute();
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
                    tasks.add(new Task(cursor.getString(0), name,cursor.getString(4),date,cursor.getString(3),1, false));
//                HashMap<String, String> mapToday = new HashMap<String, String>();
//                mapToday.put(KEY_ID, cursor.getString(0).toString());
//                mapToday.put(KEY_TASK, cursor.getString(1).toString());
//                mapToday.put(KEY_DATE, Helpers.EpochToDateString(cursor.getString(2).toString(), "dd-MM-yyyy"));
//                dataList.add(mapToday);
                    cursor.moveToNext();
                }
            }
        }

        public void loadListView(RecyclerView recyclerView, final ArrayList<Task> dataList)
        {
            TasksAdapter adapter = new TasksAdapter(FinishedTasksActivity.this, dataList,"FinishedTasksActivity");
//            TasksAdapter adapter = new TasksAdapter(FinishedTasksActivity.this, dataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(FinishedTasksActivity.this));
            recyclerView.setAdapter(adapter);

//            listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),"itemClicked",Toast.LENGTH_SHORT).show();
////                Intent i = new Intent(activity, AddTask.class);
////                i.putExtra("isUpdate", true);
////                i.putExtra("id", dataList.get(+position).get(KEY_ID));
////                startActivity(i);
//            }
//        });



        }

        class LoadTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                finishedList.clear();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor today = mydb.getDataFinished(getApplicationContext());
                loadDataList(today, finishedList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                loadListView(finishedTaskRv,finishedList);


                if(finishedList.size()>0)
                {
                    finishedText.setVisibility(View.VISIBLE);
                }else{
                    finishedText.setVisibility(View.GONE);
                    noListLo.setVisibility(View.VISIBLE);
                }

                loader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

            }

        }
    }
