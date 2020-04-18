package es.formulastudent.app.mvp.view.screen.teamsdetailscrutineering;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import es.formulastudent.app.mvp.data.business.BusinessCallback;
import es.formulastudent.app.mvp.data.business.ResponseDTO;
import es.formulastudent.app.mvp.data.business.egress.EgressBO;
import es.formulastudent.app.mvp.data.business.raceaccess.RaceAccessBO;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.teammember.TeamMemberBO;
import es.formulastudent.app.mvp.data.model.EventRegister;
import es.formulastudent.app.mvp.data.model.EventType;
import es.formulastudent.app.mvp.data.model.PreScrutineeringRegister;
import es.formulastudent.app.mvp.data.model.Team;
import es.formulastudent.app.mvp.data.business.DataConsumer;
import es.formulastudent.app.mvp.view.screen.teamsdetailscrutineering.tabs.TeamsDetailFragment;
import es.formulastudent.app.mvp.view.utils.messages.Messages;


public class TeamsDetailScrutineeringPresenter extends DataConsumer {

    //Dependencies
    private View view;
    private TeamBO teamBO;
    private RaceAccessBO raceAccessBO;
    private TeamMemberBO teamMemberBO;
    private EgressBO egressBO;
    private Messages messages;

    //Data
    private List<EventRegister> eventRegisterList = new ArrayList<>();


    public TeamsDetailScrutineeringPresenter(TeamsDetailScrutineeringPresenter.View view, TeamBO teamBO,
                                             RaceAccessBO raceAccessBO, EgressBO egressBO,
                                             TeamMemberBO teamMemberBO, Messages messages) {
        super(teamBO, raceAccessBO, teamMemberBO, egressBO);
        this.view = view;
        this.teamBO = teamBO;
        this.raceAccessBO = raceAccessBO;
        this.teamMemberBO = teamMemberBO;
        this.egressBO = egressBO;
        this.messages = messages;
    }

    public void updateTeam(final Team mofifiedTeam, final Team originalTeam) {
        teamBO.updateTeam(mofifiedTeam, new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                //Get fragments and update fields with the new values
                List<Fragment> fragmentList = view.getViewFragmentManager().getFragments();
                for(Fragment fragment: fragmentList){
                    if(fragment instanceof TeamsDetailFragment){
                        ((TeamsDetailFragment)fragment).updateView(mofifiedTeam);
                    }
                }
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {

                //Get fragments and update fields with the old values
                List<Fragment> fragmentList = view.getViewFragmentManager().getFragments();
                for(Fragment fragment: fragmentList){
                    if(fragment instanceof TeamsDetailFragment) {
                        ((TeamsDetailFragment) fragment).updateView(originalTeam);
                    }
                }
            }
        });
    }

    public void retrieveEgressRegisterList() {
        raceAccessBO.retrieveRegisters(null, null, view.getCurrentTeam().getID(), null, EventType.PRE_SCRUTINEERING, new BusinessCallback() {

            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                List<EventRegister> results = (List<EventRegister>) responseDTO.getData();
                updateEventRegisters(results==null ? new ArrayList<>() : results);
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                messages.showError(responseDTO.getError());
            }
        });
    }


    private void updateEventRegisters(List<EventRegister> items){
        this.eventRegisterList.clear();
        this.eventRegisterList.addAll(items);
        this.view.refreshRegisterItems();
    }

    public List<EventRegister> getEventRegisterList(){
        return eventRegisterList;
    }


    /**
     * Retrieve user by NFC tag after read
     * @param tag
     */
    public void onNFCTagDetected(String tag) {
        teamMemberBO.retrieveTeamMemberByNFCTag(tag,
                teamMember -> {
                    //The driver can run, create the register
                    raceAccessBO.createRegister(teamMember, teamMember.getCarNumber(), null, EventType.PRE_SCRUTINEERING,
                            new BusinessCallback() {
                                @Override
                                public void onSuccess(ResponseDTO responseDTO) {
                                    PreScrutineeringRegister register = (PreScrutineeringRegister) responseDTO.getData();
                                    createEgressRegister(register);
                                }

                                @Override
                                public void onFailure(ResponseDTO responseDTO) {
                                    messages.showError(responseDTO.getError());
                                }
                            });
                }, error -> messages.showError(1)); //FIXME delete when implemented liveData messages here
    }


    /**
     * Call business to create the Egress register
     * @param register
     */
    private void createEgressRegister(PreScrutineeringRegister register){
        egressBO.createRegister(register.getID(), new BusinessCallback() {
            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                retrieveEgressRegisterList();
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                messages.showError(responseDTO.getError());
            }
        });
    }


    /**
     * It is time to update the chrono time
     * @param milliseconds
     * @param registerID
     */
    public void onChronoTimeRegistered(Long milliseconds, String registerID) {
        raceAccessBO.updatePreScrutineeringRegister(registerID, milliseconds, new BusinessCallback() {

            @Override
            public void onSuccess(ResponseDTO responseDTO) {
                retrieveEgressRegisterList();
            }

            @Override
            public void onFailure(ResponseDTO responseDTO) {
                messages.showError(responseDTO.getError());
            }
        });
    }



    public interface View {

        Activity getActivity();

        /**
         * Get fragment manager
         * @return
         */
        FragmentManager getViewFragmentManager();

        /**
         * Get current team
         * @return
         */
        Team getCurrentTeam();

        /**
         * Refresh the list items
         */
        void refreshRegisterItems();
    }
}
