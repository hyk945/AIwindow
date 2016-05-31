package org.hyk.aiwindow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by hyk on 2016/5/27.
 */
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private CheckBox rememberPass;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit=(EditText)findViewById(R.id.account);
        passwordEdit=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        rememberPass=(CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember=pref.getBoolean("remember_password",false);


        if(isRemember){
            //如果用户之前密码框有打勾，将账号和密码都设置到文本框中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String account=accountEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                if(account.equals("admin") && password.equals("123456")){//满足账号密码条件才允许登陆
                    editor=pref.edit();
                    if(rememberPass.isChecked()){
                        //检查复选框是否被选中
                        editor.putString("account",account);
                        editor.putString("password",password);
                        editor.putBoolean("remember_password",true);
                    }else{
                        editor.clear();
                    }
                    editor.commit();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"account or password " +
                            "is invalid",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
