package soleilcode.onepicaday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<String> mProjectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Button Click listeners.
        // Button for creating a new project.
        Button newProjectButton = (Button) findViewById(R.id.new_project);
        newProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateProjectActivity.class));
            }
        });

        populateProjectList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateProjectList() {

        List<File> projectFiles = FileUtils.getInstance().getProjectFiles();
        List<String> projectFileNames = new ArrayList<String>();
        for (File file : projectFiles) {
            projectFileNames.add(file.getName());
        }
        mProjectsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                        projectFileNames);

        ListView listView = (ListView) findViewById(R.id.project_list);
        listView.setAdapter(mProjectsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + mProjectsAdapter.getItem(position), Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }
}
