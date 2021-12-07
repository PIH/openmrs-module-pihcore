package org.openmrs.module.pihcore.page.controller;

import org.openmrs.module.appframework.feature.FeatureToggleProperties;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class TogglesPageController {
	public void controller(PageModel model,
	    @SpringBean("featureToggles") FeatureToggleProperties featureToggleProperties) {
		model.addAttribute("featureToggles", featureToggleProperties.getToggleMap());
	}
}
