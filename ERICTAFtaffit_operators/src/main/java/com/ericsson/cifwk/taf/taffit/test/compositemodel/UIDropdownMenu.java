package com.ericsson.cifwk.taf.taffit.test.compositemodel;

import com.ericsson.cifwk.taf.ui.core.AbstractUiComponent;
import com.ericsson.cifwk.taf.ui.core.SelectorType;
import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UIDropdownMenu extends AbstractUiComponent {

    private static final Logger logger = LoggerFactory.getLogger(UIDropdownMenu.class);

    @UiComponentMapping(selector = ".ebComponentList-item", selectorType = SelectorType.CSS)
    private List<Link> dropDownOptions;

    @UiComponentMapping(selector = ".ebComponentList-item", selectorType = SelectorType.CSS)
    private List<UiComponent> dropDownCheckboxOptions;

    public void clickOptionByName(String optionName) {
        logger.info("Attempting to click '{}'.", optionName);
        boolean found = false;
        for (Link link : dropDownOptions) {
            if (link.getText().equalsIgnoreCase(optionName)) {
                link.click();
                logger.info("Clicked '{}'.", optionName);
                found = true;
                break;
            }
        }
        if(!found) {
            logger.error("'{}' not in drop down options", optionName);
        }
    }

    public void clickDropboxOptionsByName(String... optionNames) {
        for(String optionName : optionNames) {
            logger.info("Attempting to click '{}'.", optionName);
            boolean found = false;
            for (UiComponent component : dropDownCheckboxOptions) {
                if (component.getText().equalsIgnoreCase(optionName)) {
                    component.click();
                    logger.info("Clicked '{}'.", optionName);
                    found = true;
                    break;
                }
            }
            if(!found) {
                logger.error("'{}' not in drop down options", optionName);
            }
        }
    }
}