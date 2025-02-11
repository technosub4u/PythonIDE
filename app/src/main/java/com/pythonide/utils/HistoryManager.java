package com.pythonide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pythonide.models.PythonScript;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String PREF_NAME = "PythonScriptHistory";
    private static final String KEY_HISTORY = "script_history";
    private static final int MAX_HISTORY_SIZE = 10;
    
    private SharedPreferences prefs;
    private Gson gson;
    
    public HistoryManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public void addScript(PythonScript script) {
        List<PythonScript> history = getHistory();
        history.add(0, script); // Add to beginning
        
        // Keep only the last 10 items
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        // Save updated history
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }
    
    public List<PythonScript> getHistory() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<PythonScript>>(){}.getType();
        List<PythonScript> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }
    
    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}
