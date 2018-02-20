package com.github.scarecrow.signscognizing.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.Armband;
import com.github.scarecrow.signscognizing.fragments.ArmbandSelectFragment;

import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by Scarecrow on 2018/2/7.
 *
 */

public class ArmbandListRecyclerViewAdapter extends RecyclerView.Adapter<ArmbandListRecyclerViewAdapter.ArmbandListItemViewHolder> {

    private List<Armband> armband_list;

    private ArmbandSelectFragment.ListItemClickListenner listItemClickListenner;

    static class ArmbandListItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_body;
        public TextView info_display;

        public ArmbandListItemViewHolder(View item_view) {
            super(item_view);
            item_body = (LinearLayout) item_view;
            info_display = (TextView) item_view.findViewById(R.id.armband_list_item_info_textview);
        }
    }

    public ArmbandListRecyclerViewAdapter(List<Armband> armband_list) {
        this.armband_list = armband_list;
    }

    public void setOnListItemClickListenner(
            ArmbandSelectFragment.ListItemClickListenner listenner) {
        listItemClickListenner = listenner;
    }

    @Override
    public ArmbandListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.armband_list_item, parent, false);
        return new ArmbandListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArmbandListItemViewHolder holder, int position) {
        final Armband armband = armband_list.get(position);
        holder.info_display.setText(armband.toString());
        holder.info_display.setBackgroundColor(Color.WHITE);
        if (armband.getArmband_status() == Armband.ARMBAND_OCCURPIED) {
            holder.info_display.setBackgroundColor(Color.LTGRAY);
            return;
        }
        holder.item_body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: armband item" + armband);
                listItemClickListenner.onListItemClick(armband);
            }
        });
    }

    @Override
    public int getItemCount() {
        return armband_list.size();
    }


}
