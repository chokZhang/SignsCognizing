package com.github.scarecrow.signscognizing.Utilities.auto_complete;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.github.scarecrow.signscognizing.adapters.PopupItemRecyclerViewAdapter;
import com.otaliastudios.autocomplete.RecyclerViewPresenter;


public class SimpleRecyclerViewPresenter extends RecyclerViewPresenter<String> {

    private static final String TAG = "RecyclerViewPresenter";


    SimpleRecyclerViewPresenter(Context context){
        super(context);
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {

        return new PopupItemRecyclerViewAdapter();
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {

    }
}
