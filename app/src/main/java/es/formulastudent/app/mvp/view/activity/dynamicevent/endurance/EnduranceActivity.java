package es.formulastudent.app.mvp.view.activity.dynamicevent.endurance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import es.formulastudent.app.FSSApp;
import es.formulastudent.app.R;
import es.formulastudent.app.di.component.AppComponent;
import es.formulastudent.app.di.component.DaggerEnduranceComponent;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.activity.EnduranceModule;
import es.formulastudent.app.mvp.data.model.Team;
import es.formulastudent.app.mvp.view.activity.NFCReaderActivity;
import es.formulastudent.app.mvp.view.activity.dynamicevent.endurance.recyclerview.EnduranceRegistersAdapter;
import es.formulastudent.app.mvp.view.activity.general.GeneralActivity;
import es.formulastudent.app.mvp.view.activity.general.spinneradapters.TeamsSpinnerAdapter;


public class EnduranceActivity extends GeneralActivity implements ChipGroup.OnCheckedChangeListener, EndurancePresenter.View, View.OnClickListener {

    private static final int NFC_REQUEST_CODE = 101;

    @Inject
    EndurancePresenter presenter;

    //View components
    private RecyclerView recyclerView;
    private EnduranceRegistersAdapter registersAdapter;
    private TeamsSpinnerAdapter teamsAdapter;
    private FloatingActionButton buttonAddRegister;
    private Spinner teamsSpinner;
    private ChipGroup dayListGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupComponent(FSSApp.getApp().component());
        setContentView(R.layout.activity_endurance);
        super.onCreate(savedInstanceState);

        initViews();
        presenter.retrieveEnduranceRegisterList();
    }

    @Override
    public Activity getActivity(){
        return this;
    }

    /**
     * Inject dependencies method
     * @param appComponent
     */
    protected void setupComponent(AppComponent appComponent) {

        DaggerEnduranceComponent.builder()
                .appComponent(appComponent)
                .contextModule(new ContextModule(this))
                .enduranceModule(new EnduranceModule(this))
                .build()
                .inject(this);
    }

    private void initViews(){

        //Add drawer
        addDrawer();
        mDrawerIdentifier = 10015L;

        //Recycler view
        recyclerView = findViewById(R.id.recyclerView);
        registersAdapter = new EnduranceRegistersAdapter(presenter.getEnduranceRegisterList(), this);
        recyclerView.setAdapter(registersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //Add endurance register button
        buttonAddRegister = findViewById(R.id.button_add_endurance_register);
        buttonAddRegister.setOnClickListener(this);

        //Teams Spinner
        teamsSpinner = findViewById(R.id.endurance_team_spinner);
        presenter.retrieveTeams();

        //Chip Group
        dayListGroup = findViewById(R.id.endurance_chip_group);
        dayListGroup.setOnCheckedChangeListener(this);

        //Add toolbar title
        setToolbarTitle(getString(R.string.endurance_activity_title));

    }


    public void initializeTeamsSpinner(List<Team> teams){
        teamsAdapter = new TeamsSpinnerAdapter(this, android.R.layout.simple_spinner_item, teams);
        teamsSpinner.setAdapter(teamsAdapter);
        teamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Team team = teamsAdapter.getItem(position);
                presenter.setSelectedTeamID(team.getID());
                presenter.retrieveEnduranceRegisterList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter) {  }
        });
    }



    @Override
    protected void onStart(){
        super.onStart();
        drawer.setSelection(mDrawerIdentifier, false);
    }


    @Override
    public void finishView() {

    }

    @Override
    public void showLoading() {
        super.showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        super.hideLoadingDialog();
    }

    @Override
    public void refreshEnduranceRegisterItems() {
        registersAdapter.notifyDataSetChanged();
        this.hideLoading();
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_add_endurance_register){
            showLoading();
            Intent i = new Intent(this, NFCReaderActivity.class);
            startActivityForResult(i, NFC_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //NFC reader
        if (requestCode == NFC_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                presenter.onNFCTagDetected(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onCheckedChanged(ChipGroup chipGroup, int selectedChipId) {

        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String competitionDayStr = null;

        if(selectedChipId == R.id.endurance_chip_wed){
            competitionDayStr = getString(R.string.competition_day_wed);
        }else if(selectedChipId == R.id.endurance_chip_thu){
            competitionDayStr = getString(R.string.competition_day_thu);
        }else if(selectedChipId == R.id.endurance_chip_fri){
            competitionDayStr = getString(R.string.competition_day_fri);
        }else if(selectedChipId == R.id.endurance_chip_sat){
            competitionDayStr = getString(R.string.competition_day_sat);
        }else if(selectedChipId == R.id.endurance_chip_sun){
            competitionDayStr = getString(R.string.competition_day_sun);
        }else{ //all
            presenter.setSelectedDateFrom(null);
            presenter.setSelectedDateTo(null);
        }

        //Get From and To dates
        if(competitionDayStr != null){

            try{

                Date competitionDate = sdf.parse(competitionDayStr);

                //From
                Calendar calFrom = Calendar.getInstance();
                calFrom.setTime(competitionDate);
                calFrom.set(Calendar.HOUR_OF_DAY, 0);
                calFrom.set(Calendar.MINUTE, 0);
                calFrom.set(Calendar.SECOND, 0);

                //To
                Calendar calTo = Calendar.getInstance();
                calTo.setTime(competitionDate);
                calTo.set(Calendar.HOUR_OF_DAY, 23);
                calTo.set(Calendar.MINUTE, 59);
                calTo.set(Calendar.SECOND, 59);

                //Update From and To values
                presenter.setSelectedDateFrom(calFrom.getTime());
                presenter.setSelectedDateTo(calTo.getTime());

            }catch (ParseException pe){
                createMessage("Error parsing dates");
            }
        }

        //Update list
        presenter.retrieveEnduranceRegisterList();

    }
}