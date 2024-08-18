package com.adamoubello.bimbouplay;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SongListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Song> songs;
    private static LayoutInflater inflater = null;

    public SongListAdapter(Activity a, List<Song> s) {
        activity = a;
        songs = s;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null){
            v = inflater.inflate(R.layout.songlistview_row, parent, false);
        }
        TextView title = v.findViewById(R.id.songsRowTextView);
        Song song = songs.get(position);
        title.setText(song.getTitle());
        return v;
    }
}
