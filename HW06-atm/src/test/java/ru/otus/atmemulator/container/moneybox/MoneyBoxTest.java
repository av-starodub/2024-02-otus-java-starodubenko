package ru.otus.atmemulator.container.moneybox;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.atmemulator.testutil.TestUtil.NOTE_COUNT_MAP;
import static ru.otus.atmemulator.testutil.TestUtil.TEST_NOMINAL_COUNT_MAP;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atmemulator.testutil.TestUtil;

class MoneyBoxTest {

    private static final int EXPECTED_BALANCE = 6600;

    @Test
    @DisplayName("Check that put() creates the Money correctly")
    void putNotesTest() {
        var moneyBox = TestUtil.createEmptyBox(1);
        var actualBalance = moneyBox.getAmount();

        assertThat(actualBalance).isZero();

        var money = TestUtil.createMoney(TEST_NOMINAL_COUNT_MAP);
        var actualBalanceAfterAdd = moneyBox.putNotes(money);
        var actualNotes = moneyBox.getNumberOfNotes();
        var expectedNotes = TestUtil.transformToNoteCountMap(TEST_NOMINAL_COUNT_MAP);

        assertThat(actualBalanceAfterAdd).isEqualTo(EXPECTED_BALANCE);
        assertThat(actualNotes).containsExactlyInAnyOrderEntriesOf(expectedNotes);
    }

    @Test
    @DisplayName("Check that get() extracts banknotes correctly")
    void extractNotesTest() {
        var moneyBox = MoneyBox.builder(1).putAll(NOTE_COUNT_MAP).build();

        var actualBalance = moneyBox.getAmount();
        assertThat(actualBalance).isEqualTo(EXPECTED_BALANCE);

        var request = NOTE_COUNT_MAP;
        var actualExtractedMoney = moneyBox.extractNotes(request);
        assertThat(actualExtractedMoney.getNumberOfNotes()).containsExactlyInAnyOrderEntriesOf(request);

        var actualMoneyBoxBalanceAfterExtracting = moneyBox.getAmount();
        assertThat(actualMoneyBoxBalanceAfterExtracting).isZero();

        var actualNumberOfNotesAfterExtracting = moneyBox.getNumberOfNotes();

        var expectedNumberOfNotesAfterExtracting = TestUtil.createEmptyBox(1).getNumberOfNotes();
        assertThat(actualNumberOfNotesAfterExtracting)
                .containsExactlyInAnyOrderEntriesOf(expectedNumberOfNotesAfterExtracting);
    }
}
