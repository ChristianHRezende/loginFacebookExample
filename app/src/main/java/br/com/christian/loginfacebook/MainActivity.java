package br.com.christian.loginfacebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe activity com um exemplo de como solicitar login de um usuário ao Facebook e conseguir
 * algumas informações de acordo com a permissão public_profile do GraphAPI.
 * @author Christian Henrique Rezende
 */
public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginManager.getInstance().logOut();

        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");

        //Inicializa o callbackManager - Responsável por administrar a resposta do GraphAPI
        callbackManager = CallbackManager.Factory.create();

        //Instância do botão do facebook
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(permissions);

        // Se usar fragment
        //loginButton.setFragment(this);

        // registro de callback, retorno
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //Exemplo de botão diferente do Facebook
        Button buttonLoginCustom = (Button) findViewById(R.id.buttonLoginFacebookCustom);
        buttonLoginCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Solicita login com as permissões public_profile, que é publico,
                //para demais permissões é necessário solicitar permissões ao usuário.
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });

        //Recebe e trata a resposta da chamada realizada de login.
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Se obter sucesso
                        getUser(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.i("Fail", "Ocorreu uma falha");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.i("Error", exception.getMessage());

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Este método e responsável por realizar uma chamada à API do Facebook e obter os dados
     * do usuário logado com base na permissão public_profile.
     *
     * @param loginResult resultado obtido ao realizar o login
     */
    private void getUser(LoginResult loginResult) {
        //Exemplo:
        //Realiza requisição dos dados do perfil
        //Está chamada pode ser realizada em qualquer momento da aplicação
        //após obter o token

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        //Objeto json com a resposta da chamada realizada
                        System.out.println(object.toString());
                    }
                });

        //Cria uma bundle para armazenar os parâmetros da requisição
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,middle_name,last_name,link,birthday,age_range");

        //Seta o bundle na requisição
        request.setParameters(parameters);

        //Executa a chamada
        request.executeAsync();
    }
}
