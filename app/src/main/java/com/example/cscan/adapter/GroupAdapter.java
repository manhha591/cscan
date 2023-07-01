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
import com.example.cscan.activity.MainActivity;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.service.getListImageCallBack;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public Activity activity;

    public List<GroupImage> List;
    private String current_mode;

    public GroupAdapter(Activity activity2, List<GroupImage> List2, String str) {
        this.activity = activity2;
        this.List = List2;
        this.current_mode = str;
    }

    public void filterList(List<GroupImage> List2) {
        this.List = List2;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View view;
//        if (current_mode.equals("Grid")) {
//            view = LayoutInflater.from(activity).inflate(R.layout.group_item_grid, viewGroup, false);
//        } else {
//            view = LayoutInflater.from(activity).inflate(R.layout.group_item_list, viewGroup, false);
//        }
//        return new ViewHolder(view);
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.group_item_list, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        GroupDocumentActivity groupDocumentActivity = new GroupDocumentActivity();

        groupDocumentActivity.getAllImage(List.get(i).getGroupId(), new getListImageCallBack() {
            @Override
            public void onGetListImageCallBack(List<Images> list) {
                if (list != null && !list.isEmpty()) {
                    Images firstImage = list.get(0);
                    String firstImgData = firstImage.getImageData();
                    viewHolder.iv_group_folder_img.setVisibility(View.GONE);
                    viewHolder.iv_group_first_img.setVisibility(View.VISIBLE);
                    // Sử dụng firstImgData ở đây, ví dụ:
                    // Hiển thị hình ảnh đầu tiên trong ImageView
                    Glide.with(activity)
                            .load(firstImgData)
                            .into(viewHolder.iv_group_first_img);
                } else {
                    viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_fill);
                    viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
                    viewHolder.iv_group_first_img.setVisibility(View.GONE);
                }
            }
        });

        viewHolder.tv_group_name.setText(List.get(i).getGroupName());
        viewHolder.tv_group_date.setText(List.get(i).getGroupDate());
        viewHolder.rl_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).clickOnListItem(((GroupImage) List.get(i)));
            }
        });
        viewHolder.iv_group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).clickOnListMore(List.get(i));
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
