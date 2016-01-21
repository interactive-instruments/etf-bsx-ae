declare namespace gml='http://www.opengis.net/gml/3.2';
declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance';
declare namespace xlink='http://www.w3.org/1999/xlink';
declare namespace etf='http://www.interactive-instruments.de/etf/1.0';
declare namespace ii='http://www.interactive-instruments.de/ii/1.0';
declare namespace iso='http://purl.oclc.org/dsdl/schematron';
declare namespace xsl='http://www.w3.org/1999/XSL/Transform';
declare namespace map='http://www.w3.org/2005/xpath-functions/map';
declare namespace svrl='http://purl.oclc.org/dsdl/svrl';
declare namespace svrlii='http://www.interactive-instruments.de/svrl';

(:===========================:)
(: Parameters as strings :)
(:===========================:)

declare variable $Files_to_test external := ".*";
(: ggfs. durch Auswahl der Schematron-Phase ersetzen declare variable $Tests_to_perform external := ".*"; :)
declare variable $Maximum_number_of_error_messages_per_test external := "100";
declare variable $Testsystem external := "unknown";
declare variable $Tester external := "unknown";

declare variable $printExactLocation external := "true"; (: if set to true - ignoring case and leading or trailing whitespace - the XPath of the element that caused an assertion to fail or a report to be generated will be included in messages:)

(:===========================:)
(: Default ETF parameters :)
(:===========================:)

declare variable $projDir external; (: path to folder that contains the schematron file :)
declare variable $outputFile external; (: path of result file :)

declare variable $dbBaseName external;
declare variable $dbCount external;
declare variable $dbDir external;

declare variable $reportLabel external;
declare variable $reportStartTimestamp external;
declare variable $reportId external;
declare variable $testObjectId external;
declare variable $validationErrors external;

(:=============================:)
(: Other variable declarations :)
(:=============================:)

declare variable $paramerror := xs:QName("etf:ParameterError");

(:===========================:)
(: Parameter checks :)
(:===========================:)

try { let $x := matches('any.valid.regex',$Files_to_test) 
return ()
} catch * {
error($paramerror,concat("The value of parameter $Files_to_test must be a valid regular expression. Given value was '",data($Files_to_test),"', which leads to the following application error:&#xa; '",data($err:description),"'&#xa;"))
},

try { let $x := xs:int($Maximum_number_of_error_messages_per_test)
return ()
} catch * {
error($paramerror,concat("The parameter $Maximum_number_of_error_messages_per_test must be an integer. Found '",data($Maximum_number_of_error_messages_per_test),"'&#xa;"))
}