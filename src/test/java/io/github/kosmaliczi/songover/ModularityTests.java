package io.github.kosmaliczi.songover;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(SongoverApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
