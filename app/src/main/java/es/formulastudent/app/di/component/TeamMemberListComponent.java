package es.formulastudent.app.di.component;

import javax.inject.Singleton;

import dagger.Component;
import es.formulastudent.app.di.module.activity.TeamMemberListModule;
import es.formulastudent.app.di.module.business.SharedPreferencesModule;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.screen.teammember.TeamMemberFragment;
import es.formulastudent.app.mvp.view.screen.teammember.TeamMemberPresenter;
import es.formulastudent.app.mvp.view.utils.LoadingDialog;


@Singleton
@Component(modules = {TeamMemberListModule.class, SharedPreferencesModule.class}, dependencies = {AppComponent.class})
public interface TeamMemberListComponent {
    void inject(TeamMemberFragment teamMemberFragment);
    TeamMemberPresenter getMainPresenter();
    User getLoggedUser();
    LoadingDialog getLoadingDialog();
}
