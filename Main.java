import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * =====================================================================
 *   Young Book Lovers Dating Club - Design Patterns Demo (Team 3)
 *   Direction: "Book Catalog and Analytics"
 * =====================================================================
 *  Existing patterns:
 *   1. Singleton               - repository.UserRepository
 *   2. Builder                 - model.User.Builder
 *   3. Factory Method          - notification.NotifierFactory
 *   4. Strategy                - matching.MatchStrategy
 *   5. Observer                - notification.ClubEventBus
 *   6. Decorator               - profile.ProfileView
 *   7. Adapter                 - catalog.LegacyCatalogAdapter
 *   8. Chain of Responsibility - validation.RegistrationValidator
 *   9. Facade                  - facade.BookLoversClub
 *  New patterns (Team 3):
 *  10. Flyweight               - catalog.BookFlyweightFactory (+ model.ReadingRecord)
 *  11. Visitor                 - analytics.ClubVisitor and its 3 visitors
 *  12. Template Method         - report.ClubReport (Text / Csv / Markdown)
 * =====================================================================
 */
public class Main {
    public static void main(String[] args) {
        BookLoversClub club = new BookLoversClub();

        System.out.println("===== 1. Registration (Builder + Chain of Responsibility + Singleton) =====");
        User aigerim = new User.Builder("Aigerim", 20)
                .city("Almaty")
                .contact(ContactChannel.TELEGRAM, "@aigerim_reads")
                .genres(Genre.FANTASY, Genre.CLASSIC, Genre.POETRY)
                .authors("Mikhail Bulgakov", "J.K. Rowling")
                .about("I love reading in coffee shops")
                .build();

        User arman = new User.Builder("Arman", 22)
                .city("Almaty")
                .contact(ContactChannel.EMAIL, "arman@mail.kz")
                .genres(Genre.FANTASY, Genre.SCI_FI, Genre.CLASSIC)
                .authors("Frank Herbert", "Mikhail Bulgakov")
                .build();

        User dana = new User.Builder("Dana", 19)
                .city("Astana")
                .contact(ContactChannel.SMS, "+7 701 000 00 00")
                .genres(Genre.DETECTIVE, Genre.ROMANCE)
                .authors("Agatha Christie")
                .build();

        User timur = new User.Builder("Timur", 24)
                .city("Almaty")
                .contact(ContactChannel.TELEGRAM, "@timur_books")
                .genres(Genre.NON_FICTION, Genre.SCI_FI, Genre.CLASSIC)
                .authors("Yuval Noah Harari", "Erich Maria Remarque")
                .build();

        User tooOld = new User.Builder("Victor", 45)
                .contact(ContactChannel.EMAIL, "victor@mail.kz")
                .genres(Genre.CLASSIC)
                .build();

        User noGenres = new User.Builder("Olzhas", 18)
                .contact(ContactChannel.EMAIL, "olzhas@mail.kz")
                .build();

        club.register(aigerim);
        club.register(arman);
        club.register(dana);
        club.register(timur);
        club.register(tooOld);
        club.register(noGenres);
        System.out.println("Total members: " + club.membersCount()
                + " (Singleton: same repository instance? "
                + (UserRepository.getInstance() == UserRepository.getInstance()) + ")");

        System.out.println("\n===== 2. Books read (Adapter + Flyweight) =====");
        club.addReadBook(aigerim, "978-5-17-080115-2", 5, LocalDate.of(2026, 1, 15));
        club.addReadBook(aigerim, "978-5-389-07435-4", 4, LocalDate.of(2026, 2, 3));
        club.addReadBook(aigerim, "978-5-389-01006-2", 5, LocalDate.of(2026, 3, 9));
        club.addReadBook(arman, "978-5-17-090630-7", 5, LocalDate.of(2026, 1, 28));
        club.addReadBook(arman, "978-5-17-080115-2", 4, LocalDate.of(2026, 2, 20));
        club.addReadBook(dana, "978-5-699-12014-7", 4, LocalDate.of(2026, 2, 11));
        club.addReadBook(timur, "978-5-04-116500-3", 5, LocalDate.of(2026, 3, 1));
        club.addReadBook(timur, "978-5-17-118366-0", 3, LocalDate.of(2026, 3, 22));
        club.addReadBook(timur, "000-0-00-000000-0", 3, LocalDate.of(2026, 3, 30));

        System.out.println("\n===== 3. Profiles (Decorator) =====");
        club.verify(aigerim);
        System.out.println(club.showProfile(aigerim));
        System.out.println(club.showProfile(timur));

        System.out.println("\n===== 4. Finding matches for Aigerim (Strategy) =====");
        List<MatchStrategy> strategies = Arrays.asList(
                new GenreMatchStrategy(),
                new AuthorMatchStrategy(),
                new AgeCityMatchStrategy(),
                new WeightedMatchStrategy()
                        .add(new GenreMatchStrategy(), 0.4)
                        .add(new AuthorMatchStrategy(), 0.4)
                        .add(new AgeCityMatchStrategy(), 0.2)
        );
        for (MatchStrategy s : strategies) {
            System.out.println("Strategy: " + s.name());
            List<Match> matches = club.findMatches(aigerim, s, 3);
            if (matches.isEmpty()) System.out.println("   no matches");
            matches.forEach(m -> System.out.println("   " + m));
        }

        System.out.println("\n===== 5. Introduction (Observer + Factory Method) =====");
        MatchStrategy best = strategies.get(strategies.size() - 1);
        List<Match> top = club.findMatches(aigerim, best, 1);
        if (!top.isEmpty()) {
            club.introduce(aigerim, top.get(0).user, best);
        }

        System.out.println("\n===== 6. Book club meetup (Observer) =====");
        club.announceMeetup("The Master and Margarita", "Bookworm Cafe, Almaty", "October 12, 6:00 PM");

        // ================= TEAM 3: new patterns =================

        System.out.println("\n===== 7. Flyweight: shared Book objects =====");
        // A fifth member and more reading, so that books are shared between users.
        User madina = new User.Builder("Madina", 21)
                .city("Almaty")
                .contact(ContactChannel.TELEGRAM, "@madina_pages")
                .genres(Genre.CLASSIC, Genre.FANTASY, Genre.ROMANCE)
                .authors("Antoine de Saint-Exupery")
                .build();
        club.register(madina);

        club.addReadBook(madina, "978-5-17-080115-2", 5, LocalDate.of(2026, 4, 2));
        club.addReadBook(madina, "978-5-389-01006-2", 5, LocalDate.of(2026, 4, 18));
        club.addReadBook(madina, "978-5-389-07435-4", 4, LocalDate.of(2026, 5, 5));
        club.addReadBook(dana, "978-5-17-080115-2", 5, LocalDate.of(2026, 4, 10));
        club.addReadBook(dana, "978-5-389-01006-2", 4, LocalDate.of(2026, 4, 25));
        club.addReadBook(arman, "978-5-17-118366-0", 4, LocalDate.of(2026, 5, 14));

        List<User> members = Arrays.asList(aigerim, arman, dana, timur, madina);

        Book viaLookup1 = club.findBook("978-5-17-080115-2").get();
        Book viaLookup2 = club.findBook("978-5-17-080115-2").get();
        Book aigerimsBook = aigerim.getReadingRecords().get(0).getBook();
        Book armansBook = arman.getReadingRecords().stream()
                .filter(r -> r.getBook().getIsbn().equals("978-5-17-080115-2"))
                .findFirst().get().getBook();
        System.out.println("Two direct lookups of the same ISBN  : lookup1 == lookup2 -> " + (viaLookup1 == viaLookup2));
        System.out.println("Aigerim's copy vs Arman's copy       : aigerimsBook == armansBook -> " + (aigerimsBook == armansBook));

        Map<Book, Integer> identityReaders = new IdentityHashMap<>();
        int totalReads = 0;
        for (User u : members) {
            for (ReadingRecord r : u.getReadingRecords()) {
                identityReaders.merge(r.getBook(), 1, Integer::sum);
                totalReads++;
            }
        }
        System.out.println("Reading records (user + rating + date): " + totalReads);
        System.out.println("Distinct Book instances in memory     : " + identityReaders.size());
        System.out.println("Book objects created by the factory   : " + club.bookObjectsCreated()
                + " (from " + club.bookLookups() + " requests)");
        identityReaders.entrySet().stream()
                .sorted((x, y) -> y.getValue() - x.getValue())
                .forEach(e -> System.out.println("   1 instance, " + e.getValue() + " reader(s): " + e.getKey().getTitle()));
        System.out.println("Extrinsic state example (Madina's journal):");
        madina.getReadingRecords().forEach(r -> System.out.println("   " + r));

        System.out.println("\n===== 8. Visitor: club statistics =====");
        ClubStatistics stats = club.collectStatistics();
        System.out.println("GenrePopularityVisitor:");
        stats.genres().ranking().forEach(e -> System.out.println("   " + e.getKey() + " - " + e.getValue() + " reads"));
        System.out.println("   Most popular genre: " + stats.genres().mostPopular().get());
        AgeStatisticsVisitor ages = stats.ages();
        System.out.println(String.format(Locale.US,
                "AgeStatisticsVisitor:\n   members=%d, average=%.1f, min=%d, max=%d",
                ages.count(), ages.average(), ages.min(), ages.max()));
        System.out.println("TopAuthorsVisitor (top 3):");
        stats.authors().top3().forEach(e -> System.out.println("   " + e.getKey() + " - " + e.getValue() + " reads"));

        System.out.println("\n===== 9. Template Method: the same report in three formats =====");
        System.out.println("--- TextReport (console) ---");
        System.out.println(club.generateReport(TextReport::new));
        System.out.println("--- CsvReport ---");
        System.out.println(club.generateReport(CsvReport::new));
        System.out.println("--- MarkdownReport ---");
        System.out.println(club.generateReport(MarkdownReport::new));
    }
}

