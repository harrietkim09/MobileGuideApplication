package com.example.csc8099dissertationproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 *
 *  @author harrietkim
 *  @version 01 (2019-08-20)
 *
 */
public class POIFragment extends Fragment {

    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_POI_LIST = "poi-list";
    public View mView = null;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<MarkerData> mPOIData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public POIFragment() {
    }

    @SuppressWarnings("unused")
    public static POIFragment newInstance(int columnCount, ArrayList<MarkerData> poiData) {
        POIFragment fragment = new POIFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList(ARG_POI_LIST, poiData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_poi_list, container, false);

        return mView;
    }

    @SuppressLint("WrongConstant")
    public boolean initRecyclerView(Bundle poiData) {
        // Set the adapter
        if (mView instanceof RecyclerView) {
            Context context = mView.getContext();
            RecyclerView recyclerView = (RecyclerView) mView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            if (poiData != null) {
                mColumnCount = poiData.getInt(ARG_COLUMN_COUNT);
                mPOIData = poiData.getParcelableArrayList(ARG_POI_LIST);
            }

            recyclerView.setAdapter(new MyPOIRecyclerViewAdapter(mPOIData, mListener));
        }

        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    List<MarkerData> getPOIData() {
        return ((MyPOIRecyclerViewAdapter) ((RecyclerView) mView).getAdapter()).getValues();
    }

    void notifyPOIListUpdate() {
        ((RecyclerView) mView).getAdapter().notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(MarkerData item);
    }
}
