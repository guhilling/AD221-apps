package com.redhat.training.payslipvalidator.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.language.xpath.XPathBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AmountProcessor implements Processor {

	@Override
	public void process(Exchange exchange) {
		NodeList result = XPathBuilder.xpath(
				"/payslip/payslipItems/payslipItem/payslipItemQty/text()"
		).evaluate(exchange, NodeList.class);

		Stream<Node> nodeStream = IntStream.range(0, result.getLength())
				.mapToObj(result::item);

		exchange.getIn().setHeader(
				"totalPayslipUnits",
				nodeStream.mapToInt(node -> Integer.parseInt(node.getNodeValue())).sum()
		);
	}
}