// ======================================================================
// MODEL (Builder: User.Builder; Flyweight extrinsic state: ReadingRecord)
// ======================================================================

/**
 * A book. Immutable, so it can be safely shared between many users.
 *
 * FLYWEIGHT (Team 3): this class holds only the INTRINSIC state (isbn, title, author, genre).
 * The per-user data (rating, date read) lives in {@link ReadingRecord}.
 * Instances must be obtained through {@code catalog.BookFlyweightFactory}, never with {@code new}.
 *
 * VISITOR (Team 3): {@link #accept(ClubVisitor)} is the "door" through which visitors enter,
 * so statistics can be added without touching this class again.
 */
final class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final Genre genre;

    public Book(String isbn, String title, String author, Genre genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public String getIsbn()   { return isbn; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public Genre getGenre()   { return genre; }

    /** Visitor: double dispatch. */
    public void accept(ClubVisitor visitor) { visitor.visitBook(this); }

    @Override
    public String toString() { return "\"" + title + "\" by " + author + " (" + genre + ")"; }
}

enum ContactChannel { EMAIL, TELEGRAM, SMS }

enum Genre { FANTASY, SCI_FI, DETECTIVE, CLASSIC, ROMANCE, NON_FICTION, POETRY }

class Match {
    public final User user;
    public final double score;

    public Match(User user, double score) {
        this.user = user;
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s — %.0f%% compatible", user, score * 100);
    }
}

/**
 * FLYWEIGHT (Team 3) - the EXTRINSIC state.
 * Everything that is specific to one user reading one book (rating, date) is stored here,
 * while the shared {@link Book} is just referenced.
 */
final class ReadingRecord {
    private final Book book;
    private final int rating;          // 1..5, 0 = not rated
    private final LocalDate dateRead;

    public ReadingRecord(Book book, int rating, LocalDate dateRead) {
        if (rating < 0 || rating > 5) throw new IllegalArgumentException("Rating must be 0..5");
        this.book = book;
        this.rating = rating;
        this.dateRead = dateRead;
    }

    public Book getBook()          { return book; }
    public int getRating()         { return rating; }
    public LocalDate getDateRead() { return dateRead; }

    @Override
    public String toString() {
        return book.getTitle() + " - " + (rating == 0 ? "not rated" : rating + "/5") + ", read on " + dateRead;
    }
}

/**
 * BUILDER (existing): {@link Builder} creates a profile step by step.
 * VISITOR (Team 3): {@link #accept(ClubVisitor)}.
 */
class User {
    private final String id;
    private final String name;
    private final int age;
    private final String city;
    private final String contact;
    private final ContactChannel channel;
    private final String about;
    private final Set<Genre> favoriteGenres;
    private final Set<String> favoriteAuthors;
    private final List<ReadingRecord> readings;
    private boolean verified;

    private User(Builder b) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.name = b.name;
        this.age = b.age;
        this.city = b.city;
        this.contact = b.contact;
        this.channel = b.channel;
        this.about = b.about;
        this.favoriteGenres = b.favoriteGenres;
        this.favoriteAuthors = b.favoriteAuthors;
        this.readings = new ArrayList<>();
    }

    public String getId()                   { return id; }
    public String getName()                 { return name; }
    public int getAge()                     { return age; }
    public String getCity()                 { return city; }
    public String getContact()              { return contact; }
    public ContactChannel getChannel()      { return channel; }
    public String getAbout()                { return about; }
    public Set<Genre> getFavoriteGenres()   { return favoriteGenres; }
    public Set<String> getFavoriteAuthors() { return favoriteAuthors; }
    public boolean isVerified()             { return verified; }
    public void setVerified(boolean v)      { this.verified = v; }

    /** Per-user reading data (extrinsic state): rating + date + a reference to the shared Book. */
    public List<ReadingRecord> getReadingRecords() { return Collections.unmodifiableList(readings); }

    /** Convenience view kept for the existing code (matching strategies, profile decorators). */
    public List<Book> getReadBooks() {
        return readings.stream().map(ReadingRecord::getBook).collect(Collectors.toList());
    }

    public void addReading(ReadingRecord record) { readings.add(record); }

    /**
     * Visitor: the user is visited first, then every book they have read (one visit per read),
     * so a visitor can count "how many reads" without any change to the model.
     */
    public void accept(ClubVisitor visitor) {
        visitor.visitUser(this);
        for (ReadingRecord r : readings) {
            r.getBook().accept(visitor);
        }
    }

    @Override
    public String toString() { return name + " (" + age + ", " + city + ")"; }

    public static class Builder {
        private final String name;
        private final int age;
        private String city = "Not specified";
        private String contact = "";
        private ContactChannel channel = ContactChannel.EMAIL;
        private String about = "";
        private final Set<Genre> favoriteGenres = new HashSet<>();
        private final Set<String> favoriteAuthors = new HashSet<>();

        public Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Builder city(String city)                    { this.city = city; return this; }
        public Builder contact(ContactChannel ch, String c) { this.channel = ch; this.contact = c; return this; }
        public Builder about(String about)                  { this.about = about; return this; }
        public Builder genres(Genre... genres)              { favoriteGenres.addAll(Arrays.asList(genres)); return this; }
        public Builder authors(String... authors)           { favoriteAuthors.addAll(Arrays.asList(authors)); return this; }

        public User build() { return new User(this); }
    }
}

