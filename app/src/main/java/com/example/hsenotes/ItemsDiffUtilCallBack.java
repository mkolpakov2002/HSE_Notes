package com.example.hsenotes;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ItemsDiffUtilCallBack extends DiffUtil.Callback {
    List<Note> newList;
    List<Note> oldList;

    public ItemsDiffUtilCallBack(List<Note> newList, List<Note> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // In the real world you need to compare something unique like id
        return oldList.get(oldItemPosition).getNoteId() == newList.get(newItemPosition).getNoteId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return (newList.get(newItemPosition).getNoteName().equals(oldList.get(oldItemPosition).getNoteName()))
                && (newList.get(newItemPosition).getNoteText().equals(oldList.get(oldItemPosition).getNoteText()))
                && (newList.get(newItemPosition).getClass() == oldList.get(oldItemPosition).getClass())
                && (newList.get(newItemPosition).getIsSelectedOnScreen() == oldList.get(oldItemPosition).getIsSelectedOnScreen());
    }
}
