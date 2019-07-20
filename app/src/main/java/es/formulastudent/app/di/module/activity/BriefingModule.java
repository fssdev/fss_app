package es.formulastudent.app.di.module.activity;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.business.BusinessModule;
import es.formulastudent.app.mvp.data.business.briefing.BriefingBO;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.view.activity.briefing.BriefingPresenter;

@Module(includes = {ContextModule.class, BusinessModule.class})
public class BriefingModule {

    private BriefingPresenter.View view;

    public BriefingModule(BriefingPresenter.View view) {
        this.view = view;
    }

    @Provides
    public BriefingPresenter.View provideView() {
        return view;
    }

    @Provides
    public BriefingPresenter providePresenter(BriefingPresenter.View categoryView, Context context,
                                              TeamBO teamBO, BriefingBO briefingBO, UserBO userBO, FirebaseAuth firebaseAuth) {
        return new BriefingPresenter(categoryView, context, teamBO, briefingBO, userBO, firebaseAuth);
    }


}
