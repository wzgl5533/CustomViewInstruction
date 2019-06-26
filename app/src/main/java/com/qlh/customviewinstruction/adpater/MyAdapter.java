package com.qlh.customviewinstruction.adpater;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qlh.customviewinstruction.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter {
    ArrayList<String> data = new ArrayList<String>();

    public MyAdapter() {
        initData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((MyViewHolder) holder).btn.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Button btn;
        public MyViewHolder(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btn);
        }
    }

    private void initData(){
        String[] str = new String[]{"AAAAAAA","BBBBBBBBB","CCCCCCCCCC"};
        for (int i = 0; i < 9; i++) {
            data.add(i, str[i % 3]);

        }
    }
}