// ======================================================================
// 1. SINGLETON - UserRepository
// ======================================================================

/** SINGLETON (existing): one shared, thread-safe user storage. */
class UserRepository {
    private static volatile UserRepository instance;
    private final Map<String, User> users = new LinkedHashMap<>();

    private UserRepository() { }

    /** Thread-safe lazy initialization (double-checked locking). */
    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public void save(User user)               { users.put(user.getId(), user); }
    public Optional<User> findById(String id) { return Optional.ofNullable(users.get(id)); }
    public Collection<User> findAll()         { return Collections.unmodifiableCollection(users.values()); }
    public int count()                        { return users.size(); }
}

// ======================================================================
// 3. FACTORY METHOD + 5. OBSERVER - notifications and events
// ======================================================================

/** OBSERVER (existing): publishes club events to subscribers. */
class ClubEventBus {
    private final Map<String, List<ClubEventListener>> listeners = new HashMap<>();

    public void subscribe(String eventType, ClubEventListener l) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(l);
    }

    public void unsubscribe(String eventType, ClubEventListener l) {
        List<ClubEventListener> list = listeners.get(eventType);
        if (list != null) list.remove(l);
    }

    public void publish(String eventType, String message) {
        for (ClubEventListener l : listeners.getOrDefault(eventType, Collections.emptyList())) {
            l.onEvent(eventType, message);
        }
    }
}

