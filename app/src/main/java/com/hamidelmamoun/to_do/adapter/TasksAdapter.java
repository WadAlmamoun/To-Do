package com.hamidelmamoun.to_do.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamidelmamoun.to_do.R;
import com.hamidelmamoun.to_do.currentTasksListApp.MainActivity;
import com.hamidelmamoun.to_do.currentTasksListApp.NewTaskFragment;
import com.hamidelmamoun.to_do.data.DBHelper;
import com.hamidelmamoun.to_do.finishedTasksApp.FinishedTasksActivity;
import com.hamidelmamoun.to_do.model.Task;
import com.hamidelmamoun.to_do.overdueTaskApp.EditOverdueTaskFragment;
import com.hamidelmamoun.to_do.overdueTaskApp.OverdueActivity;

import java.util.ArrayList;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<Task> mTasks;
    private String mActivityFlag;

    public TasksAdapter(Activity a, ArrayList<Task> tasks, String activityFlag) {
        mActivity = a;
        mTasks = tasks;
        mActivityFlag = activityFlag;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView task_name, taskComment, task_date;
        private ImageView priorityIv;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            task_name = itemView.findViewById(R.id.task_tv);
            task_date = itemView.findViewById(R.id.date_tv);
            taskComment = itemView.findViewById(R.id.task_comments_tv);
            priorityIv = itemView.findViewById(R.id.flag_iv);
            checkBox = itemView.findViewById(R.id.task_check_cb);
        }
    }


    @NonNull
    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View convertView = inflater.inflate(R.layout.item_task, parent, false);
        final TasksAdapter.ViewHolder viewHolder = new TasksAdapter.ViewHolder(convertView);

        if(mTasks.size()>0 && viewHolder.getAdapterPosition() ==0) {

//            adapterShowCase(convertView);
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if (mTasks.get(position).getmFinished() == 0) {
                    //             you cannot edit a finished task just delete
                    itemSelected(mTasks.get(position), position);
                } else {
                    confirmDelete(mTasks.get(position).getmId(), position);
                }
                return true;
            }
        });


        viewHolder.task_name.setId(viewHolder.getAdapterPosition());
        viewHolder.task_date.setId(viewHolder.getAdapterPosition());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.ViewHolder holder, final int position) {
        final Task task = mTasks.get(position);



        if (task.getmFinished() == 1) {
            holder.checkBox.setVisibility(View.GONE);
        }
        try {
            holder.task_name.setText(task.getmName());
            holder.task_date.setText(task.getmDate());
            if (!task.getmComment().equals("")) {
                holder.taskComment.setText(task.getmComment());
                holder.taskComment.setVisibility(View.VISIBLE);
            }

            int priorityLevel = Integer.parseInt(task.getmPriority());
            if (priorityLevel == 0) {
                holder.priorityIv.setVisibility(View.GONE);
            } else if (priorityLevel == 1) {
                holder.priorityIv.setImageDrawable(mActivity.getDrawable(R.drawable.ic_flag_red_24dp));
                holder.priorityIv.setVisibility(View.VISIBLE);
            } else if (priorityLevel == 2) {
                holder.priorityIv.setImageDrawable(mActivity.getDrawable(R.drawable.ic_flag_yellow_24dp));
                holder.priorityIv.setVisibility(View.VISIBLE);
            } else {
                holder.priorityIv.setImageDrawable(mActivity.getDrawable(R.drawable.ic_flag_green_24dp));
                holder.priorityIv.setVisibility(View.VISIBLE);
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        DBHelper myDb = new DBHelper(mActivity);
                        myDb.setTaskFinished(task.getmId());
                        mTasks.remove(position);
                        Toast.makeText(mActivity,task.getmName() + " Completed ✔", Toast.LENGTH_LONG).show();
                        setViews();
                    }
                }
            });


        } catch (Exception e) {
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    private void itemSelected(final Task task, final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setMessage("Please Choose an action");
        alertDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FragmentManager manager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                Bundle args = new Bundle();

                args.putBoolean("isUpdate", true);
                args.putString("id", task.getmId());
                args.putString("name", task.getmName());
                args.putString("date", task.getmDate());
                args.putString("comment", task.getmComment());
                args.putString("priority", task.getmPriority());

                if(task.getmOverdueTask()){
                    EditOverdueTaskFragment fragment = EditOverdueTaskFragment.newInstance();
                    fragment.setArguments(args);
                    fragment.show(manager,"");
                }else{
                    NewTaskFragment newTaskFragment = NewTaskFragment.newInstance();
                    newTaskFragment.setArguments(args);
                    newTaskFragment.show(manager, "");
                }
            }
        });
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmDelete(task.getmId(), pos);
            }
        });
        alertDialog.show();
    }

    private void confirmDelete(final String id, final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setMessage("Confirm Task Deletion");
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper myDb = new DBHelper(mActivity);
                myDb.deleteTask(id);
                mTasks.remove(pos);
                setViews();
            }
        });
        alertDialog.show();
    }

            private void setViews(){
        if(mActivityFlag.equals("MainActivity")){
            ((MainActivity)mActivity).setViews();
        }else if(mActivityFlag.equals("FinishedTasksActivity")){
            ((FinishedTasksActivity)mActivity).setViews();
        }else if(mActivityFlag.equals("OverdueActivity")){
            ((OverdueActivity)mActivity).setViews();
        }
    }


}

