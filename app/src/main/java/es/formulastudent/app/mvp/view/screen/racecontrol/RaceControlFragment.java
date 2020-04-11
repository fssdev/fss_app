package es.formulastudent.app.mvp.view.screen.racecontrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

import es.formulastudent.app.FSSApp;
import es.formulastudent.app.R;
import es.formulastudent.app.di.component.AppComponent;
import es.formulastudent.app.di.component.DaggerRaceControlComponent;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.activity.RaceControlModule;
import es.formulastudent.app.mvp.data.model.RaceControlEvent;
import es.formulastudent.app.mvp.view.screen.racecontrol.recyclerview.RaceControlAdapter;


public class RaceControlFragment extends Fragment implements
        RaceControlPresenter.View, View.OnClickListener{

    @Inject
    RaceControlPresenter presenter;

    private String raceRound; //Round 1, Round 2 or Final
    private String rcArea;

    //Real-time register listener
    private ListenerRegistration registerListener;

    private RaceControlAdapter rcAdapter;
    private FloatingActionButton buttonAddVehicle;
    private MenuItem filterItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_race_control, container, false);

        //Get the event type (Endurance, AutoX...)
        assert getArguments() != null;
        RaceControlFragmentArgs args = RaceControlFragmentArgs.fromBundle(getArguments());
        RaceControlEvent rcEvent = args.getRaceControlEvent();

        if(rcEvent.equals(RaceControlEvent.ENDURANCE)){
            this.raceRound = args.getRaceControlRound();
            this.rcArea = args.getRaceControlArea();
        }

        setupComponent(FSSApp.getApp().component(), rcEvent, raceRound, rcArea);
        setHasOptionsMenu(true);

        //Remove elevation from Action bar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);

        initViews(view);
        return view;
    }

    /**
     * Inject dependencies method
     * @param appComponent
     */
    protected void setupComponent(AppComponent appComponent, RaceControlEvent rcEvent, String raceType, String rcArea) {
        DaggerRaceControlComponent.builder()
                .appComponent(appComponent)
                .contextModule(new ContextModule(getContext(), getActivity()))
                .raceControlModule(new RaceControlModule(this, rcEvent, raceType, rcArea))
                .build()
                .inject(this);
    }

    private void initViews(View view){
        //View components
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        rcAdapter = new RaceControlAdapter(presenter.getEventRegisterList(), getContext(), presenter, presenter);
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && buttonAddVehicle.isShown())
                    buttonAddVehicle.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    buttonAddVehicle.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Add vehicle button
        buttonAddVehicle = view.findViewById(R.id.button_add_vehicle);
        buttonAddVehicle.setOnClickListener(this);

        //Round
        TextView roundTextView = view.findViewById(R.id.round_number);
        roundTextView.setText(raceRound == null ? "-" : raceRound);

        //Area
        TextView areaTextView = view.findViewById(R.id.area);
        areaTextView.setText(rcArea == null ? "-" : rcArea);
    }


    @Override
    public void refreshEventRegisterItems() {
        rcAdapter.notifyDataSetChanged();
    }


    @Override
    public void filtersActivated(Boolean activated) {
        if(activated){
            filterItem.setIcon(R.drawable.ic_filter_active);
        }else{
            filterItem.setIcon(R.drawable.ic_filter_inactive);
        }
    }

    @Override
    public void closeUpdatedRow(String id) {
        rcAdapter.closeSwipeRow(id);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_add_vehicle){
            presenter.openCreateRegisterDialog();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dynamic_event, menu);
        filterItem = menu.findItem(R.id.filter_results);
        filterItem.setOnMenuItemClickListener(menuItem -> {
            presenter.filterIconClicked();
            return true;
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        registerListener.remove();
    }

    @Override
    public void onResume(){
        super.onResume();

        //Remove realTime listener
        if(registerListener != null){
            registerListener.remove();
        }

        //Refresh again the list after resuming activity
        registerListener = presenter.retrieveRegisterList();
    }
}