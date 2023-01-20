package com.redhat.training.bookpublishing.strategy;

import org.apache.camel.language.xpath.XPath;

public class DynamicRoutingStrategy {
    public String compute(
            @XPath(value="/book/bookinfo/productname/text()") String type
    ) {
        // TODO: Create a strategy for the printing pipeline
        return "";
    }
}
