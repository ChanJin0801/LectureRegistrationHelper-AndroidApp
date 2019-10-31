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

public class RankListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;
    private Fragment parent;

    public RankListAdapter(Context context, List<Course> courseList, Fragment parent) {
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


        View v = View.inflate(context, R.layout.rank, null);
        TextView rankTextView = (TextView) v.findViewById(R.id.rankTextView);
        TextView courseGrade = (TextView) v.findViewById(R.id.courseGrade);
        TextView courseTitle = (TextView) v.findViewById(R.id.courseTitle);
        TextView courseCredit = (TextView) v.findViewById(R.id.courseCredit);
        TextView courseDivide = (TextView) v.findViewById(R.id.courseDivide);
        TextView coursePersonnel = (TextView) v.findViewById(R.id.coursePersonnel);
        TextView courseProfessor = (TextView) v.findViewById(R.id.courseProfessor);
        TextView courseTime = (TextView)v.findViewById(R.id.courseTime);

        rankTextView.setText((i + 1) + " prize");
        if(i != 0){
            rankTextView.setBackgroundColor(parent.getResources().getColor(R.color.colorPrimary));
        }
        if(courseList.get(i).getCourseGrade().equals("No Limit") || courseList.get(i).getCourseGrade().equals("")){
            courseGrade.setText("All grades");
        }
        else{
            courseGrade.setText(courseList.get(i).getCourseGrade());
        }
        courseTitle.setText(courseList.get(i).getCourseTitle());
        courseCredit.setText(courseList.get(i).getCourseCredit() + " credits");
        courseDivide.setText("Division: " + courseList.get(i).getCourseDivide());
        if(courseList.get(i).getCoursePersonnel() == 0){
            coursePersonnel.setText("No student limit.");
        }
        else{
            coursePersonnel.setText("Personnel Limit: " + courseList.get(i).getCoursePersonnel());
        }
        if(courseList.get(i).getCourseProfessor().equals("")){
            courseProfessor.setText("Personal Research");
        }
        else{
            courseProfessor.setText(courseList.get(i).getCourseProfessor() + " professor");
        }
        courseTime.setText(courseList.get(i).getCourseTime());

        v.setTag(courseList.get(i).getCourseID());

        return v;
    }
}

