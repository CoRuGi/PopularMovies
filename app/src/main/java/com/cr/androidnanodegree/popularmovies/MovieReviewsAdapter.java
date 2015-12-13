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
public class MovieReviewsAdapter extends ArrayAdapter<ArrayList> {
    public MovieReviewsAdapter(Context context, int resource, List<ArrayList> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        ArrayList arrayList = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_reviews, parent, false);
        }

        TextView reviewAuthor =
                (TextView) convertView.findViewById(R.id.detail_movie_list_review_author);
        reviewAuthor.setText(arrayList.get(FetchMovieReviewsTask.REVIEW_AUTHOR).toString());

        TextView reviewContent =
                (TextView) convertView.findViewById(R.id.detail_movie_list_review_content);
        reviewContent.setText(arrayList.get(FetchMovieReviewsTask.REVIEW_CONTENT).toString());

        return convertView;
    }
}
