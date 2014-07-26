VERSION 2.1.0 (07/26/2014)
=============
* Built-in supports for ORM (`BaseEntity` + `IEnvironment`)
* Iterables changed to Collection
* Smarter error handler
* Various bug fixes

VERSION 2.0.0 (04/30/2014)
=============
* Various bug fixes
* Read-only database: `db.getConfig().setReadOnly(true)`
* `UPDATE` and `INSERT` Refactoring
* Column `Id` is now `Long` type (was `Integer`)
* Support function with condition: (i.e: `db.get("Products").avg("Prices", "ProductName LIKE ?", "Key%")`)
* Major code and API refactoring

VERSION 1.0.0 (12/01/2013)
=============
Initial Release
