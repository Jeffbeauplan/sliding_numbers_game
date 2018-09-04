package com.techexchange.mobileapps.assignment1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridviewAdapter extends BaseAdapter {
    private ArrayList<ImageView> mTile = null;
    private int mColumnWidth, mColumnHeight;
    private Context mContext;

    public GridviewAdapter(Context context, ArrayList<ImageView> buttons, int columnWidth, int columnHeight) {
        mTile = buttons;
        mColumnWidth = columnWidth;
        mColumnHeight = columnHeight;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTile.size();
    }

    @Override
    public Object getItem(int position) {return (Object) mTile.get(position);}

    @Override
    public long getItemId(int position) {
        return position-1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView tile;

        if (convertView == null) {
            tile = mTile.get(position);
        } else {
            tile = (ImageView) convertView;
        }

        android.widget.AbsListView.LayoutParams params =
                new android.widget.AbsListView.LayoutParams(mColumnWidth, mColumnHeight);
        tile.setLayoutParams(params);

        return tile;
    }
}
