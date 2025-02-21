package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.ViewHolder> {

    private final List<Birthday> birthdayList;

    public BirthdayAdapter(List<Birthday> birthdayList) {
        this.birthdayList = birthdayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_birthday, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Birthday birthday = birthdayList.get(position);
        holder.nameTextView.setText(birthday.getName());
        holder.dateTextView.setText(birthday.getDate().toString());
    }

    @Override
    public int getItemCount() { return birthdayList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
