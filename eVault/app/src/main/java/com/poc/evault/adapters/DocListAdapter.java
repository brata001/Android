package com.poc.evault.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DASP2 on 4/4/2017.
 */
public class DocListAdapter extends RecyclerView.Adapter<DocListAdapter.ViewHolder> {
    private List<Item> listItem;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private String PATH = Environment.getExternalStorageDirectory() + "/";
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

    public DocListAdapter(List<Item> listItem, Activity context) {
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
        final Item item=listItem.get(position);
        Resources res = context.getResources();
        final String name = String.format(res.getString(R.string.item_name), item.getName());
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
        holder.imgItemIcon.setImageBitmap(BitmapFactory.decodeResource(res, imageId));
        holder.txtItemName.setText(name);
        holder.txtItemUploadDate.setText(date);
        holder.txtItemSize.setText(size);
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(holder.imgMenu, item.getName());
            }
        });
        //bm.recycle();
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public void showPopup(View v, final String name) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_view:
                        showProgressDialog(R.string.downloading,context);
                        new ViewImage(context).execute(name);
                        break;
                    case R.id.action_download:
                        showProgressDialog(R.string.downloading,context);
                        // do what you need
                        new DownloadImage(context).execute(name);
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

    public class ViewImage extends AsyncTask<String, Integer, Boolean> {
        Activity context;
        public ViewImage(Activity context){
            this.context=context;
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            // This is done in a background thread
            return viewImage(arg0[0], context);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image)
        {

        }


        /**
         * Actually download the Image from the _url
         * @param fileName
         * @return
         */
        private boolean viewImage(String fileName, Activity context)
        {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            try {
                String _url = "http://49.12.33.176/TDLockerServer/uploads/"+fileName;
                url = new URL(_url);
                in = url.openStream();

                try {
                    File file = new File(PATH, fileName);
                    OutputStream output = new FileOutputStream(file);
                    try {
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = in.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    } catch (Exception e) {
                        hideProgressDialog(context);
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    in.close();
                }
                hideProgressDialog(context);
                openFile(context,fileName);
                return true;

            } catch (Exception e) {
                hideProgressDialog(context);
                Log.e("Error reading file", e.toString());
                return false;
            }

        }

    }

    public class DownloadImage extends AsyncTask<String, Integer, Boolean> {
        Activity context;
        public DownloadImage(Activity context){
            this.context=context;
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0],context);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image)
        {

        }


        /**
         * Actually download the Image from the _url
         * @param fileName
         * @return
         */
        private boolean downloadImage(String fileName, Activity context)
        {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            try {
                String _url = "http://49.12.33.176/TDLockerServer/uploads/"+fileName;
                url = new URL(_url);
                in = url.openStream();

                try {
                    File file = new File(PATH, fileName);
                    OutputStream output = new FileOutputStream(file);
                    try {
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = in.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    } catch (Exception e) {
                        hideProgressDialog(context);
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    in.close();
                }
                hideProgressDialog(context);
                return true;

            } catch (Exception e) {
                hideProgressDialog(context);
                Log.e("Error reading file", e.toString());
                return false;
            }

        }

    }

    private void showProgressDialog(final int text, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage(context.getString(text));
                    mProgressDialog.setIndeterminate(true);
                }

                mProgressDialog.show();
            }
        });
    }

    private void hideProgressDialog(Activity context) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.hide();
                }
            }
        });
    }

    private void openFile(Activity context, String fileName){
        File file = new File(PATH, fileName);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        String type = "*/*";
        intent.setDataAndType(data, type);
        context.startActivity(intent);
    }
}

