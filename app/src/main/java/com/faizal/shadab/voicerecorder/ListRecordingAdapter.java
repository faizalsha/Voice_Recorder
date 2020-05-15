package com.faizal.shadab.voicerecorder;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;


public class ListRecordingAdapter extends RecyclerView.Adapter<ListRecordingAdapter.ListRecordViewHolder> {
    private ArrayList<File> files;
    private OnRecordingClickListener onRecordingClickListener;
    public ListRecordingAdapter(ArrayList<File> files, OnRecordingClickListener onRecordingClickListener){
        this.files = files;
        this.onRecordingClickListener = onRecordingClickListener;
    }
    @NonNull
    @Override
    public ListRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recording, parent, false);
        return new ListRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListRecordViewHolder holder, final int position) {
        holder.title.setText(files.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ListRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView title;
        ListRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_recording_title);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecordingClickListener.onclick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onRecordingClickListener.onLongClick(getAdapterPosition());
            return false;
        }
    }
    public interface OnRecordingClickListener{
        void onclick(int position);
        void onLongClick(int position);
    }
}
