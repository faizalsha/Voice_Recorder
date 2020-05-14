package com.faizal.shadab.voicerecorder;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ListRecordingAdapter extends RecyclerView.Adapter<ListRecordingAdapter.ListRecordViewHolder> {
    private File[] files;
    private OnRecordingClickListener onRecordingClickListener;
    public ListRecordingAdapter(File[] files, OnRecordingClickListener onRecordingClickListener){
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
        holder.title.setText(files[position].getName());

    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class ListRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        ListRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_recording_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRecordingClickListener.onclick(getAdapterPosition());
        }

    }
    public interface OnRecordingClickListener{
        void onclick(int position);
    }
}