interface ClubEventListener {
    void onEvent(String eventType, String message);
}

class EmailNotifier implements Notifier {
    public void send(User to, String message) {
        System.out.println("   [E-mail -> " + to.getContact() + "] " + message);
    }
}

interface Notifier {
    void send(User to, String message);
}

/** FACTORY METHOD (existing): creates the right notifier for the user's contact channel. */
class NotifierFactory {
    public static Notifier create(ContactChannel channel) {
        switch (channel) {
            case TELEGRAM: return new TelegramNotifier();
            case SMS:      return new SmsNotifier();
            case EMAIL:
            default:       return new EmailNotifier();
        }
    }
}

class SmsNotifier implements Notifier {
    public void send(User to, String message) {
        System.out.println("   [SMS -> " + to.getContact() + "] " + message);
    }
}

class TelegramNotifier implements Notifier {
    public void send(User to, String message) {
        System.out.println("   [Telegram -> " + to.getContact() + "] " + message);
    }
}

/** OBSERVER (existing): a user-subscriber receives events through their own contact channel. */
class UserSubscriber implements ClubEventListener {
    private final User user;
    private final Notifier notifier;

    public UserSubscriber(User user) {
        this.user = user;
        this.notifier = NotifierFactory.create(user.getChannel()); // Factory used inside Observer
    }

    @Override
    public void onEvent(String eventType, String message) {
        notifier.send(user, "[" + eventType + "] " + message);
    }
}

// ======================================================================
// 4. STRATEGY - matchmaking algorithms
// ======================================================================

/** Closeness in age and living in the same city. */
class AgeCityMatchStrategy implements MatchStrategy {
    public double score(User a, User b) {
        double ageScore = Math.max(0, 1 - Math.abs(a.getAge() - b.getAge()) / 8.0);
        double cityScore = a.getCity().equalsIgnoreCase(b.getCity()) ? 1.0 : 0.0;
        return 0.5 * ageScore + 0.5 * cityScore;
    }
    public String name() { return "By age and city"; }
}

/** Similarity by favorite authors and authors of books already read. */
class AuthorMatchStrategy implements MatchStrategy {
    public double score(User a, User b) {
        Set<String> authorsA = new HashSet<>(a.getFavoriteAuthors());
        Set<String> authorsB = new HashSet<>(b.getFavoriteAuthors());
        a.getReadBooks().forEach(book -> authorsA.add(book.getAuthor()));
        b.getReadBooks().forEach(book -> authorsB.add(book.getAuthor()));
        return GenreMatchStrategy.jaccard(authorsA, authorsB);
    }
    public String name() { return "By authors"; }
}

/** Similarity by favorite genres (Jaccard index). */
class GenreMatchStrategy implements MatchStrategy {
    public double score(User a, User b) {
        return jaccard(a.getFavoriteGenres(), b.getFavoriteGenres());
    }
    public String name() { return "By genres"; }

    static <T> double jaccard(Set<T> x, Set<T> y) {
        if (x.isEmpty() && y.isEmpty()) return 0;
        Set<T> intersection = new HashSet<>(x);
        intersection.retainAll(y);
        Set<T> union = new HashSet<>(x);
        union.addAll(y);
        return (double) intersection.size() / union.size();
    }
}

/** STRATEGY (existing): interchangeable matchmaking algorithms. */
interface MatchStrategy {
    /** Compatibility from 0.0 to 1.0 */
    double score(User a, User b);
    String name();
}

/** Combined strategy with weights. */
class WeightedMatchStrategy implements MatchStrategy {
    private final Map<MatchStrategy, Double> weights = new LinkedHashMap<>();

    public WeightedMatchStrategy add(MatchStrategy s, double weight) {
        weights.put(s, weight);
        return this;
    }

    public double score(User a, User b) {
        double total = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0) return 0;
        double sum = 0;
        for (Map.Entry<MatchStrategy, Double> e : weights.entrySet()) {
            sum += e.getKey().score(a, b) * e.getValue();
        }
        return sum / total;
    }
    public String name() { return "Weighted (combined)"; }
}

// ======================================================================
// 6. DECORATOR - profile view
// ======================================================================

class BasicProfileView implements ProfileView {
    private final User user;
    public BasicProfileView(User user) { this.user = user; }

    public String render() {
        return "👤 " + user.getName() + ", " + user.getAge() + " y.o., " + user.getCity()
                + "\n   Genres: " + user.getFavoriteGenres()
                + "\n   Authors: " + user.getFavoriteAuthors()
                + (user.getAbout().isEmpty() ? "" : "\n   About: " + user.getAbout());
    }
}

