package com.example.mynotebook.db;

import com.example.mynotebook.adapter.ListItem;
import java.util.List;


public interface OnDataReceived {
    void onReceived(List<ListItem> list);
}
