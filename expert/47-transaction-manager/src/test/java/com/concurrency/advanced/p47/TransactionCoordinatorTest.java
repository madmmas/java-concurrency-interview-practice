package com.concurrency.advanced.p47;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@Timeout(value=10, unit=TimeUnit.SECONDS)
class TransactionCoordinatorTest {
    private TransactionCoordinator coord;
    @BeforeEach void setUp()                    { coord=new TransactionCoordinator(); }
    @AfterEach  void tearDown() throws Exception{ coord.shutdown(); }
    @Test void allReliableCommit() throws Exception {
        coord.registerParticipant(new TransactionCoordinator.ReliableParticipant("p1"));
        coord.registerParticipant(new TransactionCoordinator.ReliableParticipant("p2"));
        assertEquals(TransactionCoordinator.TransactionResult.COMMITTED, coord.execute("tx1"));
        assertEquals(1,coord.getCommittedCount()); assertEquals(0,coord.getAbortedCount());
    }
    @Test void oneAbortAbortsAll() throws Exception {
        coord.registerParticipant(new TransactionCoordinator.ReliableParticipant("p1"));
        coord.registerParticipant(new TransactionCoordinator.UnreliableParticipant("p2",1.0));
        assertEquals(TransactionCoordinator.TransactionResult.ABORTED, coord.execute("tx2"));
        assertEquals(0,coord.getCommittedCount()); assertEquals(1,coord.getAbortedCount());
    }
    @Test void commitCallsCommitOnAll() throws Exception {
        var p1=new TransactionCoordinator.ReliableParticipant("p1");
        var p2=new TransactionCoordinator.ReliableParticipant("p2");
        coord.registerParticipant(p1); coord.registerParticipant(p2);
        coord.execute("tx3");
        assertTrue(p1.getCommitted().contains("tx3")); assertTrue(p2.getCommitted().contains("tx3"));
    }
    @Test void abortCallsRollbackOnAll() throws Exception {
        var p1=new TransactionCoordinator.ReliableParticipant("p1");
        coord.registerParticipant(p1);
        coord.registerParticipant(new TransactionCoordinator.UnreliableParticipant("p2",1.0));
        coord.execute("tx4");
        assertTrue(p1.getRolledBack().contains("tx4")); assertTrue(p1.getCommitted().isEmpty());
    }
    @Test void multipleTransactionsCount() throws Exception {
        coord.registerParticipant(new TransactionCoordinator.ReliableParticipant("p"));
        coord.execute("a"); coord.execute("b"); coord.execute("c");
        assertEquals(3,coord.getCommittedCount());
    }
    @Test void noParticipantsCommitsVacuously() throws Exception {
        assertEquals(TransactionCoordinator.TransactionResult.COMMITTED, coord.execute("empty"));
    }
}
