package johnfatso.laptimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.sql.Time;

import johnfatso.laptimer.timerdbms.SimpleTimerSequenceContainer;
import johnfatso.laptimer.timerdbms.TimerSequenceCollection;

public class EditorActivity extends AppCompatActivity {

    final String LOG_TAG = "MODIFIER";

    final static int NEW_TIMER = 0x01;
    final static int MODIFY_TIMER = 0x02;
    //final static int CONFIG_CHANGE = 0x03;

    final static String REQUEST_ID = "request_type";
    final static String BOX_ID = "box_string";

    SimpleTimerSequenceContainer container;

    Toolbar toolbar;
    MenuItem saveButton;

    EditText name_label;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent receivedIntent = getIntent();
        int request_type = receivedIntent.getIntExtra(REQUEST_ID, 0);

        Toolbar toolbar = findViewById(R.id.editor_toolbar);
        setSupportActionBar(toolbar);

        switch (request_type){
            case 0:
                throw new IllegalStateException("illegal request type specified by requesting main activity");

            case NEW_TIMER:
                // TODO: implement new timer implementation
                container = new SimpleTimerSequenceContainer("newTimer");
                container.setUniqueID(System.currentTimeMillis());
                toolbar.setTitle(R.string.title_new_timer_definition);
                break;

            case MODIFY_TIMER:

                container = TimerSequenceCollection.getContainer().getSequenceContainer(receivedIntent.getStringExtra(BOX_ID));
                toolbar.setTitle(R.string.title_modification);
        }

        name_label = (EditText) findViewById(R.id.editor_list_name);
        frameLayout = (FrameLayout) findViewById(R.id.editor_list_frame);

        Fragment fragment = TimerCustomizationListItemFragment.newInstance(container.getRootNode(), null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(frameLayout.getId(), fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.modifier_actionbar, menu);
        saveButton = menu.findItem(R.id.save_modifier);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.save_modifier) {
            /*
            if name has been given and at least a single timer has been added, the response
            is encapsulated into TimerBox and pushed into PersistenceContainer
             */
            if (true) {
                Toast.makeText(this, "save triggered", Toast.LENGTH_SHORT).show();
                String name = name_label.getText().toString();
                Intent resultIntent = new Intent(EditorActivity.this, MainActivity.class);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else
                Toast.makeText(this, "save missed", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent(EditorActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }
}
