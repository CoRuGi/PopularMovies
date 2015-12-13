package com.cr.androidnanodegree.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to hold the movie videos
 */
public class MovieVideosAdapter extends ArrayAdapter<ArrayList> {
    public MovieVideosAdapter(Context context, int resource, List<ArrayList> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        ArrayList arrayList = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_videos, parent, false);
        }

        TextView videoName =
                (TextView) convertView.findViewById(R.id.detail_movie_list_video_name);
        videoName.setText(arrayList.get(FetchMovieVideosTask.MOVIE_NAME).toString());

        return convertView;
    }
}
