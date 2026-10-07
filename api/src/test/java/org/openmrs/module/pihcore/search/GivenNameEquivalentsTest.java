package org.openmrs.module.pihcore.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GivenNameEquivalentsTest {

    @Test
    public void shouldMatchCommonShortNames() {
        assertTrue(GivenNameEquivalents.areEquivalent("David", "Dave"));
        assertTrue(GivenNameEquivalents.areEquivalent("Dave", "David"));
        assertTrue(GivenNameEquivalents.areEquivalent("William", "Bill"));
        assertTrue(GivenNameEquivalents.areEquivalent("Robert", "Bob"));
        assertTrue(GivenNameEquivalents.areEquivalent("Elizabeth", "Betty"));
    }

    @Test
    public void shouldIgnoreCaseAndAccents() {
        assertTrue(GivenNameEquivalents.areEquivalent("JEAN", "john"));
        assertTrue(GivenNameEquivalents.areEquivalent("André", "andrew"));
        assertTrue(GivenNameEquivalents.areEquivalent("Étienne", "Steve"));
    }

    @Test
    public void shouldMatchIdenticalNames() {
        assertTrue(GivenNameEquivalents.areEquivalent("Zorro", "zorro"));
    }

    @Test
    public void shouldNotMatchUnrelatedOrBlankNames() {
        assertFalse(GivenNameEquivalents.areEquivalent("David", "Robert"));
        assertFalse(GivenNameEquivalents.areEquivalent("Bill", "Bob"));
        assertFalse(GivenNameEquivalents.areEquivalent(null, "Dave"));
        assertFalse(GivenNameEquivalents.areEquivalent("Dave", " "));
    }

    @Test
    public void shouldNotChainAcrossGroups() {
        // "dave" is only a variant of david, not of everything david's other variants relate to
        assertFalse(GivenNameEquivalents.areEquivalent("Dave", "Bill"));
    }

    @Test
    public void variantsShouldIncludeTheNameItself() {
        assertTrue(GivenNameEquivalents.getVariants("Zorro").contains("zorro"));
        assertTrue(GivenNameEquivalents.getVariants("David").contains("david"));
        assertTrue(GivenNameEquivalents.getVariants("David").contains("dave"));
    }
}
