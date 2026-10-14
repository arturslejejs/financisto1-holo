package tw.tib.financisto.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import tw.tib.financisto.model.Currency;

public class IntentTransactionProcessorTest {

    private static Currency currency(long id, String code) {
        Currency c = new Currency();
        c.id = id;
        c.name = code;
        c.title = code;
        return c;
    }

    @Test
    public void findCurrencyByCode_ignoresCaseAndSpaces() {
        Currency eur = currency(1, "EUR");
        Currency gbp = currency(2, "GBP");
        List<Currency> all = Arrays.asList(eur, gbp);

        assertSame(gbp, IntentTransactionProcessor.findCurrencyByCode(all, "GBP"));
        assertSame(gbp, IntentTransactionProcessor.findCurrencyByCode(all, " gbp "));
        assertNull(IntentTransactionProcessor.findCurrencyByCode(all, "USD"));
        assertNull(IntentTransactionProcessor.findCurrencyByCode(all, null));
    }

    @Test
    public void withSignOf_followsTheAccountAmount() {
        BigDecimal expense = new BigDecimal("-25.40");
        BigDecimal income = new BigDecimal("25.40");

        assertEquals(new BigDecimal("-21.70"),
                IntentTransactionProcessor.withSignOf(new BigDecimal("21.70"), expense));
        assertEquals(new BigDecimal("-21.70"),
                IntentTransactionProcessor.withSignOf(new BigDecimal("-21.70"), expense));
        assertEquals(new BigDecimal("21.70"),
                IntentTransactionProcessor.withSignOf(new BigDecimal("-21.70"), income));
    }

    @Test
    public void withSignOf_keepsItsOwnSignWithoutAnAccountAmount() {
        assertEquals(new BigDecimal("-21.70"),
                IntentTransactionProcessor.withSignOf(new BigDecimal("-21.70"), null));
        assertEquals(new BigDecimal("21.70"),
                IntentTransactionProcessor.withSignOf(new BigDecimal("21.70"), BigDecimal.ZERO));
    }

    @Test
    public void parseAmount_acceptsTheAmountFormats() {
        assertEquals(new BigDecimal("-16.25"), IntentTransactionProcessor.parseAmount("-16.25"));
        assertEquals(0, new BigDecimal("4500").compareTo(IntentTransactionProcessor.parseAmount("4,500")));
        assertEquals(0, new BigDecimal("1.23").compareTo(IntentTransactionProcessor.parseAmount("1.23")));
    }

    @Test
    public void parseAmount_rejectsEmptyZeroAndGarbage() {
        assertNull(IntentTransactionProcessor.parseAmount(""));
        assertNull(IntentTransactionProcessor.parseAmount("0"));
        assertNull(IntentTransactionProcessor.parseAmount("0.00"));
        assertNull(IntentTransactionProcessor.parseAmount("abc"));
    }

    @Test
    public void toCents() {
        assertEquals(-1625L, IntentTransactionProcessor.toCents(new BigDecimal("-16.25")));
        assertEquals(3500L, IntentTransactionProcessor.toCents(new BigDecimal("35")));
    }
}
