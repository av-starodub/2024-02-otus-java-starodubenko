package ru.otus.atmemulator.container.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.atmemulator.testutil.TestUtil.NOTE_COUNT_MAP;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_100;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_1000;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_500;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_5000;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atmemulator.container.NoteBox;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.container.money.Money;
import ru.otus.atmemulator.container.moneybox.MoneyBox;
import ru.otus.atmemulator.denomination.Note;

class AbstractNoteContainerBuilderTest {

    private static final Map<Note, Integer> EXPECTED_NOTES = NOTE_COUNT_MAP;

    private static final int EXPECTED_BALANCE = 6600;

    @Test
    @DisplayName("Check that builder creates the Money instance correctly")
    void buildMoneyTest() {
        var money = Money.builder()
                .put(Note.of(RUB_5000.getNominal()), 1)
                .put(Note.of(RUB_1000.getNominal()), 1)
                .put(Note.of(RUB_500.getNominal()), 1)
                .put(Note.of(RUB_100.getNominal()), 1)
                .build();
        assertThat(money).isInstanceOf(NoteContainer.class).isNotInstanceOf(NoteBox.class);

        var actualNotes = money.getNumberOfNotes();
        assertThat(actualNotes).containsExactlyInAnyOrderEntriesOf(EXPECTED_NOTES);

        var actualBalance = money.getAmount();
        assertThat(actualBalance).isEqualTo(EXPECTED_BALANCE);
    }

    @Test
    @DisplayName("Check that builder creates the MoneyBox instance correctly")
    void buildMoneyBoxTest() {
        var moneyBox = MoneyBox.builder(1).putAll(EXPECTED_NOTES).build();

        var actualNotes = moneyBox.getNumberOfNotes();
        var actualBalance = moneyBox.getAmount();

        assertThat(moneyBox).isInstanceOf(NoteContainer.class).isInstanceOf(NoteBox.class);
        assertThat(actualBalance).isEqualTo(EXPECTED_BALANCE);
        assertThat(actualNotes).containsExactlyInAnyOrderEntriesOf(EXPECTED_NOTES);
    }
}
