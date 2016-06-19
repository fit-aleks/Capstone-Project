package com.fitaleks.walkwithme.ui.friends;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.R;

/**
 * Created by alexanderkulikovskiy on 20.06.16.
 */
public class FriendsDetailsFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

    private static final int LOADER_FRIEND_DETAILS = 21;

    static final String DETAIL_URI = "URI";
    static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    private Uri uri;
    private TextView name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            uri = arguments.getParcelable(DETAIL_URI);
        }
        final View rootView = inflater.inflate(R.layout.fragment_friend_details, container, false);
        final Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        if (toolbar != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = (TextView) rootView.findViewById(R.id.friend_name);
        name.setText(uri.toString());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(LOADER_FRIEND_DETAILS, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getLoaderManager().restartLoader(LOADER_FRIEND_DETAILS, null, this);
    }
    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    */
}
