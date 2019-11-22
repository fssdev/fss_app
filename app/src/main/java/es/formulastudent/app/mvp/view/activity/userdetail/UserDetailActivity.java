package es.formulastudent.app.mvp.view.activity.userdetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import es.formulastudent.app.FSSApp;
import es.formulastudent.app.R;
import es.formulastudent.app.di.component.AppComponent;
import es.formulastudent.app.di.component.DaggerUserDetailComponent;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.activity.UserDetailModule;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.activity.NFCReaderActivity;
import es.formulastudent.app.mvp.view.activity.general.GeneralActivity;


public class UserDetailActivity extends GeneralActivity implements UserDetailPresenter.View, View.OnClickListener {

    private static final int NFC_REQUEST_CODE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;


    /**
     * TODO
     * Se tiene que poder cambiar:
     *  - Rol
     *  - Foto
     *
     *  Además se tiene que poder gestionar el préstamo de walkies y móviles
     */

    @Inject
    UserDetailPresenter presenter;

    //View components
    private TextView userName;
    private ImageView userProfilePhoto;
    private ImageView userNFCImage;
    private TextView userNFCTag;

    //Selected user
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupComponent(FSSApp.getApp().component());
        setContentView(R.layout.activity_team_member_detail);
        super.onCreate(savedInstanceState);

        user = (User) getIntent().getSerializableExtra("selectedUser");

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
        userProfilePhoto.setOnClickListener(this);
        userNFCTag = findViewById(R.id.user_detail_nfc_tag);
        userNFCImage = findViewById(R.id.user_detail_nfc_image);
        userNFCImage.setOnClickListener(this);

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
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.user_detail_profile_image){
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //Camera image
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //presenter.uploadProfilePicture(imageBitmap, user);
        }
    }

    @Override
    public void updateProfilePicture(Bitmap imageBitmap){
        userProfilePhoto.setImageBitmap(imageBitmap);
    }

    @Override
    public User getSelectedUser() {
        return user;
    }

    @Override
    public void openNFCReader() {
        Intent i = new Intent(this, NFCReaderActivity.class);
        startActivityForResult(i, NFC_REQUEST_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
