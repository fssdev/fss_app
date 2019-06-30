package es.formulastudent.app.mvp.view.activity.dynamicevent;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.formulastudent.app.mvp.data.business.BusinessCallback;
import es.formulastudent.app.mvp.data.business.ResponseDTO;
import es.formulastudent.app.mvp.data.business.briefing.BriefingBO;
import es.formulastudent.app.mvp.data.business.dynamicevent.DynamicEventBO;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.data.model.BriefingRegister;
import es.formulastudent.app.mvp.data.model.Car;
import es.formulastudent.app.mvp.data.model.EventRegister;
import es.formulastudent.app.mvp.data.model.EventType;
import es.formulastudent.app.mvp.data.model.Team;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.activity.dynamicevent.dialog.ConfirmEventRegisterDialog;
import es.formulastudent.app.mvp.view.activity.dynamicevent.recyclerview.RecyclerViewLongClickedListener;


public class DynamicEventPresenter implements RecyclerViewLongClickedListener {

    //DYNAMIC EVENT TYPE
    EventType eventType;


    //Dependencies
    private View view;
    private Context context;
    private TeamBO teamBO;
    private DynamicEventBO dynamicEventBO;
    private UserBO userBO;
    private BriefingBO briefingBO;


    //Data
    List<EventRegister> allEventRegisterList = new ArrayList<>();
    List<EventRegister> filteredEventRegisterList = new ArrayList<>();

    //Filtering values
    List<Team> teams;
    String selectedTeamID;
    String selectedDay;
    Long selectedCarNumber;

    //Selected chip to filter
    private Date selectedDateFrom;
    private Date selectedDateTo;


    public DynamicEventPresenter(DynamicEventPresenter.View view, Context context, TeamBO teamBO,
                                 DynamicEventBO dynamicEventBO, UserBO userBO, BriefingBO briefingBO, EventType eventType) {
        this.view = view;
        this.context = context;
        this.teamBO = teamBO;
        this.dynamicEventBO = dynamicEventBO;
        this.userBO = userBO;
        this.briefingBO = briefingBO;
        this.eventType = eventType;
    }


