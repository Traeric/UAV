package com.eric.uav.send_at;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.uav.R;

import java.util.List;

public class CommandDisplayAdapter extends RecyclerView.Adapter<CommandDisplayAdapter.CommandViewHolder> {
    private Context context;
    private List<String> list;

    public CommandDisplayAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommandDisplayAdapter.CommandViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CommandViewHolder(LayoutInflater.from(this.context).inflate(R.layout.command_display_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandDisplayAdapter.CommandViewHolder viewHolder, int i) {
        viewHolder.textView.setText(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommandViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public CommandViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cmd_text);
        }
    }
}
