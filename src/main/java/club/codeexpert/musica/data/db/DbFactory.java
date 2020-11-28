package club.codeexpert.musica.data.db;

import android.content.Context;

import androidx.room.Room;

public class DbFactory {
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (null == instance)
            instance = Room.databaseBuilder(context, AppDatabase.class, "database-name").build();

        return instance;
    }

    public static synchronized AppDatabase getInstance() {
        if (null == instance) {
            throw new IllegalStateException(DbFactory.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }

        return instance;
    }

}
