package soleilcode.onepicaday;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.soleilcode.onepicaday.Projects.Project;

public class ProjectActivity extends ActionBarActivity {

    private static final String TAG = "ProjectActivity";
    public static final String EXTRA_PROJECT = "soleilcode.onepicaday.PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Project project = null;
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra(EXTRA_PROJECT);
        if (bytes != null) {
            try {
                project = Project.parseFrom(bytes);
            } catch (InvalidProtocolBufferNanoException e) {
                Log.e(TAG, "Unable to parse Project proto", e);
            }
        }
        if (project != null) {
            TextView projectName = (TextView) findViewById(R.id.project_name);
            projectName.setText(project.name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
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
}
