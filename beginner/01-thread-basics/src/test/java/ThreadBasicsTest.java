import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("01 — Thread Basics")
public class ThreadBasicsTest {

    private ThreadBasics tb;

    @BeforeEach
    void setUp() { tb = new ThreadBasics(); }

    @Test
    @DisplayName("createAndStartThread: thread should be alive after start")
    void testCreateAndStartThread() throws InterruptedException {
        AtomicBoolean ran = new AtomicBoolean(false);
        Thread t = tb.createAndStartThread(() -> {
            ran.set(true);
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        assertNotNull(t, "Returned thread must not be null");
        assertTrue(t.isAlive() || ran.get(), "Thread should have started");
        t.join(1000);
        assertTrue(ran.get(), "Runnable must have executed");
    }

    @Test
    @DisplayName("extendThread: should not be started, should print name when run")
    void testExtendThread() throws InterruptedException {
        Thread t = tb.extendThread("Alice");
        assertNotNull(t, "Thread must not be null");
        assertEquals(Thread.State.NEW, t.getState(), "Thread should not be started yet");
        t.start();
        t.join(1000);
        assertEquals(Thread.State.TERMINATED, t.getState());
    }

    @Test
    @DisplayName("getThreadInfo: format must match name=X,state=Y,daemon=Z")
    void testGetThreadInfo() throws InterruptedException {
        Thread t = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "test-worker");
        t.setDaemon(false);
        t.start();
        Thread.sleep(50);
        String info = tb.getThreadInfo(t);
        assertTrue(info.contains("name=test-worker"), "Info must contain thread name: " + info);
        assertTrue(info.contains("daemon=false"), "Info must contain daemon status: " + info);
        assertTrue(info.contains("state="), "Info must contain state: " + info);
        t.join();
    }

    @Test
    @DisplayName("countActiveThreads: returns positive count")
    void testCountActiveThreads() {
        int count = tb.countActiveThreads();
        assertTrue(count >= 1, "Should have at least 1 active thread (the main thread): " + count);
    }
}
