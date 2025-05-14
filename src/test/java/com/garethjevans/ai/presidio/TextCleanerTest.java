package com.garethjevans.ai.presidio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class TextCleanerTest {

    @Autowired
    private TextCleaner textCleaner;

    @Test
    public void testCleanText() {
        assertThat(textCleaner.clean("My name is Bond, James Bond")).isEqualTo("My name is Bond, <PERSON>");
        assertThat(textCleaner.clean("hello world, my name is Jane Doe. My number is: 034453334")).isEqualTo("hello world, my name is <PERSON>. My number is: <PHONE_NUMBER>");
        assertThat(textCleaner.clean("The year is 2003")).isEqualTo("The year is 2003");
    }

    @Test
    public void testCleanText_usDrivingLicense() {
        assertThatThrownBy(() -> textCleaner.clean("John Smith drivers license is AC432223")).isInstanceOf(BannedElementException.class);
    }

    @Test
    public void testCleanText_creditCardNumber() {
        assertThatThrownBy(() -> textCleaner.clean("please buy some stuff for me, my credit card number is 5555555555554444")).isInstanceOf(BannedElementException.class);
    }
}
