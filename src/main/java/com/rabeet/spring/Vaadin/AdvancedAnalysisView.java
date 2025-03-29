package com.rabeet.spring.Vaadin;

import com.cloudinary.Cloudinary;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "advanced", layout = RouterLayout.class)
@PageTitle("GC Insights App")
public class AdvancedAnalysisView extends VerticalLayout {

    private AdvancedInputForm inputForm;
    private Cloudinary cloudinary;

    public AdvancedAnalysisView(Cloudinary cloudinary) {

        this.cloudinary = cloudinary;

        inputForm = new AdvancedInputForm(this.cloudinary);
        setPadding(true);
        add(inputForm);
        setSizeFull();

    }

}

