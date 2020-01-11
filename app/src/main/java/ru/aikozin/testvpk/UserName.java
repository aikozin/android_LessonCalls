package ru.aikozin.testvpk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserName extends AppCompatActivity {

    int code, codeTest;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        code = getIntent().getExtras().getInt("code");
        codeTest = getIntent().getExtras().getInt("codeTest");
    }

    public void clickStartTest(View view) {
        EditText editText1 = findViewById(R.id.editText1);
        EditText editText2 = findViewById(R.id.editText2);
        EditText editText3 = findViewById(R.id.editText3);

        final String name = editText1.getText().toString() + " "
                + editText2.getText().toString() + " "
                + editText3.getText().toString();
        final String deviceType = "mobile";

        new Thread() {
            public void run() {
                String parameters = "type=%s&code=%d&codeTest=%d&deviceType=%s&name=%s";
                parameters = String.format(parameters, "getQuestionsAndStartTest", code, codeTest, deviceType, name);
                final JSONObject jsonConnection = API.getJSON(parameters);
                if (jsonConnection != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                int numberQuestion = jsonConnection.getInt("numberQuestion");
                                List<Integer> questionOrder = new ArrayList<>();
                                for (int i = 0; i < numberQuestion; i++)
                                    questionOrder.add(i);
                                Collections.shuffle(questionOrder);

                                Intent intent = new Intent(UserName.this, Question.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("jsonConnection", String.valueOf(jsonConnection));
                                intent.putExtra("code", code);
                                intent.putExtra("codeTest", codeTest);
                                intent.putIntegerArrayListExtra("questionOrder", (ArrayList<Integer>) questionOrder);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();
    }
}
