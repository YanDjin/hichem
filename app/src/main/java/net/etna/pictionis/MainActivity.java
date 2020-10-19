package net.etna.pictionis;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.Menu;
import net.etna.pictionis.R;
import com.firebase.ui.auth.AuthUI;
import android.view.View;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{
    int Code = 50005;
    private DatabaseReference firebaseDatabase;
    DessinSection dessinSection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            authentication();
        }
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        dessinSection = findViewById(R.id.dessinSection);
    }

    //    ---------------------------------------------------------------------------------------
    //    ---------------------------------- authentication -------------------------------------
    //    ---------------------------------------------------------------------------------------

    public void authentication()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
                .build(), Code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Code) {
            if (resultCode == RESULT_OK) {
                Toast toast = Toast.makeText(this, "Bienvenue", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(this, MainActivity.class));
                this.finish(); // stop the current activity
            }
        }
    }

    //    ---------------------------------------------------------------------------------------
    //    --------------------------------------- menu ------------------------------------------
    //    ---------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful()) {
                            authentication();
                        }
                    }
                });
        return super.onOptionsItemSelected(item);
    }


    //    ---------------------------------------------------------------------------------------
    //    --------------------------------------- draw ------------------------------------------
    //    ---------------------------------------------------------------------------------------

    public void clearCanvas(View view)
    {
        dessinSection.clear();
    }
}