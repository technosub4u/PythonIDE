package com.pythonide.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pythonide.R;
import com.pythonide.models.PythonScript;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryDialog extends MaterialAlertDialogBuilder {
    private List<PythonScript> history;
    private OnScriptSelectedListener listener;
    
    public interface OnScriptSelectedListener {
        void onScriptSelected(PythonScript script);
    }
    
    public HistoryDialog(@NonNull Context context, List<PythonScript> history, OnScriptSelectedListener listener) {
        super(context);
        this.history = history;
        this.listener = listener;
        setupDialog();
    }
    
    private void setupDialog() {
        setTitle("Script History");
        
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new HistoryAdapter(history, script -> {
            listener.onScriptSelected(script);
            dismiss();
        }));
        
        setView(recyclerView);
        setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
    }
    
    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<PythonScript> scripts;
        private OnScriptSelectedListener listener;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        
        public HistoryAdapter(List<PythonScript> scripts, OnScriptSelectedListener listener) {
            this.scripts = scripts;
            this.listener = listener;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(32, 16, 32, 16);
            textView.setTextColor(parent.getContext().getResources().getColor(R.color.cyberpunk_text));
            return new ViewHolder(textView);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PythonScript script = scripts.get(position);
            String preview = script.getCode().length() > 50 ? 
                           script.getCode().substring(0, 47) + "..." : 
                           script.getCode();
            
            holder.textView.setText(String.format("%s\n%s", 
                dateFormat.format(script.getTimestamp()),
                preview));
                
            holder.itemView.setOnClickListener(v -> listener.onScriptSelected(script));
        }
        
        @Override
        public int getItemCount() {
            return scripts.size();
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            
            ViewHolder(TextView textView) {
                super(textView);
                this.textView = textView;
            }
        }
    }
}
