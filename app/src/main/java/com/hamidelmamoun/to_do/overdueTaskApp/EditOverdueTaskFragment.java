package com.hamidelmamoun.to_do.overdueTaskApp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.hamidelmamoun.to_do.R;
import com.hamidelmamoun.to_do.adapter.PriorityAdapter;
import com.hamidelmamoun.to_do.data.DBHelper;
import com.hamidelmamoun.to_do.data.SPHelper;
import com.hamidelmamoun.to_do.model.Priority;
import com.hamidelmamoun.to_do.utils.NotificationReceiver;
import com.hamidelmamoun.to_do.utils.Helpers;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class EditOverdueTaskFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener  {

    public static EditOverdueTaskFragment newInstance() {
        return new EditOverdueTaskFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        dpd = (DatePickerDialog) getActivity().getFragmentManager().findFragmentByTag("startDatepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    private View view;
    private EditText taskEt,commentEt;
    private ImageView dateIv, commentIv ,addTaskIv;
    private Spinner prioritySp;
    private ArrayList<Priority> priorities = new ArrayList<>();
    private Drawable[] priorityFlagsArray = new Drawable[3];
    private String[] priorityNamesArray = new String[3];

    private DBHelper mydb;
    private DatePickerDialog dpd;
    private int startYear = 0, startMonth = 0, startDay = 0;

    private String dateFinal="";
    private String nameFinal="";
    private String commentFinal="";
    private String priorityFinal="";

    private boolean commentEtVisible = false;

    Intent intent;
    private Boolean isUpdate;
    private String id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {




        view = inflater.inflate(R.layout.fragment_new_task_dialog, container, false);

        isUpdate = Objects.requireNonNull(getArguments()).getBoolean("isUpdate");

        setViews();
        initViews();
        listeners();

        if(isUpdate){
            initUpdate();
        }

        return view;
    }


    private void initViews(){
        taskEt = view.findViewById(R.id.task_et);
        commentEt = view.findViewById(R.id.comment_et);
        dateIv = view.findViewById(R.id.date_iv);
        commentIv = view.findViewById(R.id.comment_iv);
        addTaskIv = view.findViewById(R.id.add_task_iv);
        mydb = new DBHelper(getContext());

        //Priority:
        prioritySp = view.findViewById(R.id.priority_sp);
        for (int i=0;i<4;i++){
            priorities.add(new Priority(String.valueOf(i), priorityNamesArray[i],priorityFlagsArray[i]));
        }
        PriorityAdapter adapter = new PriorityAdapter(getActivity(), priorities);
        prioritySp.setAdapter(adapter);
    }

    public void initUpdate() {
        id = Objects.requireNonNull(getArguments()).getString("id");
        nameFinal = Objects.requireNonNull(getArguments()).getString("name");
        dateFinal = Objects.requireNonNull(getArguments()).getString("date");
        commentFinal = Objects.requireNonNull(getArguments()).getString("comment");
        priorityFinal = Objects.requireNonNull(getArguments()).getString("priority");

        taskEt.setText(nameFinal);
        commentEt.setText(commentFinal);
        prioritySp.setSelection(Integer.parseInt(priorityFinal));

        dateIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_range_primary_24dp));
        if(!commentFinal.equals("")){
            commentIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_primary_24dp));
            commentEtVisible = true;
            commentEt.setVisibility(View.VISIBLE);
        }

        int priority = Integer.parseInt(priorityFinal);
        if(priority == 1){
            prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_red_24dp));
        }else if(priority == 2){
            prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_yellow_24dp));
        }else if (priority ==3){
            prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_green_24dp));
        }

    }


    private void setViews(){
        priorityFlagsArray = new Drawable[]{null,getActivity().getDrawable(R.drawable.ic_flag_red_24dp), getActivity().getDrawable(R.drawable.ic_flag_yellow_24dp), getActivity().getDrawable(R.drawable.ic_flag_green_24dp)};
        priorityNamesArray = new String[]{"","Priority 1", "Priority 2", "Priority 3"};
    }

    private void listeners(){
        taskEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    addTaskIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_primary_24dp));
                    addTaskIv.setEnabled(true);
                }else{
                    addTaskIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_grey_24dp));
                    addTaskIv.setEnabled(false);
                }

                nameFinal = s.toString();
            }
        });

        dateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartDatePicker();
            }
        });

        commentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentEtVisible){
                    commentIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_grey_24dp));
                    commentEtVisible = false;
                    commentEt.setVisibility(View.GONE);
                }else{
                    commentIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_primary_24dp));
                    commentEtVisible = true;
                    commentEt.setVisibility(View.VISIBLE);
                }
            }
        });

        prioritySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_red_24dp));
                    priorityFinal = "1";
                }else if(position == 2){
                    prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_yellow_24dp));
                    priorityFinal = "2";
                }else if(position == 3){
                    priorityFinal = "3";
                    prioritySp.setBackgroundDrawable(getActivity().getDrawable(R.drawable.ic_flag_green_24dp));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        addTaskIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

    }

    void addTask(){
        commentFinal = commentEt.getText().toString();

        if ( !dateFinal.equals("") && !nameFinal.equals("")) {
            if (isUpdate){
                mydb.updateContact(id, nameFinal, dateFinal, commentFinal, priorityFinal, SPHelper.getAccountId(getContext()), SPHelper.getAccountType(getContext()));
//                Toast.makeText(getContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                if(priorityFinal.equals("")){
                    priorityFinal = "0";
                }
                mydb.insertContact(nameFinal, dateFinal, commentFinal, priorityFinal, SPHelper.getAccountId(getContext()), SPHelper.getAccountType(getContext()));
                Toast.makeText(getContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }

            NotificationReceiver.NotifyUserSoon(getActivity(),getString(R.string.app_name),nameFinal, dateFinal);

            dismiss();
            ((OverdueActivity)getActivity()).setViews();
        } else {
            Snackbar.make(getView(),"Add expected task date",Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showStartDatePicker() {
        dpd = DatePickerDialog.newInstance(this, startYear, startMonth, startDay);
        if(isUpdate){
            Cursor task = mydb.getDataSpecific(id);
            if (task != null) {
                task.moveToFirst();
                Calendar cal = Helpers.EpochToCalender(task.getString(2).toString());
                Toast.makeText(getContext(),String.valueOf(cal),Toast.LENGTH_LONG).show();
                Calendar [] cals = new Calendar[] {cal};
                dpd.setHighlightedDays(cals);
            }
        }
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setOnDateSetListener(this);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        dpd.setMinDate(calendar);

        dpd.show(Objects.requireNonNull(getActivity()).getFragmentManager(), "startDatepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        int monthAddOne = startMonth + 1;
        dateFinal = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;
        dateIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_date_range_primary_24dp));
    }
}
