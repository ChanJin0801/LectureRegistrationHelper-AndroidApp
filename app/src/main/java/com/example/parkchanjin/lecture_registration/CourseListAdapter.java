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

/**
 * Created by parkchanjin on 27/03/2019.
 */

public class CourseListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;
    private Fragment parent;
    private String userID = MainActivity.userID;
    private Schedule schedule = new Schedule();
    private List<Integer> courseIDList;
    public static int totalCredit = 0;

    public CourseListAdapter(Context context, List<Course> courseList, Fragment parent) {
        this.context = context;
        this.courseList = courseList;
        this.parent = parent;
        schedule = new Schedule();
        courseIDList = new ArrayList<Integer>();
        new BackgroundTask().execute();
        totalCredit = 0;
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


        View v = View.inflate(context, R.layout.course, null);
        TextView courseGrade = (TextView) v.findViewById(R.id.courseGrade);
        TextView courseTitle = (TextView) v.findViewById(R.id.courseTitle);
        TextView courseCredit = (TextView) v.findViewById(R.id.courseCredit);
        TextView courseDivide = (TextView) v.findViewById(R.id.courseDivide);
        TextView coursePersonnel = (TextView) v.findViewById(R.id.coursePersonnel);
        TextView courseProfessor = (TextView) v.findViewById(R.id.courseProfessor);
        TextView courseTime = (TextView) v.findViewById(R.id.courseTime);

        if(courseList.get(i).getCourseGrade().equals("No Limit") || courseList.get(i).getCourseGrade().equals(" ")){
            courseGrade.setText("All grades");
        }
        else{
            courseGrade.setText(courseList.get(i).getCourseGrade());
        }
        courseTitle.setText(courseList.get(i).getCourseTitle());
        courseCredit.setText("Credit: " + courseList.get(i).getCourseCredit());
        courseDivide.setText("Class division: " + courseList.get(i).getCourseDivide());
        if(courseList.get(i).getCoursePersonnel() == 0){
            coursePersonnel.setText("No student limit.");
        }
        else{
            coursePersonnel.setText("Class limit: " + courseList.get(i).getCoursePersonnel());
        }
        if(courseList.get(i).getCourseProfessor().equals(" ")){
            courseProfessor.setText("Personal research");
        }
        else{
            courseProfessor.setText(courseList.get(i).getCourseProfessor());
        }
        courseTime.setText(courseList.get(i).getCourseTime());

        v.setTag(courseList.get(i).getCourseID());

        Button addButton = (Button) v.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view) {
                boolean validate = false;
                validate = schedule.validate(courseList.get(i).getCourseTime());
                if(!alreadyIn(courseIDList, courseList.get(i).getCourseID())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("This course is already added.")
                            .setPositiveButton("Try again", null)
                            .create();
                    dialog.show();
                }
                else if(totalCredit + courseList.get(i).getCourseCredit() > 5){
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("It cannot be more than 5 credits.")
                            .setPositiveButton("Try again", null)
                            .create();
                    dialog.show();
                }
                else if(validate == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                    AlertDialog dialog = builder.setMessage("Schedule is overlapped.")
                            .setPositiveButton("Try again", null)
                            .create();
                    dialog.show();
                }
                else{
                    Response.Listener<String> responseListener = new Response.Listener<String>(){

                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("This course is added.")
                                            .setPositiveButton("OK", null)
                                            .create();
                                    dialog.show();
                                    courseIDList.add(courseList.get(i).getCourseID());
                                    schedule.addSchedule(courseList.get(i).getCourseTime());
                                    totalCredit += courseList.get(i).getCourseCredit();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                                    AlertDialog dialog = builder.setMessage("Unfortunately failed in adding course.")
                                            .setNegativeButton("OK", null)
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
                    AddRequest addReqeust = new AddRequest(userID, courseList.get(i).getCourseID() + "", responseListener);
                    RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                    queue.add(addReqeust);

                }
            }
        });

        return v;
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute(){
            try{
                target = "http://10.0.2.2/ScheduleList.php?userID=" + URLEncoder.encode(userID, "UTF-8");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "Wn");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void...values){
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String courseProfessor, courseTime;
                int courseID;
                totalCredit = 0;
                while(count < jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    courseID = object.getInt("courseID");
                    courseProfessor = object.getString("courseProfessor");
                    courseTime = object.getString("courseTime");
                    totalCredit += object.getInt("courseCredit");
                    courseIDList.add(courseID);
                    schedule.addSchedule(courseTime);
                    count++;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }


    public boolean alreadyIn(List<Integer> courseIDList, int item){

        for(int i=0; i<courseIDList.size(); i++){
            if(courseIDList.get(i) == item){
                return false;
            }
        }
        return true;
    }

}

