package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LabResultsFragmentControllerTest {

    @SuppressWarnings("unchecked")
    private Set<String> invokeGetLabCategoriesListRecursive(LabResultsFragmentController controller,
                                                            String labCategoriesSet,
                                                            ConceptService conceptService,
                                                            Set<String> visited) throws Exception {
        Method m = LabResultsFragmentController.class.getDeclaredMethod(
                "getLabCategoriesListRecursive", String.class, ConceptService.class, Set.class);
        m.setAccessible(true);
        return (Set<String>) m.invoke(controller, labCategoriesSet, conceptService, visited);
    }

    @Test
    public void getLabCategoriesListRecursive_shouldReturnEmptySet_whenInputBlank() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        Set<String> result = invokeGetLabCategoriesListRecursive(controller, "   ", conceptService, new HashSet<String>());

        assertEquals(Collections.<String>emptySet(), result);
        verifyNoInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesListRecursive_shouldReturnEmptySet_whenConceptNotFound() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        when(conceptService.getConceptByUuid("set-uuid")).thenReturn(null);

        Set<String> result = invokeGetLabCategoriesListRecursive(controller, "set-uuid", conceptService, new HashSet<String>());

        assertEquals(Collections.<String>emptySet(), result);
        verify(conceptService, times(1)).getConceptByUuid("set-uuid");
        verifyNoMoreInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesListRecursive_shouldCollectAllUuids_inTree() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        Concept root = mock(Concept.class);
        Concept child1 = mock(Concept.class);
        Concept child2 = mock(Concept.class);

        when(root.getUuid()).thenReturn("root");
        when(child1.getUuid()).thenReturn("child1");
        when(child2.getUuid()).thenReturn("child2");

        when(root.getSetMembers()).thenReturn(Arrays.asList(child1, child2));
        when(child1.getSetMembers()).thenReturn(Collections.emptyList());
        when(child2.getSetMembers()).thenReturn(Collections.emptyList());

        when(conceptService.getConceptByUuid("root")).thenReturn(root);
        when(conceptService.getConceptByUuid("child1")).thenReturn(child1);
        when(conceptService.getConceptByUuid("child2")).thenReturn(child2);

        Set<String> result = invokeGetLabCategoriesListRecursive(controller, "root", conceptService, new HashSet<String>());

        Set<String> expected = new HashSet<String>(Arrays.asList("root", "child1", "child2"));
        assertEquals(expected, result);

        verify(conceptService, times(1)).getConceptByUuid("root");
        verify(conceptService, times(1)).getConceptByUuid("child1");
        verify(conceptService, times(1)).getConceptByUuid("child2");
        verifyNoMoreInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesListRecursive_shouldHandleCycles_withoutInfiniteRecursion() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        Concept a = mock(Concept.class);
        Concept b = mock(Concept.class);

        when(a.getUuid()).thenReturn("A");
        when(b.getUuid()).thenReturn("B");

        // Cycle: A -> B -> A
        when(a.getSetMembers()).thenReturn(Collections.singletonList(b));
        when(b.getSetMembers()).thenReturn(Collections.singletonList(a));

        when(conceptService.getConceptByUuid("A")).thenReturn(a);
        when(conceptService.getConceptByUuid("B")).thenReturn(b);

        Set<String> result = invokeGetLabCategoriesListRecursive(controller, "A", conceptService, new HashSet<String>());

        Set<String> expected = new HashSet<String>(Arrays.asList("A", "B"));
        assertEquals(expected, result);

        // Each concept UUID should be looked up once due to the visited-set early return
        verify(conceptService, times(1)).getConceptByUuid("A");
        verify(conceptService, times(1)).getConceptByUuid("B");
        verifyNoMoreInteractions(conceptService);
    }
}