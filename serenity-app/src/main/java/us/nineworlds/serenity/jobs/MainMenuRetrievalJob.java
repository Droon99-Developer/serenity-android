package us.nineworlds.serenity.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.RetryConstraint;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import us.nineworlds.plex.rest.PlexappFactory;
import us.nineworlds.plex.rest.model.impl.MediaContainer;
import us.nineworlds.serenity.events.MainMenuEvent;
import us.nineworlds.serenity.injection.InjectingJob;

public class MainMenuRetrievalJob extends InjectingJob {

    @Inject
    PlexappFactory client;

    @Inject
    EventBus eventBus;


    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        MediaContainer mediaContainer = client.retrieveSections();
        MainMenuEvent event = new MainMenuEvent(mediaContainer);
        eventBus.post(event);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}