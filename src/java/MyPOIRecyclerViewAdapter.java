package com.example.csc8099dissertationproject;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csc8099dissertationproject.POIFragment.OnListFragmentInteractionListener;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 *
 *  {@link RecyclerView.Adapter} that can display a {@link MarkerData}
 *  and makes a call to the specified {@link OnListFragmentInteractionListener}.
 *
 *  @author harrietkim
 *  @version 01 (2019-08-20)
 */

public class MyPOIRecyclerViewAdapter extends RecyclerView.Adapter<MyPOIRecyclerViewAdapter.ViewHolder> {

    private final List<MarkerData> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     *
     * @param items MarkerData items
     * @param listener FragmentListener to view the MarkerData
     */
    public MyPOIRecyclerViewAdapter(List<MarkerData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Resources res = holder.itemView.getResources();
        holder.mItem = mValues.get(position);
        holder.mCardTitle.setText(mValues.get(position).getTitle());
        holder.mImageView.setImageDrawable(new BitmapDrawable(res, holder.mItem.getImage()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        // Animation on focus change
        holder.mCardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {// has focus
                    Log.d(TAG, "onFocusChange: scale big");
                    scaleView(view, 1f, 1.2f);
                    view.setElevation(1);
                } else {
                    Log.d(TAG, "onFocusChange: scale small");
                    scaleView(view, 1.2f, 1f);
                    view.setElevation(0);
                }
            }
        });

        MainActivity.setMapPadding(holder.mCardView.getLayoutParams().height);
    }

    private void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f);// Pivot point of Y scaling

        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        v.startAnimation(anim);
    }

    List<MarkerData> getValues() {
        return mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mCardTitle;
        public final CardView mCardView;
        public final ImageView mImageView;
        public MarkerData mItem;

        /**
         * ViewHolder to hold Card_View
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCardTitle = (TextView) view.findViewById(R.id.card_title);
            mImageView = (ImageView) view.findViewById(R.id.card_image);
            mCardView = (CardView) view.findViewById(R.id.card_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCardTitle.getText() + "'";
        }
    }
}
