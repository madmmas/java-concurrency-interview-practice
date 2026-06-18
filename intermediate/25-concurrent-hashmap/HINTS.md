# Hints — Problem 25: ConcurrentHashMap

## Level 1 — Nudge

`ConcurrentHashMap`'\''s atomic operations (`merge`, `compute`, `computeIfAbsent`, `replace`) are the key to thread-safe updates without external locking. The callback you pass is called atomically with respect to other operations on the same key.

---

## Level 2 — Direction

**`WordFrequencyCounter.addDocument`**:
```java
String[] words = text.split("\\s+");
for (String word : words) {
    if (!word.isEmpty()) map.merge(word, 1, Integer::sum);
}
```
`merge(key, 1, Integer::sum)` atomically: if key absent → put 1; if present → apply `Integer::sum` to old and new value.

**`ConcurrentInventory.reserveItem`** — CAS loop:
```java
boolean reserved = false;
while (!reserved) {
    Integer current = map.get(item);
    if (current == null || current < quantity) return false;
    reserved = map.replace(item, current, current - quantity);
}
return true;
```
`replace(key, expectedValue, newValue)` is atomic compare-and-set on the map entry.

**`removeItem`**: use `compute` — if the new quantity would be < 0, throw; if == 0, return null (removes the key); otherwise return the new quantity.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| Word count off under concurrency | Using `getOrDefault` + `put` — not atomic; two threads both read 0 and both write 1 |
| `reserveItem` over-reserves | CAS loop missing — using `get` then `put` instead of `replace(key, old, new)` |
| `removeItem` leaves key with value 0 | Returning 0 from `compute` instead of `null` (null removes the key) |
