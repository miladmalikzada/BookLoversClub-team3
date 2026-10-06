# Defense notes — Team 3 (Flyweight, Visitor, Template Method)

## 1. How does the pattern differ from a similar one?

**Flyweight vs Singleton.** Singleton: exactly one instance of one class (`UserRepository`).
Flyweight: a pool with one instance *per key* (one `Book` per ISBN, so 7 instances for 7 books),
and the point is saving memory by sharing immutable state, not controlling global access.

**Template Method vs Strategy.** Template Method fixes the order of steps in a base class (`generate()` is `final`)
and subclasses override individual steps (inheritance, chosen when the object is created).
Strategy replaces the whole algorithm by composition and can be swapped at runtime (`MatchStrategy`).

**Visitor vs Strategy / Decorator (bonus).** Visitor adds *new operations* over a fixed set of classes
(`User`, `Book`) via double dispatch; it does not change behaviour of the objects themselves.

## 2. What would change without the pattern?

- **Without Flyweight:** `LegacyCatalogAdapter` creates `new Book` on every lookup. 14 reads = 14 book objects,
  identical books are different objects, `==` fails, memory grows with the number of readers.
- **Without Visitor:** `User` and `Book` would get `countGenres()`, `averageAge()`, `topAuthors()`...
  and must be edited for every new statistic; reporting code leaks into the model.
- **Without Template Method:** each report format would repeat the header/body/footer order
  (copy-paste); one change in the structure would have to be made three times.

## 3. Interaction with existing patterns

- Adapter → Flyweight: the adapter converts the legacy record, then asks the factory for the shared `Book`.
- Facade → everything: `findBook`, `addReadBook`, `collectStatistics`, `generateReport`.
- Singleton → Visitor: `ClubStatistics.collect` walks over the users from `UserRepository`.
- Visitor → Template Method: reports contain no counting logic; they only format what the visitors collected.
- Builder: `User.Builder` is unchanged; reading data is added later through `ReadingRecord`.

## 4. Extending without modifying existing classes

- **Another report format:** `class HtmlReport extends ClubReport { ... }` and
  `club.generateReport(HtmlReport::new)`. Nothing else changes (Open/Closed).
- **Another statistic:** a new class implementing `ClubVisitor`.
  (Trade-off: adding a new *element* class, e.g. `Meetup`, requires a new `visitMeetup` in every visitor.)
- **Another filtering/validation rule:** a new `RegistrationValidator` subclass linked into the chain.

## Why `visitBook` is called once per read

`User.accept` calls `visitUser(this)` and then `book.accept(visitor)` for each reading record.
Because the `Book` is a shared flyweight, visiting "all books once" would lose the number of reads;
so the visitor is sent through the users' records, which gives "reads per genre / per author" for free.
