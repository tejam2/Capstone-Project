package com.rabeet.spring.Vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

@Tag("div")
public class VideoEmbed extends Component {

    private Element iframe = new Element("iframe");
    public static final String VIDEO_URL = "https://streamable.com/s/6lp6g/plndxu?autoplay=1";

    public VideoEmbed() {

        iframe
                .setAttribute("src", VIDEO_URL)
                .setAttribute("frameborder", "0")
                .setAttribute("width", "100%")
                .setAttribute("height", "100%")
                .setAttribute("allowfullscreen", true)
                .setAttribute("style", "width:100%;height:100%;position:absolute;left:0px;top:0px;overflow:hidden;");

        getElement()
                .setAttribute("style", "width:100%;height:0px;position:relative;padding-bottom:62.500%;")
                .appendChild(iframe);
    }

}