package org.schabi.ocbookmarks;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.schabi.ocbookmarks.REST.OCBookmarksRestConnector;
import org.schabi.ocbookmarks.REST.RequestException;

public class LoginAcitivty extends AppCompatActivity {

    // reply info
    private static final int OK = 0;
    private static final int FAIL = 1;

    LoginData loginData = new LoginData();

    EditText urlInput;
    EditText userInput;
    EditText passwordInput;
    Button connectButton;
    ProgressBar progressBar;
    TextView errorView;

    TestLoginTask testLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.oc_bookmark_login));
        urlInput = (EditText) findViewById(R.id.urlInput);
        userInput = (EditText) findViewById(R.id.userInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        connectButton = (Button) findViewById(R.id.connectButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorView = (TextView) findViewById(R.id.loginErrorView);

        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginData.url = fixUrl(urlInput.getText().toString());
                loginData.user = userInput.getText().toString();
                loginData.password = passwordInput.getText().toString();
                urlInput.setText(loginData.url);

                testLoginTask = new TestLoginTask();
                testLoginTask.execute(loginData);
                progressBar.setVisibility(View.VISIBLE);
                connectButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String fixUrl(String rawUrl) {
        if(!rawUrl.startsWith("http")) {
            return "https://" + rawUrl;
        }
        return rawUrl;
    }

    private class TestLoginTask extends AsyncTask<LoginData, Void, Integer> {
        protected Integer doInBackground(LoginData... loginDatas) {
            LoginData loginData = loginDatas[0];
            OCBookmarksRestConnector connector =
                    new OCBookmarksRestConnector(loginData.url, loginData.user, loginData.password);
            try {
                connector.getBookmarks();
                return new Integer(OK);
            } catch (RequestException re) {
                re.printStackTrace();
                return new Integer(FAIL);
            } catch (Exception e) {
                e.printStackTrace();
                return new Integer(FAIL);
            }
        }
        protected void onProgressUpdate(Void... nix) {

        }
        protected void onPostExecute(Integer result) {
            switch (result.intValue()) {
                case OK:
                    progressBar.setVisibility(View.GONE);
                    connectButton.setVisibility(View.VISIBLE);
                    break;
                case FAIL:
                    errorView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }
}
