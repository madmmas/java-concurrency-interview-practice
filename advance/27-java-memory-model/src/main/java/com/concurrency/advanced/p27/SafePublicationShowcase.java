package com.concurrency.advanced.p27;

/**
 * Problem 27 – JMM: Safe Publication Showcase
 *
 * Demonstrates four publication patterns and which JMM rules make each
 * (or don't make each) safe.
 */
public class SafePublicationShowcase {

    // ── Shared holder types ───────────────────────────────────────────────────

    /** Mutable holder — field visibility depends entirely on publication mechanism. */
    public static class MutableHolder {
        public int value;
        public MutableHolder(int value) { this.value = value; }
    }

    /** Immutable holder — final field freeze guarantees visibility after construction. */
    public static class ImmutableHolder {
        public final int value;
        public ImmutableHolder(int value) { this.value = value; }
    }

    // ── Publication fields ────────────────────────────────────────────────────

    private MutableHolder  unsafeRef;                     // plain field — UNSAFE
    private MutableHolder  synchronizedRef;               // protected by this monitor
    private volatile MutableHolder  volatileRef;          // volatile write HB read
    private ImmutableHolder finalFieldHolder;             // final-field freeze guarantee

    // ── Unsafe publication (educational — do NOT use) ─────────────────────────

    /**
     * Publishes via a plain non-volatile, non-synchronized field.
     * A reader thread has NO happens-before guarantee — may see null or a
     * partially constructed object.
     * Returns the reference so single-threaded tests can inspect it.
     */
    public MutableHolder publishUnsafe(int value) {
        // TODO: unsafeRef = new MutableHolder(value); return unsafeRef;
        throw new UnsupportedOperationException("Implement publishUnsafe()");
    }

    // ── Synchronized publication ──────────────────────────────────────────────

    /**
     * Publishes via a synchronized setter.
     * The monitor unlock in the writer HB the monitor lock in the reader.
     */
    public synchronized void publishViaSynchronized(int value) {
        // TODO: synchronizedRef = new MutableHolder(value);
        throw new UnsupportedOperationException("Implement publishViaSynchronized()");
    }

    public synchronized MutableHolder getSynchronized() {
        return synchronizedRef;
    }

    // ── Volatile publication ──────────────────────────────────────────────────

    /**
     * Publishes via a volatile field.
     * The volatile write HB every subsequent volatile read of volatileRef.
     */
    public void publishViaVolatile(int value) {
        // TODO: volatileRef = new MutableHolder(value);
        throw new UnsupportedOperationException("Implement publishViaVolatile()");
    }

    public MutableHolder getVolatile() {
        return volatileRef;
    }

    // ── Final-field publication ───────────────────────────────────────────────

    /**
     * Stores an ImmutableHolder (all-final fields) in a plain field.
     *
     * Safe because:
     *  1. The constructor completes before the reference escapes.
     *  2. thread.start() establishes HB — all writes before start() are visible
     *     to the new thread.
     *  3. After thread.join(), the joining thread sees all writes made before
     *     the joined thread terminated.
     *
     * The final-field freeze ensures ImmutableHolder.value is visible to any
     * thread that obtains the reference through a proper HB chain.
     */
    public void publishViaFinalField(int value) {
        // TODO: finalFieldHolder = new ImmutableHolder(value);
        throw new UnsupportedOperationException("Implement publishViaFinalField()");
    }

    public ImmutableHolder getFinalFieldHolder() {
        return finalFieldHolder;
    }
}
