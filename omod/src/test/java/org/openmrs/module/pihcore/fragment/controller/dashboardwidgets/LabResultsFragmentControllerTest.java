package org.openmrs.module.pihcore.fragment.controller.dashboardwidgets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.parameter.ConceptSearchCriteria;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LabResultsFragmentControllerTest {

    @SuppressWarnings("unchecked")
    private Set<String> invokeGetLabCategoriesList(LabResultsFragmentController controller,
                                                            String labCategoriesSet,
                                                            ConceptService conceptService) throws Exception {
        Method m = LabResultsFragmentController.class.getDeclaredMethod(
                "getLabCategoriesList", String.class, ConceptService.class);
        m.setAccessible(true);
        return (Set<String>) m.invoke(controller, labCategoriesSet, conceptService);
    }

    @Test
    public void getLabCategoriesList_shouldReturnEmptySet_whenInputBlank() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        Set<String> result = invokeGetLabCategoriesList(controller, "   ", conceptService);

        assertEquals(Collections.<String>emptySet(), result);
        verifyNoInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesList_shouldReturnEmptySet_whenConceptNotFound() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        when(conceptService.getConcepts(any(ConceptSearchCriteria.class))).thenReturn(Collections.emptyList());

        Set<String> result = invokeGetLabCategoriesList(controller, "set-uuid", conceptService);

        assertEquals(Collections.<String>emptySet(), result);
        verify(conceptService, times(1)).getConcepts(any(ConceptSearchCriteria.class));
        verifyNoMoreInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesList_shouldCollectAllUuids_inTree() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        /*
         * root
         * |- child1
         *   |- grandChild1
         *   |- grandChild2
         * |- child2
         */
        Concept root = mock(Concept.class);
        Concept child1 = mock(Concept.class);
        Concept child2 = mock(Concept.class);
        Concept grandChild1 = mock(Concept.class);
        Concept grandChild2 = mock(Concept.class);

        when(root.getUuid()).thenReturn("root");
        when(child1.getUuid()).thenReturn("child1");
        when(child2.getUuid()).thenReturn("child2");
        when(grandChild1.getUuid()).thenReturn("grandChild1");
        when(grandChild2.getUuid()).thenReturn("grandChild2");

        when(root.getSetMembers()).thenReturn(Arrays.asList(child1, child2));
        when(child1.getSetMembers()).thenReturn(Arrays.asList(grandChild1, grandChild2));
        when(child2.getSetMembers()).thenReturn(Collections.emptyList());
        when(grandChild1.getSetMembers()).thenReturn(Collections.emptyList());
        when(grandChild2.getSetMembers()).thenReturn(Collections.emptyList());

        Map<String, Concept> conceptMap = new HashMap<>();
        conceptMap.put("root", root);
        conceptMap.put("child1", child1);
        conceptMap.put("child2", child2);
        conceptMap.put("grandChild1", grandChild1);
        conceptMap.put("grandChild2", grandChild2);

        when(conceptService.getConcepts(any(ConceptSearchCriteria.class))).thenAnswer(inv -> {
            ConceptSearchCriteria criteria = inv.getArgument(0);
            ArrayList<Concept> result = new ArrayList<>();
            for(String uuid : criteria.getUuids()) {
                Concept c = conceptMap.get(uuid);
                if(c != null) {
                    result.add(c);
                }
            }
            return result;
        });

        Set<String> result = invokeGetLabCategoriesList(controller, "root", conceptService);

        Set<String> expected = new HashSet<>(Arrays.asList("root", "child1", "child2", "grandChild1", "grandChild2"));
        assertEquals(expected, result);

        // BFS makes 3 batched calls: {root}, {child1,child2}, {grandChild1,grandChild2}
        verify(conceptService, times(3)).getConcepts(any(ConceptSearchCriteria.class));
        verifyNoMoreInteractions(conceptService);
    }

    @Test
    public void getLabCategoriesList_shouldHandleCycles_withoutInfiniteLoop() throws Exception {
        LabResultsFragmentController controller = new LabResultsFragmentController();
        ConceptService conceptService = mock(ConceptService.class);

        Concept a = mock(Concept.class);
        Concept b = mock(Concept.class);

        when(a.getUuid()).thenReturn("A");
        when(b.getUuid()).thenReturn("B");

        // Cycle: A -> B -> A
        when(a.getSetMembers()).thenReturn(Collections.singletonList(b));
        when(b.getSetMembers()).thenReturn(Collections.singletonList(a));

        Map<String, Concept> conceptMap = new HashMap<>();
        conceptMap.put("A", a);
        conceptMap.put("B", b);

        when(conceptService.getConcepts(any(ConceptSearchCriteria.class))).thenAnswer(inv -> {
            ConceptSearchCriteria criteria = inv.getArgument(0);
            return criteria.getUuids().stream()
                    .filter(conceptMap::containsKey)
                    .map(conceptMap::get)
                    .collect(Collectors.toList());
        });

        Set<String> result = invokeGetLabCategoriesList(controller, "A", conceptService);

        Set<String> expected = new HashSet<>(Arrays.asList("A", "B"));
        assertEquals(expected, result);

        // BFS makes 2 batched calls: {A}, then {B} — A is already visited so loop ends
        verify(conceptService, times(2)).getConcepts(any(ConceptSearchCriteria.class));
        verifyNoMoreInteractions(conceptService);
    }
}
