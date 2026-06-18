# Hints — Problem 46: Concurrent Trie

## Level 1 — Nudge

A trie node has a `Map<Character, TrieNode>` for children and a boolean `isEnd`. For thread safety, use `ConcurrentHashMap` for children and `volatile boolean isEnd`. `ConcurrentHashMap.computeIfAbsent` atomically creates a child node only if it does not exist.

---

## Level 2 — Direction

**`TrieNode`**:
```java
class TrieNode {
    final ConcurrentHashMap<Character, TrieNode> children = new ConcurrentHashMap<>();
    volatile boolean isEnd = false;
}
```

**`insert(word)`**:
```java
TrieNode current = root;
for (char c : word.toCharArray()) {
    current = current.children.computeIfAbsent(c, k -> new TrieNode());
}
if (!current.isEnd) { current.isEnd = true; wordCount.incrementAndGet(); }
```

**`delete(word)`**: navigate to the end, set `isEnd = false`, decrement `wordCount`. Physical node removal (cleanup) is optional — the test only checks logical deletion.

**`wordsWithPrefix(prefix)`**: navigate to prefix end node, then DFS the subtree collecting all paths where `isEnd == true`.

---

## Level 3 — Almost there

| Symptom | Likely cause |
|---|---|
| `wordCountNoDuplicates` fails | Incrementing count on every insert, not checking `isEnd` before setting it |
| `delete` test: `car` gone after deleting `cat` | Physically removing nodes instead of just clearing `isEnd` |
| Concurrent insert count wrong | Using `++wordCount` (non-atomic) instead of `AtomicInteger.incrementAndGet()` |
