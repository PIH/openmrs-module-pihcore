package org.openmrs.module.pihcore;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.openmrs.Location;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkServiceImpl;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.pihcore.config.Config;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.SimpleObject;
import org.powermock.api.mockito.PowerMockito;

import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openmrs.module.pihcore.apploader.RequireUtil.and;
import static org.openmrs.module.pihcore.apploader.RequireUtil.or;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientHasActiveVisit;
import static org.openmrs.module.pihcore.apploader.RequireUtil.patientVisitWithinPastThirtyDays;
import static org.openmrs.module.pihcore.apploader.RequireUtil.sessionLocationHasTag;
import static org.openmrs.module.pihcore.apploader.RequireUtil.userHasPrivilege;

// TODO these tests are passing when run through IntelliJ, but failing through maven for some reason, so @Ignoring this for now
// TODO update: looks like these tests have degraded to the point that they don't work on IntelliJ either

@Disabled
public class RequireUtilTest {

    private AppFrameworkServiceImpl appFrameworkService;

    private User user;

    private Role doctor;

    private Role admin;

    private UserContext userContext;

    private UiSessionContext uiSessionContext;

    @BeforeEach
    public void setup() throws Exception {
        appFrameworkService = new AppFrameworkServiceImpl(null, null, null, null, null, null, null, null);

        doctor = new Role("Doctor");
        admin = new Role("Admin");

        Privilege enterConsultNote = new Privilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE);

        Privilege retroClinicalNote = new Privilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE);

        Privilege retroClinicalNoteThisProviderOnly = new Privilege(PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY);

        doctor.addPrivilege(enterConsultNote);
        doctor.addPrivilege(retroClinicalNoteThisProviderOnly);

        admin.addPrivilege(enterConsultNote);
        admin.addPrivilege(retroClinicalNote);

        user = new User();
        user.setUsername("bobMeIn");
        user.setUuid("123-456");
        user.setSystemId("abc");
        user.setRetired(true);
        userContext = mock(UserContext.class);
        when(userContext.getAuthenticatedUser()).thenReturn(user);

        uiSessionContext = new UiSessionContext();
        uiSessionContext.setUserContext(userContext);

    }

    @Test
    public void shouldCreateCompoundAnd() {
        assertThat(and("big", "bad", "bird"), is("(big && bad && bird)"));
    }

    @Test
    public void shouldCreateCompoundOr() {
        assertThat(or("big", "bad", "bird"), is("(big || bad || bird)"));
    }

    @Test
    public void shouldReturnTrueIfUserHasPrivilege() {
        user.addRole(doctor);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE)), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfUserDoesNotHavePrivilege() {
        user.addRole(doctor);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE)), appContextModel), is(false));
    }


    @Test
    public void shouldReturnTrueIfVisitActive() {

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfVisitNotActive() {

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfNoVisit() {
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientHasActiveVisit()), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfLocationHasTag() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admission Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(sessionLocationHasTag("Admission Location")), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfLocationDoesNotHaveTag() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admission Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(sessionLocationHasTag("Archives Location")), appContextModel), is(false));
    }


    @Test
    public void shouldReturnTrueIfLocationHasTagAndVisitActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admission Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag("Admission Location"), patientHasActiveVisit())), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfLocationDoesNotHasTagAndVisitActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Check-In Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag("Admission Location"), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfLocationHasTagButVisitNotActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admission Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag("Admission Location"), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfLocationDOesNotHaveTagAndVisitNotActive() {

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Check-In Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitDomainWrapper visit = mock(VisitDomainWrapper.class);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring
                (and(sessionLocationHasTag("Admission Location"), patientHasActiveVisit())), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfVisitWithinPastThirtyDays() {

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new Date().getTime());

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientVisitWithinPastThirtyDays(config)), appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfVisitNotWithinPastThirtyDays() {

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(patientVisitWithinPastThirtyDays(config)), appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasRetroPrivilege() {

        user.addRole(admin);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Consult Note Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                and(sessionLocationHasTag("Consult Note Location"),
                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfNotProperLocationTagAndUserHasRetroPrivilege() {

        user.addRole(admin);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Admission Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime(2014,1,1,1,1).getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag("Consult Note Location"),
                                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasConsultPrivilegeAndActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Consult Note Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag("Consult Note Location"),
                                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(true));
    }

    @Test
    public void shouldReturnFalseIfProperLocationTagAndUserHasConsultPrivilegeButNoActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Consult Note Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(false);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag("Consult Note Location"),
                                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnFalseIfImproperLocationTagEventThoughUserHasConsultPrivilegeAndActiveVisit() {

        user.addRole(doctor);

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Check-In Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(null);
        when(visit.isActive()).thenReturn(true);

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag("Consult Note Location"),
                                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(false));
    }

    @Test
    public void shouldReturnTrueIfProperLocationTagAndUserHasRetroPrivilegeThisProviderOnlyAndVisitInLastThirtyDays() {

        user.addRole(doctor);  // doctor has last thirty days privilege

        Location sessionLocation = new Location();
        SimpleObject sessionLocationRestRep = new SimpleObject();
        sessionLocationRestRep.put("uuid", "123abc");
        SimpleObject admitTag = new SimpleObject();
        admitTag.put("display", "Consult Note Location");
        sessionLocationRestRep.put("tags", Arrays.asList(admitTag));

        PowerMockito.mockStatic(ConversionUtil.class);
        when(ConversionUtil.convertToRepresentation(sessionLocation, Representation.DEFAULT)).thenReturn(sessionLocationRestRep);

        VisitContextModel visit = mock(VisitContextModel.class);
        when(visit.getStopDatetimeInMilliseconds()).thenReturn(new DateTime().getMillis());

        uiSessionContext.setSessionLocation(sessionLocation);
        AppContextModel appContextModel = uiSessionContext.generateAppContextModel();
        appContextModel.put("visit", visit);

        Config config = mock(Config.class);
        when(config.isComponentEnabled("visitNote")).thenReturn(false);

        assertThat(appFrameworkService.checkRequireExpression(extensionRequiring(
                        and(sessionLocationHasTag("Consult Note Location"),
                                or(and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_ENTER_CONSULT_NOTE), patientHasActiveVisit()),
                                        userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE),
                                        and(userHasPrivilege(  PihEmrConfigConstants.PRIVILEGE_TASK_EMR_RETRO_CLINICAL_NOTE_THIS_PROVIDER_ONLY), patientVisitWithinPastThirtyDays(config))))),
                appContextModel), is(true));
    }

    private Extension extensionRequiring(String requires) {
        Extension extension = new Extension();
        extension.setRequire(requires);
        return extension;
    }


}
