package com.example.parkchanjin.lecture_registration;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class StatisticsCourseListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;
    private Fragment parent;
    private String userID = MainActivity.userID;

    public StatisticsCourseListAdapter(Context context, List<Course> courseList, Fragment parent) {
        this.context = context;
        this.courseList = courseList;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        View v = View.inflate(context, R.layout.statistics, null);
        TextView courseGrade = (TextView) v.findViewById(R.id.courseGrade);
        TextView courseTitle = (TextView) v.findViewById(R.id.courseTitle);
        TextView courseDivide = (TextView) v.findViewById(R.id.courseDivide);
        TextView coursePersonnel = (TextView) v.findViewById(R.id.coursePersonnel);
        TextView courseRate = (TextView) v.findViewById(R.id.courseRate);


        if(courseList.get(i).getCourseGrade().equals("No Limit") || courseList.get(i).getCourseGrade().equals(" ")){
            courseGrade.setText("All grades");
        }
        else{
            courseGrade.setText(courseList.get(i).getCourseGrade());
        }
        courseTitle.setText(courseList.get(i).getCourseTitle());
        courseDivide.setText("Division: " + courseList.get(i).getCourseDivide());
        if(courseList.get(i).getCoursePersonnel() == 0){
            coursePersonnel.setText("No student limit.");
            courseRate.setText("");
        }
        else{
            coursePersonnel.setText("Applied: " + courseList.get(i).getCourseRival() + "/" + courseList.get(i).getCoursePersonnel());
            int rate = ((int) (((double) courseList.get(i).getCourseRival() * 100 / courseList.get(i).getCoursePersonnel()) + 0.5));
            courseRate.setText("Competition rate: " + rate + "%");
            if(rate < 20){
                courseRate.setTextColor(parent.getResources().getColor(R.color.colorSafe));
            }
            else if(rate <= 50){
                courseRate.setTextColor(parent.getResources().getColor(R.color.colorBlue));
            }
            else if(rate <= 100){
                courseRate.setTextColor(parent.getResources().getColor(R.color.colorDanger));
            }
            else if(rate <= 150){
                courseRate.setTextColor(parent.getResources().getColor(R.color.colorWarning));
            }
            else {
                courseRate.setTextColor(parent.getResources().getColor(R.color.colorRed));
            }
        }
        v.setTag(courseList.get(i).getCourseID());


        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                    Response.Listener<String> responseListener = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("This course is deleted.")
                                            .setPositiveButton("OK", null)
                                            .create();
                                    dialog.show();
                                    StatisticsFragment.totalCredit -= courseList.get(i).getCourseCredit();
                                    StatisticsFragment.credit.setText(StatisticsFragment.totalCredit + "credits");
                                    courseList.remove(i);
                                    notifyDataSetChanged();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("Unfortunately failed in deleting.")
                                            .setNegativeButton("Try again", null)
                                            .create();
                                    dialog.show();
                                }
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    };//Response.Listener 완료

                    //Volley 라이브러리를 이용해서 실제 서버와 통신을 구현하는 부분
                    DeleteRequest deleteReqeust = new DeleteRequest(userID, courseList.get(i).getCourseID() + "", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                    queue.add(deleteReqeust);


            }
        });

        return v;
    }
}

