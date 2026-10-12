package com.bluemoon.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

    @Test
    void docCauHinhKhongNem() {
        assertNotNull(Database.docCauHinh());
    }
}