    /**
     * Create register
     * @param user
     * @param carNumber
     * @param carType
     * @param briefingDone
     */
     public void createRegistry(User user, Long carNumber, String carType, Boolean briefingDone){

        //Show loading
        view.showLoading();

         dynamicEventBO.createRegister(user, carType, carNumber, briefingDone, eventType, new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                retrieveRegisterList();
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                view.createMessage("Couldn't not create the registry");
            }
        });
    }


    /**
     * Retrieve Event registers
     */
     public void retrieveRegisterList() {

        //Show loading
        view.showLoading();

        //Call Event business
        dynamicEventBO.retrieveRegisters(selectedDateFrom, selectedDateTo, selectedTeamID, selectedCarNumber, eventType, new BusinessCallback() {

             @Override
             public void onSuccess(ResponseDTO responseDTO) {
                     List<EventRegister> results = (List<EventRegister>) responseDTO.getData();
                     updateEventRegisters(results==null ? new ArrayList<EventRegister>() : results);
             }

             @Override
             public void onFailure(ResponseDTO responseDTO) {
                view.createMessage("Couldn't get the register");
             }
         });
    }


    public void updateEventRegisters(List<EventRegister> items){
        //Update all-register-list
        this.allEventRegisterList.clear();
        this.allEventRegisterList.addAll(items);

        //Update and refresh filtered-register-list
        this.filteredEventRegisterList.clear();
        this.filteredEventRegisterList.addAll(items);
        this.view.refreshEventRegisterItems();
    }


    /**
     * Retrieve user by NFC tag after read
     * @param tag
     */
    void onNFCTagDetected(String tag){

        userBO.retrieveUserByNFCTag(tag, new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                User user = (User)responseDTO.getData();

                //Now check if the user did the briefing today
                getUserBriefingRegister(user);
            }
            @Override
            public void onFailure(ResponseDTO responseDTO) {
                view.createMessage("Couldn't get the user by this Tag");
            }
        });
    }

    void getUserBriefingRegister(final User user){

        Calendar cal = Calendar.getInstance();
        Date to = cal.getTime();

        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 5);
        cal.set(Calendar.SECOND, 0);

        Date from = cal.getTime(); //current day at 05:00am

        if(user != null && user.getID() != null) {
            briefingBO.retrieveBriefingRegistersByUserAndDates(from, to, user.getID(), new BusinessCallback() {
                @Override
                public void onSuccess(ResponseDTO responseDTO) {

                    List<BriefingRegister> briefingRegisters = (List<BriefingRegister>) responseDTO.getData();

                    //Get now cars
                    getCarsByUserId(user, !briefingRegisters.isEmpty());
                }

                @Override
                public void onFailure(ResponseDTO responseDTO) {
                    //TODO mostrar mensajes
                }
            });
        } else {
            view.hideLoading();
            view.createMessage("User does not exist");
        }
    }

    void getCarsByUserId(final User user, final boolean briefingExists){

        teamBO.retrieveTeamById(user.getTeamID(), new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {

                Team team = (Team) responseDTO.getData();
                Car car = team.getCar();

                //With all the information, we open the dialog
                FragmentManager fm = ((DynamicEventActivity)view.getActivity()).getSupportFragmentManager();
                ConfirmEventRegisterDialog createUserDialog = ConfirmEventRegisterDialog
                        .newInstance(DynamicEventPresenter.this, user, briefingExists, car);
                createUserDialog.show(fm, "fragment_event_confirm");
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                view.createMessage("Couldn't get the team from this user");
            }
        });
    }


    /**
     * Retrieve teams from database
     */
    void retrieveTeams(){

        //Show loading
        view.showLoading();

        //Call business to retrieve teams
        teamBO.retrieveAllTeams(new BusinessCallback() {

            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                List<Team> teams = (List<Team>)responseDTO.getData();

                //Add "All" option
                Team teamAll = new Team("-1", "All");
                teams.add(0, teamAll);

                openFilteringDialog(teams);

            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                //TODO mostrar mensajes de error
            }

        });
    }



    @Override
    public void recyclerViewListLongClicked(android.view.View v, int position) {
        EventRegister selectedRegister = filteredEventRegisterList.get(position);

        //With all the information, we open the dialog
        FragmentManager fm = ((DynamicEventActivity)view.getActivity()).getSupportFragmentManager();
        ConfirmEventRegisterDialog createUserDialog = ConfirmEventRegisterDialog
                .newInstance(DynamicEventPresenter.this, selectedRegister);
        createUserDialog.show(fm, "fragment_event_confirm");

    }


    public void openFilteringDialog(List<Team> teams){
        this.teams = teams;

        //With all the information, we open the dialog
        FragmentManager fm = ((DynamicEventActivity)view.getActivity()).getSupportFragmentManager();
        FilteringRegistersDialog createUserDialog = FilteringRegistersDialog
                .newInstance(this, teams, selectedTeamID, selectedCarNumber, selectedDay);
        createUserDialog.show(fm, "fragment_event_confirm");

        //Hide loading right after showing the filtering dialog
        view.hideLoading();
    }


    void filterIconClicked(){

        //Go retrieve teams if we have not yet
        if(teams == null){
            retrieveTeams();
        }else{
            openFilteringDialog(teams);
        }
    }

    public void setFilteringValues(Date selectedDateFrom, Date selectedDateTo, String selectedDay, String selectedTeamID, Long selectedCarNumber){
        this.selectedDateFrom = selectedDateFrom;
        this.selectedDateTo = selectedDateTo;
        this.selectedTeamID = selectedTeamID;
        this.selectedDay = selectedDay;
        this.selectedCarNumber = selectedCarNumber;

        view.filtersActivated(
                selectedDay!=null
                        || selectedCarNumber != null
                        || (selectedTeamID != null && !selectedTeamID.equals("-1"))
        );
    }

    public void createMessage(String message){
        view.createMessage(message);
    }


    public List<EventRegister> getEventRegisterList() {
        return filteredEventRegisterList;
    }

    public void setSelectedTeamID(String selectedTeamID) {
        this.selectedTeamID = selectedTeamID;
    }





    public interface View {

        Activity getActivity();

        /**
         * Show message to user
         * @param message
         */
        void createMessage(String message);

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
        void hideLoading();

        /**
         * Refresh items in list
         */
        void refreshEventRegisterItems();

        /**
         * Method to know if the filters are activated
         * @param activated
         */
        void filtersActivated(Boolean activated);
    }

}