class LastBookDecorator extends ProfileDecorator {
    private final User user;
    public LastBookDecorator(ProfileView inner, User user) { super(inner); this.user = user; }

    public String render() {
        List<Book> books = user.getReadBooks();
        if (books.isEmpty()) return inner.render();
        return inner.render() + "\n   🔖 Last book: " + books.get(books.size() - 1);
    }
}

abstract class ProfileDecorator implements ProfileView {
    protected final ProfileView inner;
    protected ProfileDecorator(ProfileView inner) { this.inner = inner; }
}

/** DECORATOR (existing): component interface. */
interface ProfileView {
    String render();
}

class ReadingStatsDecorator extends ProfileDecorator {
    private final User user;
    public ReadingStatsDecorator(ProfileView inner, User user) { super(inner); this.user = user; }

    public String render() {
        int count = user.getReadBooks().size();
        String level = count >= 5 ? "Bookworm 🐛" : count >= 2 ? "Reader 📖" : "Newbie 🌱";
        return inner.render() + "\n   📚 Books read: " + count + " — " + level;
    }
}

class VerifiedBadgeDecorator extends ProfileDecorator {
    public VerifiedBadgeDecorator(ProfileView inner) { super(inner); }
    public String render() { return inner.render() + "\n   ✅ Verified profile"; }
}

// ======================================================================
// 8. CHAIN OF RESPONSIBILITY - registration validators
// ======================================================================

class AgeValidator extends RegistrationValidator {
    public static final int MIN_AGE = 14;
    public static final int MAX_AGE = 30;

    @Override
    protected String check(User u) {
        return (u.getAge() < MIN_AGE || u.getAge() > MAX_AGE)
                ? "The club is for young people aged " + MIN_AGE + " to " + MAX_AGE : null;
    }
}

class ContactValidator extends RegistrationValidator {
    @Override
    protected String check(User u) {
        String c = u.getContact();
        if (c == null || c.isEmpty()) return "Please provide a contact for notifications";
        if (u.getChannel() == ContactChannel.EMAIL && !c.contains("@"))
            return "Invalid e-mail address";
        return null;
    }
}

class GenresValidator extends RegistrationValidator {
    @Override
    protected String check(User u) {
        return u.getFavoriteGenres().isEmpty()
                ? "Please choose at least one favorite genre" : null;
    }
}

class NameValidator extends RegistrationValidator {
    @Override
    protected String check(User u) {
        return (u.getName() == null || u.getName().trim().length() < 2)
                ? "Name must be at least 2 characters long" : null;
    }
}

/** CHAIN OF RESPONSIBILITY (existing): sequential, extendable registration checks. */
abstract class RegistrationValidator {
    private RegistrationValidator next;

    public RegistrationValidator linkWith(RegistrationValidator next) {
        this.next = next;
        return next;
    }

    /** Returns null if the user is valid, otherwise an error message. */
    public String validate(User user) {
        String error = check(user);
        if (error != null) return error;
        return next == null ? null : next.validate(user);
    }

    protected abstract String check(User user);
}

// ======================================================================
// 7. ADAPTER + 10. FLYWEIGHT (Team 3) - book catalog
// ======================================================================

/** The interface our system expects (target of the Adapter). */
interface BookCatalog {
    Optional<Book> findByIsbn(String isbn);
}

/**
 * FLYWEIGHT FACTORY (Team 3).
 *
 * Why it is needed here: before this class, LegacyCatalogAdapter did "new Book(...)" on every lookup,
 * so "The Master and Margarita" read by 100 users meant 100 identical objects in memory.
 * The factory keeps a pool keyed by ISBN and always returns the same shared Book.
 *
 * Intrinsic state (shared, immutable): isbn, title, author, genre  -> {@link Book}
 * Extrinsic state (per user):          rating, date read           -> {@link model.ReadingRecord}
 *
 * Difference from Singleton: Singleton guarantees ONE instance of ONE class;
 * Flyweight guarantees one instance per distinct key (here: per ISBN) and there are many of them.
 */
class BookFlyweightFactory {
    private final Map<String, Book> pool = new HashMap<>();
    private int requests;

    /** Returns the shared Book for this ISBN, creating it only on the first request. */
    public synchronized Book getBook(String isbn, String title, String author, Genre genre) {
        requests++;
        return pool.computeIfAbsent(isbn, k -> new Book(isbn, title, author, genre));
    }

    /** How many distinct Book objects were really created. */
    public synchronized int createdCount() { return pool.size(); }

    /** How many times a Book was requested (including requests served from the cache). */
    public synchronized int requestsCount() { return requests; }
}

/**
 * ADAPTER (existing): converts the legacy "title|author|CODE" strings into {@link Book} objects.
 * Now it does not create books itself but asks the {@link BookFlyweightFactory} (FLYWEIGHT, Team 3).
 */
class LegacyCatalogAdapter implements BookCatalog {
    private final LegacyLibraryCatalog legacy;
    private final BookFlyweightFactory flyweights;

    public LegacyCatalogAdapter(LegacyLibraryCatalog legacy, BookFlyweightFactory flyweights) {
        this.legacy = legacy;
        this.flyweights = flyweights;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String raw = legacy.lookupRecord(isbn);
        if (raw == null) return Optional.empty();
        String[] parts = raw.split("\\|");
        return Optional.of(flyweights.getBook(isbn, parts[0], parts[1], convertGenre(parts[2])));
    }

