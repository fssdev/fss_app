package es.formulastudent.app.di.module.activity;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.business.BusinessModule;
import es.formulastudent.app.di.module.business.UtilsModule;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.view.screen.user.UserPresenter;

@Module(includes = {UtilsModule.class, ContextModule.class, BusinessModule.class})
public class UserListModule {

    private UserPresenter.View view;

    public UserListModule(UserPresenter.View view) {
        this.view = view;
    }

    @Provides
    public UserPresenter.View provideView() {
        return view;
    }

    @Provides
    public UserPresenter providePresenter(UserPresenter.View categoryView, UserBO userBO,
                                          TeamBO teamBO) {
        return new UserPresenter(categoryView, userBO, teamBO);
    }
}
