package es.formulastudent.app.mvp.view.activity.userdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import es.formulastudent.app.FSSApp;
import es.formulastudent.app.R;
import es.formulastudent.app.di.component.AppComponent;
import es.formulastudent.app.di.component.DaggerUserDetailComponent;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.UserDetailModule;
import es.formulastudent.app.mvp.data.model.dto.UserDTO;
import es.formulastudent.app.mvp.view.activity.GeneralActivity;
import es.formulastudent.app.mvp.view.activity.NFCReaderActivity;


public class UserDetailActivity extends GeneralActivity implements UserDetailPresenter.View, View.OnClickListener {

    private static final int NFC_REQUEST_CODE = 101;

    @Inject
    UserDetailPresenter presenter;

    //View components
    private TextView userName;
    private ImageView userProfilePhoto;
    private ImageView userNFCImage;
    private TextView userNFCTag;

    //Selected user
    UserDTO user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupComponent(FSSApp.getApp().component());
        setContentView(R.layout.activity_user_detail);
        super.onCreate(savedInstanceState);

        user = (UserDTO) getIntent().getSerializableExtra("selectedUser");

        initViews();
    }


    /**
     * Inject dependencies method
     * @param appComponent
     */
    protected void setupComponent(AppComponent appComponent) {

        DaggerUserDetailComponent.builder()
                .appComponent(appComponent)
                .contextModule(new ContextModule(this))
                .userDetailModule(new UserDetailModule(this))
                .build()
                .inject(this);
    }


    private void initViews(){

        //Add toolbar title
        setToolbarTitle(getString(R.string.activity_user_detail_label));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Instantiate vies components
        userName = findViewById(R.id.user_detail_name);
        userProfilePhoto = findViewById(R.id.user_detail_profile_image);
        userNFCTag = findViewById(R.id.user_detail_nfc_tag);
        userNFCImage = findViewById(R.id.user_detail_nfc_image);
        userNFCImage.setOnClickListener(this);

        //Set data
        userName.setText(user.getName());
        Picasso.get().load(user.getPhotoUrl()).into(userProfilePhoto);

        if(user.getNFCTag()!=null && !user.getNFCTag().isEmpty()){
            userNFCImage.setImageResource(R.drawable.ic_user_nfc_registered);
            userNFCTag.setText(user.getNFCTag());
        }else{
            userNFCImage.setImageResource(R.drawable.ic_user_nfc_not_registered);
            userNFCTag.setText("Not registered");
        }
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishView() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoadingIcon() {

    }

    @Override
    public void updateNFCInformation(String TAG) {
        userNFCImage.setImageResource(R.drawable.ic_user_nfc_registered);
        userNFCTag.setText(TAG);
        Toast.makeText(this, "User successfully registered", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.user_detail_nfc_image){
            Intent i = new Intent(this, NFCReaderActivity.class);
            startActivityForResult(i, NFC_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NFC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                presenter.onNFCTagDetected(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}