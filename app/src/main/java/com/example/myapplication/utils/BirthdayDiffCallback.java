package com.example.myapplication.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.entity.PersonBirthday;

import java.util.List;

/**
 * Утилита для сравнения списков
 */
public class BirthdayDiffCallback extends DiffUtil.Callback {

    private final List<PersonBirthday> oldList;
    private final List<PersonBirthday> newList;

    public BirthdayDiffCallback(List<PersonBirthday> oldList, List<PersonBirthday> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    /**
     * // Считаем, что элемент тот же, если совпадает ID или уникальное поле
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId()
                .equals(newList.get(newItemPosition).getId());
    }

    /**
     * Считаем, что элементы равны, если совпадают все поля
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the oldItem
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        PersonBirthday oldItem = oldList.get(oldItemPosition);
        PersonBirthday newItem = newList.get(newItemPosition);
        return oldItem.getName().equals(newItem.getName())
                && oldItem.getDate().equals(newItem.getDate());
    }
}

