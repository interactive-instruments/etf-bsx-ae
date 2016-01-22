/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.etf.bsxm;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.xalan.xslt.EnvironmentCheck;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.GlobalOptions;
import org.basex.core.MainOptions;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Set;
import org.basex.data.Result;
import org.basex.io.IOFile;
import org.basex.query.QueryProcessor;
import org.basex.util.Prop;
import org.basex.util.Util;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Johannes Echterhoff (echterhoff <at> interactive-instruments
 *         <dot> de)
 *
 */
public class XQuerySchematronTest {

	/** Database context. */
	protected Context context;

	/** Clean up files. */
	protected boolean cleanupDB;

	protected String dbname_base = Util.className(XQuerySchematronTest.class);

	public static final String SCHEMATRON_TEMPLATE_DIR_PATH = "src/main/resources/testprojects/templates/schematron";

	@Test
	public void test() throws Exception {

//		String transFac = System.getProperty("javax.xml.transform.TransformerFactory");
//		System.out.println("Transformer factory implementation: "+(transFac == null ? "<null>" : transFac));
//		
//		TransformerFactory tf = TransformerFactory.newInstance();
//		System.out.println(tf.getClass().getName());
//		
//		boolean environmentOK = (new EnvironmentCheck()).checkEnvironment (new PrintWriter(System.out));
		
		
		etfTest("imro2012_rules_invalid",
				"src/test/resources/schematron/imro2012-rulesv007.sch",
				"src/test/resources/xml/imro2012/schematron/invalid", "*.gml",
				true);
		etfTest("imro2012_rules_valid",
				"src/test/resources/schematron/imro2012-rulesv007.sch",
				"src/test/resources/xml/imro2012/schematron/valid", "*.gml",
				false);
		
		etfTest("imro2012_additional_validator_rules_invalid",
				"src/test/resources/schematron/additional-validator-rules-2012v0.06.sch",
				"src/test/resources/xml/imro2012/schematron_additional/invalid", "*.gml",
				true);
		etfTest("imro2012_additional_validator_rules_valid",
				"src/test/resources/schematron/additional-validator-rules-2012v0.06.sch",
				"src/test/resources/xml/imro2012/schematron_additional/valid", "*.gml",
				false);
		
		etfTest("imro2012_gmlsfL2_invalid",
				"src/test/resources/schematron/gmlsfL2-20110816.sch",
				"src/test/resources/xml/imro2012/schematron_gml32sf2/invalid",
				"*.gml", true);
		etfTest("imro2012_gmlsfL2_valid",
				"src/test/resources/schematron/gmlsfL2-20110816.sch",
				"src/test/resources/xml/imro2012/schematron_gml32sf2/valid",
				"*.gml", false);
	}

	/**
	 * @param testName
	 *            unique name for the test
	 * @param schFilePath
	 *            path to the schematron file to test with
	 * @param xmlFolderPath
	 *            path to the folder that contains the XML file(s) to test
	 * @param xmlFileFilterExpression
	 *            expression to identify the file(s) to test, for example
	 *            '*.gml', '*.xml', or 'testfile.xml'
	 * @param isFailExpected
	 *            <code>true</code> if the Schematron test is expected to fail
	 *            (i.e., the resulting ETF test report does contain at least one
	 *            etf:ResultStatus element with text value 'FAILED'), else
	 *            <code>false</code>.
	 * @throws Exception
	 */
	protected void etfTest(String testName,
			String schFilePath, String xmlFolderPath,
			String xmlFileFilterExpression, boolean isFailExpected)
					throws Exception {

		System.err
				.println("===== Now processing test '" + testName + "'. =====");
		/*
		 * Create output directory (delete an already existing one first to have
		 * a fresh start)
		 */
		String outDirPath = "testResults/" + testName;
		File outputDir = new File(outDirPath);

		if (outputDir.exists()) {
			FileUtils.forceDelete(outputDir);
		}

		FileUtils.forceMkdir(outputDir);

		/*
		 * Get content of test project directory and copy it to the result
		 * directory
		 */
		File templateDir = new File(SCHEMATRON_TEMPLATE_DIR_PATH);
		FileUtils.copyDirectory(templateDir, outputDir);

		/*
		 * Get the schematron file to test with and copy it to the output
		 * directory, renaming it to schematron.sch
		 */
		File schFile = new File(schFilePath);
		File schDestFile = new File(outputDir,"schematron.sch");

		FileUtils.copyFile(schFile, schDestFile);

		/*
		 * Determine parameters for query execution
		 */
		Map<String, String> params = paramMap(
				"testResults/" + testName);

		/*
		 * Execute the query
		 */
		String queryResult = executeQuery(outDirPath, params, xmlFolderPath,
				xmlFileFilterExpression);

		/*
		 * Parse result to identify if the ETF report XML indicates a success or
		 * not
		 */
		boolean isNoFails = parseResultStatus(queryResult);

		assertTrue(
				testName + ": Test result does not match expected result, which is that "
						+ (isFailExpected ? "at least one test failed."
								: "no test failed."),
				isFailExpected == !isNoFails);
	}