    private Genre convertGenre(String code) {
        switch (code) {
            case "FNT": return Genre.FANTASY;
            case "SCF": return Genre.SCI_FI;
            case "DET": return Genre.DETECTIVE;
            case "ROM": return Genre.ROMANCE;
            case "NFC": return Genre.NON_FICTION;
            case "POE": return Genre.POETRY;
            case "CLS":
            default:    return Genre.CLASSIC;
        }
    }
}

/** Old city library catalog with an inconvenient API (cannot be modified) - the Adaptee. */
class LegacyLibraryCatalog {
    private final Map<String, String> records = new HashMap<>();

    public LegacyLibraryCatalog() {
        records.put("978-5-17-080115-2", "The Master and Margarita|Mikhail Bulgakov|CLS");
        records.put("978-5-389-07435-4", "Harry Potter and the Philosopher's Stone|J.K. Rowling|FNT");
        records.put("978-5-17-090630-7", "Dune|Frank Herbert|SCF");
        records.put("978-5-699-12014-7", "Murder on the Orient Express|Agatha Christie|DET");
        records.put("978-5-17-118366-0", "Three Comrades|Erich Maria Remarque|CLS");
        records.put("978-5-389-01006-2", "The Little Prince|Antoine de Saint-Exupery|CLS");
        records.put("978-5-04-116500-3", "Sapiens|Yuval Noah Harari|NFC");
    }

    /** Returns a string in the format "title|author|CODE", or null. */
    public String lookupRecord(String code) { return records.get(code); }
}

// ======================================================================
// 11. VISITOR (Team 3) - club statistics
// ======================================================================

/** Average, minimum and maximum age of the members. */
class AgeStatisticsVisitor implements ClubVisitor {
    private int count;
    private long sum;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    @Override
    public void visitUser(User u) {
        count++;
        sum += u.getAge();
        min = Math.min(min, u.getAge());
        max = Math.max(max, u.getAge());
    }

    @Override public void visitBook(Book b) { /* not interested in books */ }

    public int count()      { return count; }
    public double average() { return count == 0 ? 0 : (double) sum / count; }
    public int min()        { return count == 0 ? 0 : min; }
    public int max()        { return count == 0 ? 0 : max; }
}

/**
 * Bundles the three visitors after they have walked over the club.
 * It is the single data source for every report.
 */
final class ClubStatistics {
    private final GenrePopularityVisitor genres = new GenrePopularityVisitor();
    private final AgeStatisticsVisitor ages = new AgeStatisticsVisitor();
    private final TopAuthorsVisitor authors = new TopAuthorsVisitor();

    private ClubStatistics() { }

    /** Sends all three visitors through every user (and, through the users, every read book). */
    public static ClubStatistics collect(Collection<User> users) {
        ClubStatistics stats = new ClubStatistics();
        for (User u : users) {
            u.accept(stats.genres);
            u.accept(stats.ages);
            u.accept(stats.authors);
        }
        return stats;
    }

    public GenrePopularityVisitor genres() { return genres; }
    public AgeStatisticsVisitor ages()     { return ages; }
    public TopAuthorsVisitor authors()     { return authors; }
}

/**
 * VISITOR (Team 3): the operation interface.
 *
 * Why it is needed here: administrators want statistics, but adding "countGenres()",
 * "averageAge()" ... to User and Book would bloat the model with reporting code.
 * Each new statistic is a new visitor class; User and Book only need accept().
 *
 * Note: User.accept() calls visitUser() once and then visitBook() once per book read,
 * so visitBook() is called once per "read" (a book read by 3 users is visited 3 times).
 */
interface ClubVisitor {
    void visitUser(User u);
    void visitBook(Book b);
}

/** Counts reads per genre -> "most popular genres". */
class GenrePopularityVisitor implements ClubVisitor {
    private final Map<Genre, Integer> reads = new EnumMap<>(Genre.class);

    @Override public void visitUser(User u) { /* genres come from books */ }

    @Override public void visitBook(Book b) { reads.merge(b.getGenre(), 1, Integer::sum); }

    /** Genres sorted by number of reads (descending), ties by name. */
    public List<Map.Entry<Genre, Integer>> ranking() {
        List<Map.Entry<Genre, Integer>> list = new ArrayList<>(reads.entrySet());
        list.sort((a, b) -> {
            int c = Integer.compare(b.getValue(), a.getValue());
            return c != 0 ? c : a.getKey().name().compareTo(b.getKey().name());
        });
        return list;
    }

    public Optional<Genre> mostPopular() {
        List<Map.Entry<Genre, Integer>> r = ranking();
        return r.isEmpty() ? Optional.empty() : Optional.of(r.get(0).getKey());
    }
}

/** Top authors by number of reads (default: top 3). */
class TopAuthorsVisitor implements ClubVisitor {
    private final Map<String, Integer> reads = new HashMap<>();

    @Override public void visitUser(User u) { /* authors come from books */ }

    @Override public void visitBook(Book b) { reads.merge(b.getAuthor(), 1, Integer::sum); }

    public List<Map.Entry<String, Integer>> top(int n) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(reads.entrySet());
        list.sort((a, b) -> {
            int c = Integer.compare(b.getValue(), a.getValue());
            return c != 0 ? c : a.getKey().compareTo(b.getKey());
        });
        return list.stream().limit(n).collect(Collectors.toList());
    }

    public List<Map.Entry<String, Integer>> top3() { return top(3); }
}

