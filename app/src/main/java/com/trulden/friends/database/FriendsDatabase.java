package com.trulden.friends.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.trulden.friends.R;
import com.trulden.friends.database.entity.BindFriendInteraction;
import com.trulden.friends.database.entity.Friend;
import com.trulden.friends.database.entity.Interaction;
import com.trulden.friends.database.entity.InteractionType;
import com.trulden.friends.database.entity.LastInteraction;
import com.trulden.friends.util.Util;

import java.lang.ref.WeakReference;

/**
 * Handles database — initialization, migration.
 * Stores static instance of database.
 */
@Database(
        entities = {
                Friend.class, InteractionType.class, Interaction.class,
                BindFriendInteraction.class, LastInteraction.class},
        version = Util.DATABASE_VERSION,
        exportSchema = false
)
public abstract class FriendsDatabase extends RoomDatabase {

    private static WeakReference<Context> mContext;

    public static final String DATABASE_NAME = "friends_database";

    private static FriendsDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDataBaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDBAsync(INSTANCE).execute();
        }
    };

    public static void closeDatabase(){
        INSTANCE.close();
        wipeDatabaseReference();
    }

    /**
     * Set database instance reference to null. Used when reloading database.
     */
    public static void wipeDatabaseReference(){
        INSTANCE = null;
    }

    /**
     * Creates database instance, if there is none.
     * @param context context for which database build
     * @return database instance
     */
    public static FriendsDatabase getDatabase(final Context context){

        mContext = new WeakReference<>(context);

        if(INSTANCE == null){
            synchronized (FriendsDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FriendsDatabase.class, DATABASE_NAME)
                            .addMigrations(
                                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                                    MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                            .addCallback(sRoomDataBaseCallback)
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract FriendsDao friendsDao();

    /**
     * Populate db at start
     */
    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void>{

        String[] defaultInteractionsNames = {
                mContext.get().getString(R.string.interaction_type_name_meeting),
                mContext.get().getString(R.string.interaction_type_name_texting),
                mContext.get().getString(R.string.interaction_type_name_call)
        };
        int[]    defaultInteractionsFrequency = {30, 7, 30};

        private final FriendsDao mDao;

        PopulateDBAsync(FriendsDatabase db){
            mDao = db.friendsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(mDao.getAnyInteractionType() == null){
                for(int i = 0; i < defaultInteractionsNames.length; ++i){
                    mDao.add(new InteractionType(defaultInteractionsNames[i], defaultInteractionsFrequency[i]));
                }
            }

            return null;
        }
    }

    // -----------------------------------------
    // MIGRATIONS
    // -----------------------------------------

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE interaction_type_table (" +
                            "id INTEGER NOT NULL, " +
                            "interactionTypeName TEXT NOT NULL, " +
                            "frequency INTEGER NOT NULL, " +
                            "PRIMARY KEY(id))");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE interaction_table (" +
                    "id INTEGER NOT NULL," +
                    "interactionTypeId INTEGER NOT NULL," +
                    "date INTEGER NOT NULL," +
                    "comment TEXT," +
                    "PRIMARY KEY(id)," +
                    "FOREIGN KEY(interactionTypeId) REFERENCES interaction_type_table(id) ON DELETE CASCADE" +
                    ")"
            );

            database.execSQL(
                    "CREATE TABLE bind_friend_interaction_table (" +
                    "friendId INTEGER NOT NULL, " +
                    "interactionId INTEGER NOT NULL, " +
                    "PRIMARY KEY(friendId, interactionId), " +
                    "FOREIGN KEY(friendId) REFERENCES friend_table(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(interactionId) REFERENCES interaction_table(id) ON DELETE CASCADE" +
                    ")"
            );
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE interaction_table ADD friendNames TEXT");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX index_Interaction_typeId ON interaction_table(interactionTypeId)");
            database.execSQL("CREATE INDEX index_bindFI_interId ON bind_friend_interaction_table(interactionId)");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX index_Interaction_typeId;");

            database.execSQL("CREATE TABLE interaction_backup (" +
                    "id INTEGER NOT NULL," +
                    "interactionTypeId INTEGER NOT NULL," +
                    "date INTEGER NOT NULL," +
                    "comment TEXT," +
                    "PRIMARY KEY(id)," +
                    "FOREIGN KEY(interactionTypeId) REFERENCES interaction_type_table(id) ON DELETE CASCADE" +
                    ")");
            database.execSQL("INSERT INTO interaction_backup(id, interactionTypeId, date, comment) " +
                    "SELECT id, interactionTypeId, date, comment FROM interaction_table;");
            database.execSQL("DROP TABLE interaction_table;");
            database.execSQL("ALTER TABLE interaction_backup RENAME TO interaction_table");

            database.execSQL("CREATE INDEX index_Interaction_typeId ON interaction_table(interactionTypeId)");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE last_interaction_table(" +
                    "friendId INTEGER NOT NULL," +
                    "typeId INTEGER NOT NULL," +
                    "interactionId INTEGER NOT NULL," +
                    "date INTEGER NOT NULL," +
                    "status INTEGER NOT NULL DEFAULT 0," +
                    "PRIMARY KEY(friendId, typeId)," +
                    "FOREIGN KEY(friendId) REFERENCES friend_table(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(typeId) REFERENCES interaction_type_table(id) ON DELETE CASCADE," +
                    "FOREIGN KEY(interactionId) REFERENCES interaction_table(id) ON DELETE CASCADE);");

            database.execSQL(
                    "INSERT INTO last_interaction_table(friendId, typeId, interactionId, date)" +
                    "SELECT friendId, typeId, interactionId, date FROM\n" +
                        "(SELECT friend_table.id AS friendId, interaction_type_table.id AS typeId, interaction_table.id AS interactionId, MAX(interaction_table.date) AS date\n" +
                        " FROM \n" +
                            " (((interaction_table INNER JOIN bind_friend_interaction_table \n" +
                                "  ON interaction_table.id = bind_friend_interaction_table.interactionId) \n" +
                            "  INNER JOIN interaction_type_table\n" +
                                "  ON interaction_table.interactionTypeId = interaction_type_table.id)\n" +
                            "  INNER JOIN friend_table\n" +
                                "  ON bind_friend_interaction_table.friendId = friend_table.id)\n" +
                            " GROUP BY friendId, interactionTypeId\n" +
                            " ORDER BY interactionTypeId, date ASC)"
            );

            database.execSQL("CREATE INDEX index_LastInteraction_typeId ON last_interaction_table(typeId)");
            database.execSQL("CREATE INDEX index_LastInteraction_interactionId ON last_interaction_table(interactionId)");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE last_interaction_table ADD frequency INTEGER NOT NULL DEFAULT 0");

            database.execSQL(
                "UPDATE last_interaction_table " +
                "SET " +
                    "frequency = (SELECT interaction_type_table.frequency " +
                                  "FROM interaction_type_table " +
                                  "WHERE interaction_type_table.id = last_interaction_table.typeId) " +
                "WHERE " +
                    "EXISTS ( " +
                        "SELECT * " +
                        "FROM interaction_type_table " +
                        "WHERE interaction_type_table.id = last_interaction_table.typeId " +
                    ")"
            );
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE last_interaction_table ADD ready INTEGER NOT NULL DEFAULT 0");
        }
    };
}
