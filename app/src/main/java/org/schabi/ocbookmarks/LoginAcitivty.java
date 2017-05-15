package org.schabi.ocbookmarks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginAcitivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.oc_bookmark_login));
    }
}
