package club.codeexpert.musica.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import club.codeexpert.musica.R;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(String.valueOf(R.string.empty_notifications));
    }

    public LiveData<String> getText() {
        return mText;
    }
}