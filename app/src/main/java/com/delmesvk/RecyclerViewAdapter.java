package com.delmesvk;

import android.app.Activity;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Света on 27.01.2016.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static MessegeItem Itemfragment = new MessegeItem();

    private static MultiSelector  selector  =  new MultiSelector();

    private int focusedItem = 0;
    public static SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    private static boolean mIsSelectable = false;

    private static void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    private static boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    private static void setSelectable(boolean selectable) {
        mIsSelectable = selectable;
    }

    private static boolean isSelectable() {
        return mIsSelectable;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        // each data item is just a string in this case



        public TextView mTextView;
        public TextView SenderTextView;
        public ImageView mImageView;
        public CheckBox cb;
        public ViewHolder(View v) {
            super(v);


            SenderTextView = (TextView) v.findViewById(R.id.tvSubTitle);
            mTextView =(TextView) v.findViewById(R.id.tvTitle);
            mImageView = (ImageView) v.findViewById(R.id.imageView2);
            //cb       = (CheckBox) v.findViewById(R.id.checkBox);
            v.setClickable(true);
            itemView.setOnClickListener(this);
            itemView.setClickable(true);
            Bitmap bmp = BitmapFactory.decodeResource(v.getContext().getResources(), R.drawable.ic_ab_app);

            for (int i = 0 ;  (i < Itemfragment.ImageUrlList.size()); i++)
            {

                Itemfragment.ImageList.add(i, bmp);
            }

            for (int i = 0 ;  (i < Itemfragment.ImageUrlList.size()); i++)
            {
                new DownloadImageTask(Itemfragment.ImageList.get(i))
                        .execute(Itemfragment.ImageUrlList.get(i));
            }

        }


        @Override
        public void onClick(View v) {

            if (itemView.isSelected()){
                mTextView.setBackgroundColor(Color.WHITE) ;
                setItemChecked(getPosition(),false);
                itemView.setSelected(false);
            }
            else
            {
                mTextView.setBackgroundColor(Color.argb(255,212,254,255));
                setItemChecked(getPosition(),true);
                itemView.setSelected(true);
            }

        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(MessegeItem mItem) {
        Itemfragment = mItem;

    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v =   LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        View view = v.findViewById(R.id.tv_recycler_item);
//        v.removeView(view);
        ViewHolder vh = new ViewHolder(v);



        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(Itemfragment.messagesList.get(position));
        holder.SenderTextView.setText(Itemfragment.senderList.get(position));

//        new DownloadImageTask(holder.mImageView)
//                .execute(Itemfragment.ImageUrlList.get(position));

        //holder.mImageView.setImageBitmap(Itemfragment.ImageList.get(position));
        ImageLoader imageLoader = ImageLoader.getInstance();
        int fallback = 0;
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();

//initialize image view


//download and display image from url
        imageLoader.displayImage(Itemfragment.ImageUrlList.get(position), holder.mImageView, options);

       // holder.itemView.sele
//        holder.itemView.setSelected(focusedItem == position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Itemfragment.messagesList.size();
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage = result;
        }
    }
}
