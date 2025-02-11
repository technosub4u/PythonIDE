package com.pythonide;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.pythonide.dialogs.HistoryDialog;
import com.pythonide.models.PythonScript;
import com.pythonide.utils.HistoryManager;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptActivity extends AppCompatActivity {
    private EditText codeInput;
    private TextView outputText;
    private MaterialButton runButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private HistoryManager historyManager;
    private Python python;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);

        // Initialize views
        codeInput = findViewById(R.id.codeInput);
        outputText = findViewById(R.id.outputText);
        runButton = findViewById(R.id.runButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // Initialize Python
        python = Python.getInstance();

        // Initialize history manager
        historyManager = new HistoryManager(this);

        // Setup navigation view
        setupNavigationView();

        // Setup run button
        runButton.setOnClickListener(v -> executeScript());
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_history) {
                showHistory();
                return true;
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void showHistory() {
        new HistoryDialog(this, historyManager.getHistory(), script -> {
            codeInput.setText(script.getCode());
            outputText.setText(script.getOutput());
        }).show();
        drawerLayout.closeDrawers();
    }

    private void executeScript() {
        String code = codeInput.getText().toString();
        
        // Create a StringBuilder for output
        StringBuilder output = new StringBuilder();

        try {
            // Get Python instance and create a new scope
            Python py = Python.getInstance();
            PyObject globals = py.getBuiltins().get("dict")().call();
            
            // Redirect stdout to capture output
            PyObject sys = py.getModule("sys");
            PyObject stdout = sys.get("stdout");
            
            // Create a custom stdout that writes to our StringBuilder
            PyObject io = py.getModule("io");
            PyObject stringIO = io.get("StringIO").call();
            sys.put("stdout", stringIO);
            
            try {
                // Execute the code
                py.getModule("builtins").callAttr("exec", code, globals);
                
                // Get the captured output
                String capturedOutput = stringIO.callAttr("getvalue").toString();
                output.append(capturedOutput);
            } finally {
                // Restore original stdout
                sys.put("stdout", stdout);
            }
            
            // Get any return value or output from the last expression
            try {
                PyObject result = py.getModule("builtins").callAttr("eval", code, globals);
                if (result != null && !result.toString().equals("None")) {
                    if (output.length() > 0 && !output.toString().endsWith("\n")) {
                        output.append("\n");
                    }
                    output.append(result.toString());
                }
            } catch (Exception e) {
                // Ignore eval errors as the code might not be a single expression
            }
            
            String finalOutput = output.toString();
            outputText.setText(finalOutput);
            
            // Save to history
            historyManager.addScript(new PythonScript(code, finalOutput));
            
        } catch (Exception e) {
            String errorMsg = "Error: " + e.getMessage();
            outputText.setText(errorMsg);
            historyManager.addScript(new PythonScript(code, errorMsg));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
