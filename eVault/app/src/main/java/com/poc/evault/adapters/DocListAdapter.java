package com.poc.evault.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.poc.evault.R;
import com.poc.evault.model.Item;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DASP2 on 4/4/2017.
 */
public class DocListAdapter extends RecyclerView.Adapter<DocListAdapter.ViewHolder> {
    private List<Item> listItem;
    private Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgItemIcon;
        public TextView txtItemName;
        public TextView txtItemUploadDate;
        public TextView txtItemSize;
        public ImageView imgMenu;

        public ViewHolder(View view) {
            super(view);
            imgItemIcon=(CircleImageView)view.findViewById(R.id.item_icon);
            txtItemName = (TextView) view.findViewById(R.id.item_name);
            txtItemUploadDate = (TextView) view.findViewById(R.id.item_upload_date);
            txtItemSize = (TextView) view.findViewById(R.id.item_size);
            imgMenu=(ImageView)view.findViewById(R.id.item_menu_icon);
        }
    }

    public DocListAdapter(List<Item> listItem, Context context) {
        this.listItem = listItem;
        this.context=context;
    }

    @Override
    public DocListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Item item=listItem.get(position);
        Resources res = context.getResources();
        String name = String.format(res.getString(R.string.item_name), item.getName());
        String date = String.format(res.getString(R.string.item_upload_date), item.getUploadDate());
        String size = String.format(res.getString(R.string.item_size), item.getSize());
        int imageId;
        if(item.getType().equalsIgnoreCase("pdf")){
            imageId=R.mipmap.pdf;
        }else if(item.getType().equalsIgnoreCase("doc")){
            imageId=R.mipmap.doc;
        }else if(item.getType().equalsIgnoreCase("xls")||item.getType().equalsIgnoreCase("xlsx")){
            imageId=R.mipmap.excel;
        }else{
            imageId=R.mipmap.ic_launcher;
        }
        Bitmap bm = BitmapFactory.decodeResource(res, imageId);
        holder.imgItemIcon.setImageBitmap(bm);
        holder.txtItemName.setText(name);
        holder.txtItemUploadDate.setText(date);
        holder.txtItemSize.setText(size);
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(holder.imgMenu);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_view:
                        // do what you need.
                        break;
                    case R.id.action_download:
                        // do what you need .
                        break;
                    case R.id.action_delete:
                        // do what you need .
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }
}

