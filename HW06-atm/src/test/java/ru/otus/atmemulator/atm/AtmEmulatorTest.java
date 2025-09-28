package ru.otus.atmemulator.atm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_100;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_1000;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_500;
import static ru.otus.atmemulator.testutil.TestUtil.NominalTypeTest.RUB_5000;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.atmemulator.container.NoteContainer;
import ru.otus.atmemulator.exeption.*;
import ru.otus.atmemulator.service.MoneyBoxService;
import ru.otus.atmemulator.service.NoteBoxService;
import ru.otus.atmemulator.strategy.NoteDispenseStrategy;
import ru.otus.atmemulator.testutil.TestUtil;

class AtmEmulatorTest {

    private static final int REQUIRED_SUM = 100;

    private static final int MIN_NOMINAL = RUB_100.getNominal();

    private static final int INVALID_REQUIRED_SUM = MIN_NOMINAL + 1;

    private static final String EXPECTED_ERROR_MESSAGE = "Error. Operation can be performed";

    private AtmEmulator atmEmulator;

    private NoteBoxService noteBoxService;

    @BeforeEach
    void setUp() {
        noteBoxService = Mockito.mock(MoneyBoxService.class);
        atmEmulator = new AtmEmulator(noteBoxService);
    }

    @Test
    @DisplayName("When not enough free space should throw AtmException")
    void checkThrowWhenNotFreeSpace() {
        when(noteBoxService.putMoney(any(NoteContainer.class)))
                .thenThrow(new NotFreeSpaceException("Not enough free space"));
        var nominalCount = Map.of(RUB_5000, 1, RUB_1000, 1, RUB_500, 5, RUB_100, 1);
        var money = TestUtil.createMoney(nominalCount);

        var thrown = catchThrowable(() -> atmEmulator.putMoney(money));

        assertThat(thrown)
                .isInstanceOf(AtmException.class)
                .hasRootCauseInstanceOf(NotFreeSpaceException.class)
                .hasMessageContaining(EXPECTED_ERROR_MESSAGE)
                .hasMessageContaining("Not enough free space");
    }

    @Test
    @DisplayName("When required sum less than minNominal should throw AtmException")
    void checkThrowWhenRequiredSumLessThanMinNominal() {
        when(noteBoxService.getMoney(anyInt(), any(NoteDispenseStrategy.class)))
                .thenThrow(new NotValidSumException("The amount must be a multiple"));

        var thrown =
                catchThrowable(() -> atmEmulator.getMoney(INVALID_REQUIRED_SUM, NoteDispenseStrategy.MINIMUM_NOTES));

        assertThat(thrown)
                .isInstanceOf(AtmException.class)
                .hasRootCauseInstanceOf(NotValidSumException.class)
                .hasMessageContaining(EXPECTED_ERROR_MESSAGE)
                .hasMessageContaining("The amount must be a multiple");
    }

    @Test
    @DisplayName("When sum more than balance should throw AtmException")
    void checkThrowWhenNotEnoughMoney() {
        when(noteBoxService.getMoney(anyInt(), any(NoteDispenseStrategy.class)))
                .thenThrow(new NotEnoughMoneyException("Not enough money"));

        Throwable thrown = catchThrowable(() -> atmEmulator.getMoney(REQUIRED_SUM, NoteDispenseStrategy.MINIMUM_NOTES));

        assertThat(thrown)
                .isInstanceOf(AtmException.class)
                .hasRootCauseInstanceOf(NotEnoughMoneyException.class)
                .hasMessageContaining(EXPECTED_ERROR_MESSAGE)
                .hasMessageContaining("Not enough money");
    }

    @Test
    @DisplayName("When not enough banknotes should throw AtmException")
    void checkThrowWhenNotEnoughBanknotes() {
        when(noteBoxService.getMoney(anyInt(), any(NoteDispenseStrategy.class)))
                .thenThrow(new NotEnoughBanknotesException("Not enough banknotes"));

        var thrown = catchThrowable(() -> atmEmulator.getMoney(REQUIRED_SUM, NoteDispenseStrategy.MINIMUM_NOTES));

        assertThat(thrown)
                .isInstanceOf(AtmException.class)
                .hasRootCauseInstanceOf(NotEnoughBanknotesException.class)
                .hasMessageContaining(EXPECTED_ERROR_MESSAGE)
                .hasMessageContaining("Not enough banknotes");
    }
}
