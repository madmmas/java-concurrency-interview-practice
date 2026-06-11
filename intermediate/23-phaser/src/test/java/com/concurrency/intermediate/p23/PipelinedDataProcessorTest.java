package com.concurrency.intermediate.p23;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class PipelinedDataProcessorTest {

    @Test
    void singleWorkerBasicProcessing() throws InterruptedException {
        PipelinedDataProcessor proc = new PipelinedDataProcessor(1);
        List<String> result = proc.process(List.of("  hello  ", "  world  "));
        assertEquals(List.of("HELLO ✓", "WORLD ✓"), result,
                "Single worker must apply trim → toUpperCase → append ✓");
    }

    @Test
    void multipleWorkersProduceSameResultAsSequential() throws InterruptedException {
        List<String> data = List.of("  foo  ", " bar ", "  baz  ", "  qux  ");
        List<String> expected = List.of("FOO ✓", "BAR ✓", "BAZ ✓", "QUX ✓");

        PipelinedDataProcessor proc = new PipelinedDataProcessor(2);
        List<String> result = proc.process(data);
        assertEquals(expected, result);
    }

    @Test
    void orderIsPreserved() throws InterruptedException {
        List<String> data = new java.util.ArrayList<>();
        for (int i = 0; i < 20; i++) data.add(" item" + i + " ");
        PipelinedDataProcessor proc = new PipelinedDataProcessor(4);
        List<String> result = proc.process(data);

        assertEquals(20, result.size());
        for (int i = 0; i < 20; i++) {
            assertTrue(result.get(i).startsWith("ITEM" + i),
                    "Element at index " + i + " must be processed ITEM" + i + ", got: " + result.get(i));
        }
    }

    @Test
    void allThreeStagesAreApplied() throws InterruptedException {
        PipelinedDataProcessor proc = new PipelinedDataProcessor(2);
        List<String> result = proc.process(List.of("  test  "));
        String item = result.get(0);
        assertFalse(item.startsWith(" "),  "Stage 1 (trim) must remove leading spaces");
        assertEquals(item, item.toUpperCase().strip() + " ✓" , "Stage 2 (uppercase) and stage 3 (✓) must be applied");
    }

    @Test
    void worksWithMoreWorkersThanItems() throws InterruptedException {
        PipelinedDataProcessor proc = new PipelinedDataProcessor(5);
        List<String> result = proc.process(List.of(" a ", " b "));
        assertEquals(List.of("A ✓", "B ✓"), result);
    }
}
