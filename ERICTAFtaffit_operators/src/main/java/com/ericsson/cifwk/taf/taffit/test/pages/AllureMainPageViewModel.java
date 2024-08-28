package com.ericsson.cifwk.taf.taffit.test.pages;

import com.ericsson.cifwk.taf.ui.core.UiComponent;
import com.ericsson.cifwk.taf.ui.core.UiComponentMapping;
import com.ericsson.cifwk.taf.ui.sdk.GenericViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class AllureMainPageViewModel extends GenericViewModel {

    @UiComponentMapping(".b-vert__title.ng-binding")
    private List<UiComponent> tabs;

    @UiComponentMapping(".btn.btn-default.ng-binding.ng-scope.btn-status-passed")
    private UiComponent passButton;

    @UiComponentMapping(".line-ellipsis.ng-binding")
    private List<UiComponent> testSuites;

    @UiComponentMapping(".line-ellipsis.ng-binding")
    private List<UiComponent> testCases;

    @UiComponentMapping(".ng-binding.ng-scope")
    private List<UiComponent> logs;

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureMainPageViewModel.class);

    public void selectTab(String select) {
        for (UiComponent component: tabs){
            if(component.getText().equalsIgnoreCase(select)){
                component.click();
                LOGGER.info("Clicked '{}'.", component.getText());
            }
        }
    }

    public void selectTestSuite(String testSuite){
        passButton.click();
        for (UiComponent component: testSuites){
            if(component.getText().equalsIgnoreCase(testSuite)){
                component.click();
                LOGGER.info("Clicked '{}'.", component.getText());
            }
        }
    }

    public void selectTestCase(String testCase){
        for (UiComponent component: testCases){
            if(component.getText().contains(testCase)){
                component.click();
                LOGGER.info("Clicked '{}'.", component.getText());
            }
        }
    }

    public Boolean verifyLogExists(){
        boolean visible = false;
        for (UiComponent component: logs) {
            if (component.getText().contains("log.txt")) {
                LOGGER.info("Selected '{}'.", component.getText());
                visible = true;
            }
        }
        return visible;
    }
}
