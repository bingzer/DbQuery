VERSION 2.0.0
=============
* Various bug fixes
* Read-only database: `db.getConfig().setReadOnly(true)`
* `UPDATE` and `INSERT` Refactoring
* Column `Id` is now `Long` type (was `Integer`)
* Support function with condition: (i.e: `db.get("Products").avg("Prices", "ProductName LIKE ?", "Key%")`)
* Major code and API refactoring

VERSION 1.0.0
=============
Initial Release
