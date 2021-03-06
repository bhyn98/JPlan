package com.example.jplan;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodayAddActivity extends AppCompatActivity {

    EditText memo_today_edt, title_today_edt;
    TimePicker time_finish, time_start;
    Button today_add_btn;
    String str_title, str_memo, str_startT, str_finishT;

    Date toTimeStamp = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String timestamp_date = dateFormat.format(toTimeStamp) ;
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
    String timestamp_time = timeFormat.format(toTimeStamp) ;

    //firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_add);
        memo_today_edt = findViewById(R.id.memo_today_edt);
        title_today_edt = findViewById(R.id.title_today_edt);
        time_finish = findViewById(R.id.time_finish);
        time_start = findViewById(R.id.time_start);
        today_add_btn = findViewById(R.id.today_add_btn);
        time_start.setIs24HourView(true);
        time_finish.setIs24HourView(true);
        time_start.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                System.out.println("addToday start Time " + hourOfDay + ":" + minute);
                str_startT = hourOfDay + ":" + minute;
                System.out.println("addToday start Time " + str_startT);

            }
        });

        time_finish.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                str_finishT = hourOfDay + ":" + minute;
            }
        });

        today_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_title = title_today_edt.getText().toString();
                str_memo = memo_today_edt.getText().toString();
                if (str_title.equals("")) {
                    Toast.makeText(TodayAddActivity.this, "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    // ????????? ??????????????? ?????? ????????? ????????? ??? ????????? ?????? ??? ?????? or ??????
                    System.out.println("value of Today title " + str_title);
                    System.out.println("value of Today start " + str_startT);
                    System.out.println("value of Today finish " + str_finishT);
                    System.out.println("value of Today memo " + str_memo);
                    Today today = new Today();
                    today.setTitle_Today(str_title);
                    today.setStart_Today(str_startT);
                    today.setFinish_Today(str_finishT);
                    today.setMemo_Today(str_memo);

                    firebaseFirestore.collection("User").document(auth.getCurrentUser()
                            .getUid()).collection("Today").document(timestamp_date).collection("PlanByTime").add(today).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(TodayAddActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(TodayAddActivity.this, "????????? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}