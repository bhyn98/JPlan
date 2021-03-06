package com.example.jplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TodayFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TodayAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Today> mTodayData;
    FloatingActionButton fabAdd;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    Date toTimeStamp = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String timestamp_date = dateFormat.format(toTimeStamp) ;
    String str_title, str_start, str_finish, str_memo, str_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);
        mAdapter = new TodayAdapter(mTodayData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        fabAdd = view.findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TodayAddActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    private void initDataset() {
        // array 24???
        // ????????? 00 ~ 23
        // ?????? 24??? ?????????
        // db???????????? ??? start_time ???????????? ????????? array??? ????????? ??????
        // ?????? ????????? ???????????? ??????
        mTodayData = new ArrayList<>();

        for(int i=0; i<24; i++){
            mTodayData.add(i, new Today(i, "", "", "", ""));
        }
        db.collection("User").document(auth.getCurrentUser()
                .getUid()).collection("Today").document(timestamp_date).collection("PlanByTime")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot item : queryDocumentSnapshots){
                    str_title = item.get(Constants.TITLE_TODAY).toString();
                    str_start = item.get(Constants.START_TODAY).toString();
                    str_finish = item.get(Constants.FINISH_TODAY).toString();
                    str_memo = item.get(Constants.MEMO_TODAY).toString();

                    // db??? start_today ??? ???????????? array ??? ?????? ????????? ???????????? ??????? ?????? ?????? string ??? ??? ?????? ???
                    // ?????? ????????? item??? ?????? -> ?????? ?????? "" ?????? ???????
                    // arraylist??? ?????? ?????? ???????

                    int idx = str_start.indexOf(":");
                    // : ???????????? ??????
                    // substring??? ????????? ????????? ???????????? ???????????? ?????????.
                    String result = str_start.substring(0, idx);
                    System.out.println("substring result" + result);
                    int timeIdx = Integer.parseInt(result);

                    mTodayData.get(timeIdx).setAll(timeIdx, str_title, str_start, str_finish, str_memo);
                }
                System.out.println("test fragment " + mTodayData.toString());

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).tb_title.setText("Today");
    }
}
