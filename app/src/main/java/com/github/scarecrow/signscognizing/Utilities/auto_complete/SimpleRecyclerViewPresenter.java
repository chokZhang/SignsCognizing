package com.github.scarecrow.signscognizing.Utilities.auto_complete;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.scarecrow.signscognizing.adapters.PopupItemRecyclerViewAdapter;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;

import java.util.ArrayList;
import java.util.List;


public class SimpleRecyclerViewPresenter extends RecyclerViewPresenter<String> {

    private static final String TAG = "RecyclerViewPresenter";
    PopupItemRecyclerViewAdapter instance ;

    public SimpleRecyclerViewPresenter(Context context){
        super(context);
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        instance = new PopupItemRecyclerViewAdapter();
        instance.setItemClickListener(new PopupItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemticClick(View view, String content) {
                dispatchClick(content);
            }
        });

        return instance;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        String queryContent = query.toString();
        String[] strs = queryContent.split(" ");
        //查询词的列表
        List<String> queryList = new ArrayList<>();
        for(String str : strs){
            queryList.add(str);
        }
        List<String> items = SentenceAutoCompleter.getInstance().executeValueQuery(queryList, false);
        instance.setItemList(items);
    }
}