// ======================================================================
// 12. TEMPLATE METHOD (Team 3) - reports
// ======================================================================

/**
 * TEMPLATE METHOD (Team 3).
 *
 * {@link #generate()} is the template: it is final and fixes the ORDER of the algorithm
 * (header -> body -> footer). Subclasses only fill in the steps, so a new format
 * (HTML, JSON ...) is a new subclass and no existing class is modified.
 * The data always comes from the visitors via {@link ClubStatistics}.
 *
 * Template Method vs Strategy: Template Method varies steps of ONE algorithm through
 * inheritance (compile time); Strategy swaps the WHOLE algorithm through composition (runtime).
 */
abstract class ClubReport {
    protected final ClubStatistics stats;

    protected ClubReport(ClubStatistics stats) { this.stats = stats; }

    /** The template method. Final - subclasses cannot change the order of steps. */
    public final String generate() {
        return header() + body() + footer();
    }

    /** Hook with a default implementation; subclasses may override. */
    protected String title() { return "Book Lovers Club - statistics"; }

    protected abstract String header();
    protected abstract String body();
    protected abstract String footer();
}

/** CSV report: one "section,key,value" row per fact. */
class CsvReport extends ClubReport {
    public CsvReport(ClubStatistics stats) { super(stats); }

    @Override
    protected String header() { return "section,key,value\n"; }

    @Override
    protected String body() {
        StringBuilder sb = new StringBuilder();
        AgeStatisticsVisitor a = stats.ages();
        sb.append("members,count,").append(a.count()).append('\n');
        sb.append(String.format(Locale.US, "age,average,%.1f\n", a.average()));
        sb.append("age,min,").append(a.min()).append('\n');
        sb.append("age,max,").append(a.max()).append('\n');
        for (Map.Entry<Genre, Integer> e : stats.genres().ranking()) {
            sb.append("genre,").append(e.getKey()).append(',').append(e.getValue()).append('\n');
        }
        for (Map.Entry<String, Integer> e : stats.authors().top3()) {
            sb.append("author,").append(escape(e.getKey())).append(',').append(e.getValue()).append('\n');
        }
        return sb.toString();
    }

    @Override
    protected String footer() { return "report,generated_by,BookLoversClub\n"; }

    private static String escape(String s) {
        return (s.contains(",") || s.contains("\"")) ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }
}

/** Markdown report with tables. */
class MarkdownReport extends ClubReport {
    public MarkdownReport(ClubStatistics stats) { super(stats); }

    @Override
    protected String header() { return "# " + title() + "\n\n"; }

    @Override
    protected String body() {
        StringBuilder sb = new StringBuilder();
        AgeStatisticsVisitor a = stats.ages();
        sb.append("**Members:** ").append(a.count()).append("  \n");
        sb.append(String.format(Locale.US, "**Age:** average %.1f, min %d, max %d\n\n", a.average(), a.min(), a.max()));
        sb.append("## Most popular genres\n\n| # | Genre | Reads |\n|---|-------|-------|\n");
        int i = 1;
        for (Map.Entry<Genre, Integer> e : stats.genres().ranking()) {
            sb.append("| ").append(i++).append(" | ").append(e.getKey()).append(" | ").append(e.getValue()).append(" |\n");
        }
        sb.append("\n## Top 3 authors\n\n| # | Author | Reads |\n|---|--------|-------|\n");
        i = 1;
        for (Map.Entry<String, Integer> e : stats.authors().top3()) {
            sb.append("| ").append(i++).append(" | ").append(e.getKey()).append(" | ").append(e.getValue()).append(" |\n");
        }
        return sb.toString();
    }

    @Override
    protected String footer() { return "\n---\n*Generated by BookLoversClub*\n"; }
}

/** Plain-text report for the console. */
class TextReport extends ClubReport {
    public TextReport(ClubStatistics stats) { super(stats); }

    @Override
    protected String header() {
        return "==============================================\n"
             + "  " + title() + "\n"
             + "==============================================\n";
    }

    @Override
    protected String body() {
        StringBuilder sb = new StringBuilder();
        AgeStatisticsVisitor a = stats.ages();
        sb.append("Members: ").append(a.count()).append('\n');
        sb.append(String.format(Locale.US, "Age: average %.1f, min %d, max %d\n", a.average(), a.min(), a.max()));
        sb.append("\nMost popular genres (reads):\n");
        int i = 1;
        for (Map.Entry<Genre, Integer> e : stats.genres().ranking()) {
            sb.append("  ").append(i++).append(". ").append(e.getKey()).append(" - ").append(e.getValue()).append('\n');
        }
        sb.append("\nTop 3 authors (reads):\n");
        i = 1;
        List<Map.Entry<String, Integer>> top = stats.authors().top3();
        for (Map.Entry<String, Integer> e : top) {
            sb.append("  ").append(i++).append(". ").append(e.getKey()).append(" - ").append(e.getValue()).append('\n');
        }
        return sb.toString();
    }

    @Override
    protected String footer() {
        return "----------------- end of report ---------------\n";
    }
}

// ======================================================================
// 9. FACADE - single entry point
// ======================================================================

