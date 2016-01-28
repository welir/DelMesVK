package com.delmesvk;

import android.app.Activity;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Света on 27.01.2016.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mDataset;
    private ArrayList<String> mSenderDataset;
    private ArrayList<String> mImageUrlDataset;

    private MultiSelector  selector  =  new MultiSelector();

    private int focusedItem = 0;
    private static SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

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

        }


        @Override
        public void onClick(View v) {
            itemView.setSelected(true);
            itemView.setFocusable(true);
            setItemChecked(getPosition(), itemView.isSelected());




        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<String> myDataset, ArrayList<String> senderDataset, ArrayList<String> imgUrlDataset) {
        mDataset = myDataset;
        mSenderDataset = senderDataset;
        mImageUrlDataset = imgUrlDataset;
    }

//    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
//        int tryFocusItem = focusedItem + direction;
//
//        // If still within valid bounds, move the selection, notify to redraw, and scroll
//        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
//            notifyItemChanged(focusedItem);
//            focusedItem = tryFocusItem;
//            notifyItemChanged(focusedItem);
//            lm.scrollToPosition(focusedItem);
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//
//        // Handle key up and key down and attempt to move selection
//        recyclerView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
//
//                // Return false if scrolled to the bounds and allow focus to move off the list
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//                        return tryMoveSelection(lm, 1);
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//                        return tryMoveSelection(lm, -1);
//                    }
//                }
//
//                return false;
//            }
//        });
//    }
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
        holder.mTextView.setText(mDataset.get(position));
        holder.SenderTextView.setText(mSenderDataset.get(position));
        new DownloadImageTask(holder.mImageView)
                .execute(mImageUrlDataset.get(position));
         holder.itemView.setActivated(mSelectedPositions.get(position, false));
       // holder.itemView.sele
//        holder.itemView.setSelected(focusedItem == position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
            bmImage.setImageBitmap(result);
        }
    }
}