	/**
	 * @param queryResult
	 *            contains the etf:TestReport
	 * @return <code>true</code> if the ETF test report does not contain any
	 *         etf:ResultStatus element with text value 'FAILED', else
	 *         <code>false</code>.
	 * @throws Exception
	 */
	protected boolean parseResultStatus(String queryResult) throws Exception {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		Document xmlDocument = builder
				.parse(new ByteArrayInputStream(queryResult.getBytes()));

		XPath xPath = XPathFactory.newInstance().newXPath();
		String xpathExpression = "//*[local-name() = 'ResultStatus' and text() = 'FAILED']";

		Object result = xPath.compile(xpathExpression).evaluate(xmlDocument,
				XPathConstants.NODESET);

		if (result == null) {
			return true;
		} else {
			NodeList resultStatusFailedNodes = (NodeList) result;
			if (resultStatusFailedNodes.getLength() == 0) {
				return true;
			} else {
				return false;
			}
		}

	}

	/**
	 * Creates parameter information which needs to be set in the XQuery before
	 * execution.
	 * 
	 * @param projDirPath
	 * @return
	 * @throws IOException
	 */
	protected Map<String, String> paramMap(String projDirPath) throws IOException {

		Map<String, String> params = new HashMap<String, String>();

		params.put("projDir", (new File(projDirPath)).getCanonicalPath());
		params.put("outputFile", (new File(projDirPath + "/result.xml")).getCanonicalPath());
		params.put("dbBaseName", dbname_base);

		return params;
	}

	protected String executeQuery(String projDirPath,
			Map<String, String> variables, String xmlFolderPath,
			String fileFilterExpression) throws Exception {

		File xml = new File(xmlFolderPath);
		File xqFile = new File(projDirPath + "/" + "Schematron-basex.xq");

		/*
		 * the xquery expects that the database name has an index number as
		 * suffix
		 */
		final String dbname = Util.className(XQuerySchematronTest.class) + "-0";

		createContext(fileFilterExpression);

		new CreateDB(dbname, xml.getCanonicalPath()).execute(context);

		String query = readQuery(xqFile);

		final QueryProcessor qp = new QueryProcessor(query, context);

		// set parameters
		if (variables != null) {
			for (Entry<String, String> e : variables.entrySet()) {

				String varName = e.getKey();
				String varValue = e.getValue();

				qp.bind(varName, varValue);
			}
		}

		// execute the query
		final Result val = qp.execute();
		String valAsString = val.serialize().toString();

		closeContext();

		return valAsString;
	}

	protected String readQuery(File xqFile) throws IOException {

		try (BufferedReader bufReader = new BufferedReader(
				new FileReader(xqFile))) {

			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = bufReader.readLine()) != null) {
				sb.append(line);
			}

			String result = sb.toString();

			return result;
		}
	}

	protected String readXml(File xmlFile) throws IOException {

		try (BufferedReader bufReader = new BufferedReader(
				new XmlStreamReader(xmlFile))) {

			StringBuilder sb = new StringBuilder();
			String line;

			while ((line = bufReader.readLine()) != null) {
				sb.append(line);
			}

			String result = sb.toString();

			return result;
		}
	}

	protected void createContext(String fileFilterExpression)
			throws BaseXException {

		final IOFile sb = sandbox();
		sb.delete();
		assertTrue("Sandbox could not be created.", sb.md());
		context = new Context();

		new Set("CREATEFILTER", fileFilterExpression).execute(context);

		initContext(context);
		cleanupDB = true;
	}

	/**
	 * Initializes the specified context.
	 * 
	 * @param ctx
	 *            context
	 */
	protected void initContext(final Context ctx) {

		final IOFile sb = sandbox();
		ctx.globalopts.set(GlobalOptions.DBPATH, sb.path() + "/data");
		ctx.globalopts.set(GlobalOptions.WEBPATH, sb.path() + "/webapp");
		ctx.globalopts.set(GlobalOptions.RESTXQPATH, sb.path() + "/webapp");
		ctx.globalopts.set(GlobalOptions.REPOPATH, sb.path() + "/repo");

		ctx.options.set(MainOptions.CACHEQUERY, true);
	}

	/**
	 * Removes test databases and closes the database context.
	 */
	protected void closeContext() {
		if (cleanupDB) {
			context.close();
			assertTrue("Sandbox could not be deleted.", sandbox().delete());
		}
	}

	/**
	 * Returns the sandbox database path.
	 * 
	 * @return database path
	 */
	protected IOFile sandbox() {
		return new IOFile(Prop.TMP, dbname_base);
	}

}
