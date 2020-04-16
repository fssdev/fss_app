package es.formulastudent.app.di.component;

import javax.inject.Singleton;

import dagger.Component;
import es.formulastudent.app.di.module.activity.TeamsModule;
import es.formulastudent.app.di.module.business.SharedPreferencesModule;
import es.formulastudent.app.di.module.business.UtilsModule;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.screen.teams.TeamsFragment;
import es.formulastudent.app.mvp.view.screen.teams.TeamsPresenter;
import es.formulastudent.app.mvp.view.utils.LoadingDialog;

@Singleton
@Component(modules = {UtilsModule.class, TeamsModule.class, SharedPreferencesModule.class}, dependencies = {AppComponent.class})
public interface TeamsComponent {
    void inject(TeamsFragment teamsFragment);
    TeamsPresenter getMainPresenter();
    User getLoggedUser();
    LoadingDialog getLoadingDialog();
}
