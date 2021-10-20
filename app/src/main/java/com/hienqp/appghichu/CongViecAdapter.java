package com.hienqp.appghichu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CongViecAdapter extends BaseAdapter {
    private MainActivity mainActivity;
    private int dongCongViec;
    private List<CongViec> congViecList;

    // Constructor
    public CongViecAdapter(MainActivity mainActivity, int dongCongViec, List<CongViec> congViecList) {
        this.mainActivity = mainActivity;
        this.dongCongViec = dongCongViec;
        this.congViecList = congViecList;
    }

    @Override
    public int getCount() {
        return congViecList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView textViewTen;
        ImageView imageViewDelete, imageViewEdit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(dongCongViec, null);
            holder.textViewTen = (TextView) convertView.findViewById(R.id.textView_ten);
            holder.imageViewDelete = (ImageView) convertView.findViewById(R.id.imageView_delete);
            holder.imageViewEdit = (ImageView) convertView.findViewById(R.id.imageView_edit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CongViec congViec = congViecList.get(position);

        holder.textViewTen.setText(congViec.getmTenCV());

        // bắt sự kiện 2 ImageView của ViewHolder
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.DialogUpdateCongViec(congViec.getmIdCV(), congViec.getmTenCV());
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.DialogDeleteCongViec(congViec.getmIdCV(), congViec.getmTenCV());
            }
        });

        return convertView;
    }

}
