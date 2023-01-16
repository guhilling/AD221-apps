package com.redhat.training.payslipvalidator.processor;

import static org.apache.camel.language.xpath.XPathBuilder.xpath;

import java.util.stream.IntStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.language.xpath.XPathBuilder;
import org.w3c.dom.NodeList;

public class AmountProcessor implements Processor {

	@Override
	public void process(Exchange exchange) {
		XPathBuilder xpath = xpath("/payslip/payslipItems/payslipItem/payslipItemQty/text()");
		NodeList result = xpath.evaluate(exchange, NodeList.class);

		int sum = IntStream.range(0, result.getLength()).mapToObj(result::item).mapToInt(node -> Integer.parseInt(node.getNodeValue())).sum();
		exchange.getIn().setHeader("totalPayslipUnits", sum);
	}
}