/**
 * FACADE (existing): the single entry point for the whole system.
 * Team 3 additions (all reachable only through this class):
 *   findBook / addReadBook(rating, date) / bookObjectsCreated  -> Flyweight
 *   collectStatistics                                           -> Visitor
 *   generateReport                                              -> Template Method (+ Visitor data)
 */
class BookLoversClub {
    public static final String EVENT_NEW_MATCH = "NEW_MATCH";
    public static final String EVENT_MEETUP    = "CLUB_MEETUP";

    private final UserRepository repository = UserRepository.getInstance();
    private final ClubEventBus eventBus = new ClubEventBus();
    private final BookFlyweightFactory bookFactory = new BookFlyweightFactory();
    private final BookCatalog catalog = new LegacyCatalogAdapter(new LegacyLibraryCatalog(), bookFactory);
    private final RegistrationValidator validator;
    private final Map<String, UserSubscriber> subscribers = new HashMap<>();

    public BookLoversClub() {
        validator = new NameValidator();
        validator.linkWith(new AgeValidator())
                 .linkWith(new GenresValidator())
                 .linkWith(new ContactValidator());
    }

    /** Registration: validation + saving + subscribing to events. */
    public boolean register(User user) {
        String error = validator.validate(user);
        if (error != null) {
            System.out.println("❌ Registration of " + user.getName() + " rejected: " + error);
            return false;
        }
        repository.save(user);
        UserSubscriber sub = new UserSubscriber(user);
        subscribers.put(user.getId(), sub);
        eventBus.subscribe(EVENT_MEETUP, sub);
        System.out.println("✔ Registered: " + user);
        return true;
    }

    public void verify(User user) { user.setVerified(true); }

    // ------------------------- catalog (Adapter + Flyweight) -------------------------

    /** Looks a book up by ISBN. The same ISBN always returns the very same Book object. */
    public Optional<Book> findBook(String isbn) { return catalog.findByIsbn(isbn); }

    /** Adds a book the user has read (not rated, read today). */
    public void addReadBook(User user, String isbn) {
        addReadBook(user, isbn, 0, LocalDate.now());
    }

    /** Adds a book the user has read: shared Book (intrinsic) + ReadingRecord (extrinsic). */
    public void addReadBook(User user, String isbn, int rating, LocalDate dateRead) {
        Optional<Book> book = catalog.findByIsbn(isbn);
        if (book.isPresent()) {
            user.addReading(new ReadingRecord(book.get(), rating, dateRead));
            System.out.println("   " + user.getName() + " has read " + book.get()
                    + (rating > 0 ? " [" + rating + "/5]" : ""));
        } else {
            System.out.println("   Book with ISBN " + isbn + " was not found in the catalog");
        }
    }

    /** How many Book objects really exist (Flyweight pool size). */
    public int bookObjectsCreated() { return bookFactory.createdCount(); }

    /** How many times a Book was requested from the pool. */
    public int bookLookups() { return bookFactory.requestsCount(); }

    // ------------------------- analytics (Visitor) -------------------------

    /** Walks all three visitors over the whole club. */
    public ClubStatistics collectStatistics() { return ClubStatistics.collect(repository.findAll()); }

    // ------------------------- reports (Template Method) -------------------------

    /**
     * Builds a report in the requested format, e.g. {@code club.generateReport(CsvReport::new)}.
     * A new format is just a new ClubReport subclass - this method does not change.
     */
    public String generateReport(Function<ClubStatistics, ClubReport> format) {
        return format.apply(collectStatistics()).generate();
    }

    // ------------------------- matching / events / profiles -------------------------

    /** Finds the best matches using the chosen strategy. */
    public List<Match> findMatches(User user, MatchStrategy strategy, int limit) {
        return repository.findAll().stream()
                .filter(other -> !other.getId().equals(user.getId()))
                .map(other -> new Match(other, strategy.score(user, other)))
                .filter(m -> m.score > 0)
                .sorted((m1, m2) -> Double.compare(m2.score, m1.score))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /** Introduction: both users get a notification. */
    public void introduce(User a, User b, MatchStrategy strategy) {
        double score = strategy.score(a, b);
        String pct = String.format(Locale.US, "%.0f%%", score * 100);
        Set<Genre> common = commonGenres(a, b);
        subscribers.get(a.getId()).onEvent(EVENT_NEW_MATCH,
                "Meet " + b.getName() + " (" + pct + " match). Shared genres: " + common);
        subscribers.get(b.getId()).onEvent(EVENT_NEW_MATCH,
                "Meet " + a.getName() + " (" + pct + " match). Shared genres: " + common);
    }

    /** Book club meetup - broadcast to all subscribers. */
    public void announceMeetup(String bookTitle, String place, String date) {
        eventBus.publish(EVENT_MEETUP, "Discussing \"" + bookTitle + "\" — " + place + ", " + date);
    }

    /** Renders a profile wrapped in decorators. */
    public String showProfile(User user) {
        ProfileView view = new BasicProfileView(user);
        view = new ReadingStatsDecorator(view, user);
        view = new LastBookDecorator(view, user);
        if (user.isVerified()) view = new VerifiedBadgeDecorator(view);
        return view.render();
    }

    public int membersCount() { return repository.count(); }

    private Set<Genre> commonGenres(User a, User b) {
        Set<Genre> s = new HashSet<>(a.getFavoriteGenres());
        s.retainAll(b.getFavoriteGenres());
        return s;
    }
}
