package com.github.scarecrow.signscognizing.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.scarecrow.signscognizing.R;

import java.util.ArrayList;
import java.util.List;

public class PopupItemRecyclerViewAdapter extends RecyclerView.Adapter <PopupItemRecyclerViewAdapter.StringListItemViewHolder>{

    List<String> items;

    public PopupItemRecyclerViewAdapter(){
        items = new ArrayList<>();
    }

    public void setItemList(List<String> itemList){
        items = itemList;
    }

    @NonNull
    @Override
    public StringListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popup_item, parent, false);
        return new StringListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringListItemViewHolder holder, int position) {
        String nowContent = items.get(position);
        holder.content.setText(nowContent);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StringListItemViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout item_body;
        public TextView content;

        public StringListItemViewHolder(View item_view){
            super(item_view);
            item_body = (LinearLayout) item_view;
            content = item_view.findViewById(R.id.popup_item_content);
        }
    }
}
