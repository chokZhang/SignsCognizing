package com.github.scarecrow.signscognizing.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.Armband;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
import com.github.scarecrow.signscognizing.fragments.ArmbandSelectFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by Scarecrow on 2018/2/7.
 *
 */

public class ArmbandListRecyclerViewAdapter extends RecyclerView.Adapter<ArmbandListRecyclerViewAdapter.ArmbandListItemViewHolder> {

    private List<Armband> armband_list;

    private ArmbandSelectFragment.ListItemClickListenner listItemClickListenner;

    /**
     * 0 一个没选
     * 1 选了左手的
     * 2 选了右手的
     * 3 选了两个手的
     */
    private int select_state = 0;
    private HashMap<Armband, Integer> select_booking = new HashMap<>();

    static class ArmbandListItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_body, both_hand_check_view;
        public TextView info_display;
        public CheckBox select_box;
        public TextView select_state;

        public ArmbandListItemViewHolder(View item_view) {
            super(item_view);
            item_body = (LinearLayout) item_view;
            info_display = item_view.findViewById(R.id.armband_list_item_info_textview);
            select_box = item_view.findViewById(R.id.armband_select_checkbox);
            select_state = item_view.findViewById(R.id.textview_select_state);
            both_hand_check_view = item_view.findViewById(R.id.both_hand_check_view);
        }
    }

    public ArmbandListRecyclerViewAdapter() {
        init_state();
        this.armband_list = new ArrayList<>();
    }

    public void setArmbandList(List<Armband> armband_list) {
        this.armband_list = armband_list;
        notifyDataSetChanged();
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
    public void onBindViewHolder(final ArmbandListItemViewHolder holder, int position) {
        final Armband armband = armband_list.get(position);
        holder.info_display.setText(armband.toString());
        holder.info_display.setBackgroundColor(Color.WHITE);
        if (armband.getArmbandStatus() == Armband.ARMBAND_OCCURPIED) {
            holder.info_display.setBackgroundColor(Color.LTGRAY);
            holder.select_box.setEnabled(false);
            return;
        }
        boolean select_mode = ArmbandManager.getArmbandsManger().getArmbandPairMode();
        if (select_mode) {
//            双手模式时 使用checkbox以及count方式选择手环
//            如果手环已被占用 禁用check box
            if (armband.getArmbandStatus() == Armband.ARMBAND_OCCURPIED) {
                holder.select_box.setEnabled(false);
                holder.select_state.setText("手环已被占用");
                return;
            }
//            初始化选择状态显示
            holder.select_box.setChecked(false);
            holder.select_state.setText("");

            if (armband.getPairStatus() != Armband.NO_PAIR) {
                holder.select_box.setChecked(true);
                if (armband.getPairStatus() == Armband.PAIR_LEFT_HAND)
                    holder.select_state.setText("作为左手手环");
                else
                    holder.select_state.setText("作为右手手环");

            }
            holder.select_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    特判一下禁止多选
                    if (select_state == 3 && isChecked) {
                        buttonView.setChecked(false);
                        return;
                    }
                    armbandSelectStateChanged(armband, isChecked, holder);
                }
            });
        } else {
//            单手模式时 使用点击list里的item方式
            holder.both_hand_check_view.setVisibility(View.GONE);
            holder.item_body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: armband item" + armband);
                    listItemClickListenner.onListItemClick(armband);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return armband_list.size();
    }

    public Armband[] getSelectedArmband() {
        if (select_state != 3)
            return null;
        else {
            Armband left = null, right = null;
            for (Armband armband : select_booking.keySet()) {
                if (select_booking.get(armband) == 1)
                    left = armband;
                else
                    right = armband;
            }
            return new Armband[]{left, right};
        }
    }

    public void init_state() {
        select_state = 0;
        select_booking = new HashMap<>();
    }


    //使用自动机状态转移控制手环的选择
    private void armbandSelectStateChanged(Armband armband, boolean is_select,
                                           ArmbandListItemViewHolder holder) {
        switch (select_state) {
            case 0:
                select_state = 1;
                pairToLeftHand(armband, holder);
                break;
            case 1:
                if (is_select) {
                    select_state = 3;
                    pairToRightHand(armband, holder);
                } else {
                    select_state = 0;
                    undoPairToHand(armband, holder);
                }
                break;
            case 2:
                if (is_select) {
                    select_state = 3;
                    pairToLeftHand(armband, holder);
                } else {
                    undoPairToHand(armband, holder);
                    select_state = 0;
                }
                break;
            case 3:
                if (!is_select) {
                    int res = select_booking.get(armband);
                    undoPairToHand(armband, holder);
                    if (res == 1)
                        select_state = 2;
                    else if (res == 2)
                        select_state = 1;
                }
                break;
        }
    }

    private void pairToLeftHand(Armband armband, ArmbandListItemViewHolder holder) {
        holder.select_state.setText("作为左手手环");
        select_booking.put(armband, 1);
        armband.setPairStatus(Armband.PAIR_LEFT_HAND);
    }

    private void pairToRightHand(Armband armband, ArmbandListItemViewHolder holder) {
        armband.setPairStatus(Armband.PAIR_RIGHT_HAND);
        select_booking.put(armband, 2);
        holder.select_state.setText("作为右手手环");
    }

    private void undoPairToHand(Armband armband, ArmbandListItemViewHolder holder) {
        holder.select_state.setText("");
        select_booking.remove(armband);
        armband.setPairStatus(Armband.NO_PAIR);
    }


}
