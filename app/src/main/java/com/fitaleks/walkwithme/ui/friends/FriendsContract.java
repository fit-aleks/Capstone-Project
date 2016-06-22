package com.fitaleks.walkwithme.ui.friends;

import com.fitaleks.walkwithme.ui.presenters.BasePresenter;
import com.fitaleks.walkwithme.ui.presenters.BaseView;

/**
 * Created by alexander on 22.06.16.
 */
public interface FriendsContract {
    interface Presenter extends BasePresenter {
        void reloadFriendsList();
    }

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showNoData();
    }
}
