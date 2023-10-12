package com.example.cscan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cscan.R;
import com.example.cscan.activity.DataActivity;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Documents;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    public Activity activity;

    public List<Datas> List;
    private String current_type;

    public DataAdapter(Activity activity2, List<Datas> List2, String current_type ) {
        this.activity = activity2;
        this.List = List2;
        this.current_type = current_type;
    }

    public void filterList(List<Datas> List2) {
        this.List = List2;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.group_item_list, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (current_type.equals("Pdf")){
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.pdf);
        }
        else {
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.doc);
        }

        viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
        viewHolder.iv_group_first_img.setVisibility(View.GONE);
        viewHolder.tv_group_name.setText(List.get(i).getDataName());
        System.out.println(List.get(i).getDataName());
        viewHolder.tv_group_date.setText(List.get(i).getDate());
        viewHolder.rl_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DataActivity) activity).clickOnListItem(((Datas) List.get(i)));
            }
        });
        viewHolder.iv_group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DataActivity) activity).clickOnListMore((Datas) List.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_group_first_img;
        ImageView iv_group_more;
        ImageView iv_group_folder_img;
        RelativeLayout rl_group;
        TextView tv_group_date;
        TextView tv_group_name;

        public ViewHolder(View view) {
            super(view);
            rl_group = (RelativeLayout) view.findViewById(R.id.rl_group);
            iv_group_first_img = (ImageView) view.findViewById(R.id.iv_group_first_img);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_group_date = (TextView) view.findViewById(R.id.tv_group_date);
            iv_group_more = (ImageView) view.findViewById(R.id.iv_group_more);
            iv_group_folder_img = (ImageView) view.findViewById(R.id.iv_group_folder_img);
        }
    }
}
