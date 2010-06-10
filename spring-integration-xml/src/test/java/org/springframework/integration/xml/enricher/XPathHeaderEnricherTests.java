/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.xml.enricher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageHeaders;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.xml.xpath.XPathEvaluationType;
import org.springframework.xml.xpath.XPathExpression;
import org.springframework.xml.xpath.XPathExpressionFactory;

/**
 * @author Jonas Partner
 * @since 2.0
 */
public class XPathHeaderEnricherTests {

	@Test
	public void simpleStringEvaluation() {
		Map<String, XPathExpression> expressionMap = new HashMap<String, XPathExpression>();
		expressionMap.put("one", XPathExpressionFactory.createXPathExpression("/root/elementOne"));
		expressionMap.put("two", XPathExpressionFactory.createXPathExpression("/root/elementTwo"));
		String docAsString = "<root><elementOne>1</elementOne><elementTwo>2</elementTwo></root>";
		XPathHeaderEnricher enricher = new XPathHeaderEnricher(expressionMap);
		Message<?> result = enricher.transform(MessageBuilder.withPayload(docAsString).build());
		MessageHeaders headers = result.getHeaders();
		assertEquals("Wrong value for element one expression", "1", headers.get("one"));
		assertEquals("Wrong value for element two expression", "2", headers.get("two"));
	}

	@Test
	public void nullValuesSkippedByDefault() {
		Map<String, XPathExpression> expressionMap = new HashMap<String, XPathExpression>();
		expressionMap.put("two", XPathExpressionFactory.createXPathExpression("/root/elementTwo"));
		String docAsString = "<root><elementOne>1</elementOne></root>";
		XPathHeaderEnricher enricher = new XPathHeaderEnricher(expressionMap);
		Message<?> result = enricher.transform(MessageBuilder.withPayload(docAsString).build());
		MessageHeaders headers = result.getHeaders();
		assertNull("value set for two when result was null", headers.get("two"));
	}

	@Test
	public void notSkippingNullValues() {
		Map<String, XPathExpression> expressionMap = new HashMap<String, XPathExpression>();
		expressionMap.put("two", XPathExpressionFactory.createXPathExpression("/root/elementTwo"));
		String docAsString = "<root><elementOne>1</elementOne></root>";
		XPathHeaderEnricher enricher = new XPathHeaderEnricher(expressionMap);
		enricher.setSkipNullResults(false);
		Message<?> result = enricher.transform(MessageBuilder.withPayload(docAsString).build());
		MessageHeaders headers = result.getHeaders();
		assertEquals("no value set for two when result was null and ignore null was false",
				"", headers.get("two"));
	}

	@Test
	public void numberEvaluationResult() {
		Map<String, XPathExpression> expressionMap = new HashMap<String, XPathExpression>();
		expressionMap.put("one", XPathExpressionFactory.createXPathExpression("/root/elementOne"));
		expressionMap.put("two", XPathExpressionFactory.createXPathExpression("/root/elementTwo"));
		Map<String, XPathEvaluationType> evalTypeMap = new HashMap<String, XPathEvaluationType>();
		evalTypeMap.put("two", XPathEvaluationType.NUMBER_RESULT);
		String docAsString = "<root><elementOne>1</elementOne><elementTwo>2</elementTwo></root>";
		XPathHeaderEnricher enricher = new XPathHeaderEnricher(expressionMap);
		enricher.setEvaluationTypes(evalTypeMap);
		Message<?> result = enricher.transform(MessageBuilder.withPayload(docAsString).build());
		MessageHeaders headers = result.getHeaders();
		assertEquals("Wrong value for element one expression", "1", headers.get("one"));
		assertEquals("Wrong value for element two expression", 2.0, headers.get("two"));
	}

}
