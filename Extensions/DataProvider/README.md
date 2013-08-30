`DataProvider`
``` java
public class ChinookDataProvider extends BasicDataProvider {

    @Override
    public IDatabase openDatabase() {
        File dbFile = ...
        IDatabase db = DbQuery.getDatabase("Chinook.sqlitex");
        db.open(1, dbFile.getAbsolutePath(), new SQLiteBuilder() {
          ...
        });

        return db;
    }

    @Override
    public String getAuthority() {
        return "com.bingzer.android.dbv.data.test";
    }
}
```
