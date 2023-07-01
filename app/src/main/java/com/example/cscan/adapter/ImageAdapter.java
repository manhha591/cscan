package com.example.cscan.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cscan.R;
import com.example.cscan.activity.GroupDocumentActivity;
import com.example.cscan.models.Images;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public Activity activity;
    private List<Images> List;

    public ImageAdapter(Activity activity2, List<Images> List2) {
        this.activity = activity2;
        this.List = List2;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_image_item_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.docLayout.setVisibility(View.VISIBLE);
        Glide.with(activity).load(List.get(i).getImageData()).into(viewHolder.iv_doc);
        TextView textView = viewHolder.tv_doc_name;
        textView.setText("Page " + (i + 1));

        viewHolder.iv_doc_item_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GroupDocumentActivity) activity).onClickItemMore(List.get(i).getImageId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_doc;
        private ImageView iv_doc_item_more;
        private TextView tv_doc_name;
        private RelativeLayout docLayout;

        public ViewHolder(View view) {
            super(view);
            iv_doc = (ImageView) view.findViewById(R.id.iv_doc);
            tv_doc_name = (TextView) view.findViewById(R.id.tv_doc_name);
            iv_doc_item_more = (ImageView) view.findViewById(R.id.iv_doc_item_more);
            docLayout = (RelativeLayout) view.findViewById(R.id.docLayout);
        }
    }
}
