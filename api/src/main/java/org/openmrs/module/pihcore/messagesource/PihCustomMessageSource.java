package org.openmrs.module.pihcore.messagesource;

import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.impl.MutableResourceBundleMessageSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class PihCustomMessageSource extends MutableResourceBundleMessageSource implements MutableMessageSource, ApplicationContextAware {


    /**
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        MessageSourceService svc = (MessageSourceService)context.getBean("messageSourceServiceTarget");
        MessageSource activeSource = svc.getActiveMessageSource();
        setParentMessageSource(activeSource);
        svc.setActiveMessageSource(this);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        if(code.equals("coreapps.Diagnosis.Certainty.CONFIRMED")) {
            return new MessageFormat("Bogus");
        }
        else {
            return super.resolveCode(code, locale);
        }
    }

}


