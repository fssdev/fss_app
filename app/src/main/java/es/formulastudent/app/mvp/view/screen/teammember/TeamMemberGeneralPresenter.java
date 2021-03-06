package es.formulastudent.app.mvp.view.screen.teammember;

import es.formulastudent.app.mvp.data.model.TeamMember;

public interface TeamMemberGeneralPresenter {

    /**
     * Create or update team member
     * @param teamMember
     */
    void updateOrCreateTeamMember(TeamMember teamMember);
}
