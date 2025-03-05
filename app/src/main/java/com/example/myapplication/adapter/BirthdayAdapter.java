package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.PersonBirthday;

import java.util.List;

public class BirthdayAdapter extends RecyclerView.Adapter<BirthdayAdapter.ViewHolder> {

    private final List<PersonBirthday> personBirthdayList;

    public BirthdayAdapter(List<PersonBirthday> personBirthdayList) {
        this.personBirthdayList = personBirthdayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_birthday, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PersonBirthday personBirthday = personBirthdayList.get(position);
        holder.nameTextView.setText(personBirthday.getName());
        if (personBirthday.getSurname() != null && !personBirthday.getSurname().isEmpty()) {
            holder.surnameTextView.setText(personBirthday.getSurname());
        } else {
            holder.surnameTextView.setText(null);
        }
        var dateDay = personBirthday.getDate().getDayOfMonth();
        var dateMonth = personBirthday.getDate().getMonth();
        holder.dateTextView.setText(String.format("%d %s", dateDay, dateMonth));
    }

    @Override
    public int getItemCount() {
        return personBirthdayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, surnameTextView, dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            surnameTextView = itemView.findViewById(R.id.surnameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
