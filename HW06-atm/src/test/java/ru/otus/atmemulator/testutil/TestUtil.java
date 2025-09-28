package ru.otus.atmemulator.testutil;

import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_100;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_1000;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_500;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_5000;

import java.util.Map;
import java.util.stream.Collectors;
import ru.otus.atmemulator.container.NoteBox;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.container.money.Money;
import ru.otus.atmemulator.container.moneybox.MoneyBox;
import ru.otus.atmemulator.denomination.Note;

public final class TestUtil {

    public static final Map<NominalTypeTest, Integer> TEST_NOMINAL_COUNT_MAP =
            Map.of(RUB_5000, 1, RUB_1000, 1, RUB_500, 1, RUB_100, 1);

    public static final Map<Note, Integer> NOTE_COUNT_MAP = transformToNoteCountMap(TEST_NOMINAL_COUNT_MAP);

    private TestUtil() {}

    public enum NominalTypeTest {
        RUB_5000(5000),
        RUB_1000(1000),
        RUB_500(500),
        RUB_100(100);

        public int getNominal() {
            return nominal;
        }

        private final int nominal;

        NominalTypeTest(int nominal) {
            this.nominal = nominal;
        }
    }

    public static NoteBox createEmptyBox(int ceilSize) {
        return MoneyBox.builder(ceilSize)
                .put(Note.of(RUB_5000.nominal), 0)
                .put(Note.of(RUB_1000.nominal), 0)
                .put(Note.of(RUB_500.nominal), 0)
                .put(Note.of(RUB_100.nominal), 0)
                .build();
    }

    public static Map<Note, Integer> transformToNoteCountMap(Map<NominalTypeTest, Integer> notes) {
        return notes.entrySet().stream()
                .collect(Collectors.toMap(e -> Note.of(e.getKey().nominal), Map.Entry::getValue));
    }

    public static NoteContainer createMoney(Map<NominalTypeTest, Integer> notes) {
        return Money.builder().putAll(transformToNoteCountMap(notes)).build();
    }
}
