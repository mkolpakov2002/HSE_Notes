package com.example.hsenotes;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public List<String> gettingListFromString(String genreIds) {
        List<String> list = new ArrayList<>();

        String[] array = genreIds.split(";");

        for (String s : array) {
            if (!s.isEmpty()) {
                list.add((s));
            }
        }
        return list;
    }

    @TypeConverter
    public String writingStringFromList(List<String> list) {
        StringBuilder genreIds = new StringBuilder();
        for (String i : list) {
            genreIds.append(i).append(";");
        }
        genreIds.deleteCharAt(genreIds.lastIndexOf(";"));
        return genreIds.toString();
    }
}
