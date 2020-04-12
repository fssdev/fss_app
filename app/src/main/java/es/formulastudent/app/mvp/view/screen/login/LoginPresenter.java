package es.formulastudent.app.mvp.view.screen.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import es.formulastudent.app.R;
import es.formulastudent.app.di.module.business.SharedPreferencesModule;
import es.formulastudent.app.mvp.data.business.BusinessCallback;
import es.formulastudent.app.mvp.data.business.ResponseDTO;
import es.formulastudent.app.mvp.data.business.auth.AuthBO;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.screen.welcome.MainActivity;
import es.formulastudent.app.mvp.view.utils.Messages;


public class LoginPresenter {

    //Dependencies
    private View view;
    private Context context;
    private AuthBO authBO;
    private UserBO userBO;
    private SharedPreferences sharedPreferences;
    private Messages messages;

    public LoginPresenter(LoginPresenter.View view, Context context, AuthBO authBO, UserBO userBO,
                          SharedPreferences sharedPreferences, Messages messages) {
        this.view = view;
        this.context = context;
        this.authBO = authBO;
        this.userBO = userBO;
        this.sharedPreferences = sharedPreferences;
        this.messages = messages;
    }

    void loginSuccess(User user){

        userBO.retrieveUserByMail(user.getMail(), new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                if(responseDTO.getData()!=null){

                    //If user exists in database, we store it in local storage
                    User user = (User) responseDTO.getData();

                    //User not activated, cannot do login
                    if(user.getRole() == null){
                        messages.showError(R.string.login_activity_user_not_activated);
                        view.hideLoadingIcon();

                    }else{
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SharedPreferencesModule.PREFS_CURRENT_USER, new Gson().toJson(user));
                        editor.commit();

                        //Start Timeline activity
                        Intent myIntent = new Intent(context, MainActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(myIntent);
                        view.finishView();
                    }

                }else{ //The teamMember is created for Login, but not in users table
                    view.hideLoadingIcon();
                    messages.showError(R.string.login_activity_user_not_found);
                }
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                messages.showError(responseDTO.getError());
            }
        });
    }


    /**
     * Get an email with the steps to reset your password
     * @param mail
     */
    void forgotPassword(String mail){
        if(mail != null && !mail.isEmpty()) {
            authBO.resetPassword(mail, new BusinessCallback() {
                @Override
                public void onSuccess(ResponseDTO responseDTO) {
                    messages.showInfo(responseDTO.getInfo());
                }

                @Override
                public void onFailure(ResponseDTO responseDTO) {
                    messages.showError(responseDTO.getError());
                }
            });
        } else {
            messages.showInfo(R.string.login_business_email_mandatory);
        }
    }

    /**
     * Log in with mail and password
     * @param mail
     * @param password
     * @return
     */
    void doLogin(String mail, String password) {

        //Hide keyboard
        this.hideKeyboard();

        //Call business to auth user
        authBO.doLoginWithMail(mail, password, new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                FirebaseUser firebaseUser = (FirebaseUser) responseDTO.getData();
                User user = new User(firebaseUser);
                loginSuccess(user);
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                view.hideLoadingIcon();
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) view.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        android.view.View focus = view.getActivity().getCurrentFocus();
        if(focus == null){
            focus = new android.view.View(view.getActivity());
        }
        imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }


    public interface View {
        Activity getActivity();

        /**
         * Finish current activity
         */
        void finishView();

        /**
         * Show loading icon
         */
        void showLoading();

        /**
         * Hide loading icon
         */
        void hideLoadingIcon();
    }
}
