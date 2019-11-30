package es.formulastudent.app.di.module.activity;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.business.BusinessModule;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.view.activity.teams.TeamsPresenter;

@Module(includes = {ContextModule.class, BusinessModule.class})
public class TeamsModule {

    private TeamsPresenter.View view;

    public TeamsModule(TeamsPresenter.View view) {
        this.view = view;
    }

    @Provides
    public TeamsPresenter.View provideView() {
        return view;
    }

    @Provides
    public TeamsPresenter providePresenter(TeamsPresenter.View categoryView, Context context, TeamBO teamBO) {
        return new TeamsPresenter(categoryView, context, teamBO);
    }
}
