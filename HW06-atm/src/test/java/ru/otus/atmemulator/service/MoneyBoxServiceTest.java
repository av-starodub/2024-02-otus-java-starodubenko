package ru.otus.atmemulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_100;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_1000;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_500;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_5000;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atmemulator.exeption.NotEnoughBanknotesException;
import ru.otus.atmemulator.exeption.NotFreeSpaceException;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;
import ru.otus.atmemulator.testutil.TestUtil;

class MoneyBoxServiceTest {

    private MoneyBoxService moneyBoxService;

    @BeforeEach
    void setUp() {
        var moneyBox = TestUtil.createEmptyBox(4);
        moneyBoxService = new MoneyBoxService(moneyBox);
    }

    @Test
    @DisplayName("Check that putMoney() adds money correctly")
    void checkPositiveScriptOfPutMoney() {
        var money = TestUtil.createMoney(Map.of(RUB_5000, 1, RUB_1000, 1, RUB_500, 1, RUB_100, 1));
        moneyBoxService.putMoney(money);

        var expectedBalance = money.getAmount();
        var actualBalance = moneyBoxService.checkBalance();

        assertThat(actualBalance).isEqualTo(expectedBalance);
    }

    @Test
    @DisplayName("When not enough free space should throw NotFreeSpaceException")
    void checkThrowWhenNotFreeSpace() {
        var money = TestUtil.createMoney(Map.of(RUB_500, 5));

        var thrown = catchThrowable(() -> moneyBoxService.putMoney(money));
        assertThat(thrown).isInstanceOf(NotFreeSpaceException.class).hasMessageContaining("Not enough free space");

        var actualBalance = moneyBoxService.checkBalance();
        assertThat(actualBalance).isZero();
    }

    @Test
    @DisplayName("Check that getMoney() extracts money correctly")
    void checkPositiveScriptOfGetMoney() {
        int requiredSum = 6000;
        var money = TestUtil.createMoney(Map.of(RUB_5000, 1, RUB_500, 4, RUB_100, 1));
        moneyBoxService.putMoney(money);
        var extractedMoney = moneyBoxService.getMoney(requiredSum, NoteDispenseStrategy.MINIMUM_NOTES);

        var actualBanknotes = extractedMoney.getNumberOfNotes();
        var expectedBanknotes = TestUtil.transformToNoteCountMap(Map.of(RUB_5000, 1, RUB_500, 2));
        assertThat(actualBanknotes).containsExactlyInAnyOrderEntriesOf(expectedBanknotes);

        var actualBalance = moneyBoxService.checkBalance();
        var expectedBalance = 1100;
        assertThat(actualBalance).isEqualTo(expectedBalance);
    }

    @Test
    @DisplayName("When not enough banknotes should throw NotEnoughBanknotesException")
    void checkThrowWhenNotEnoughBanknotes() {
        var money = TestUtil.createMoney(Map.of(RUB_5000, 1, RUB_1000, 1, RUB_100, 1));

        moneyBoxService.putMoney(money);

        var requiredSum = 500;

        var thrown = catchThrowable(() -> moneyBoxService.getMoney(requiredSum, NoteDispenseStrategy.MINIMUM_NOTES));
        assertThat(thrown).isInstanceOf(NotEnoughBanknotesException.class).hasMessageContaining("Not enough banknotes");
    }
}
