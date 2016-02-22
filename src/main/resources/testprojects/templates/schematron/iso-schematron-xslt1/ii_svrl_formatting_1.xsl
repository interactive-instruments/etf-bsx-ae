<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
  xmlns:svrlii="http://www.interactive-instruments.de/svrl"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="yes" method="xml" omit-xml-declaration="yes"/>

  <xsl:strip-space elements="*" />
  
  <xsl:template match="svrl:schematron-output">
    <svrlii:formattedOutput>
      <xsl:apply-templates select="svrl:active-pattern"/>
    </svrlii:formattedOutput>
  </xsl:template>

  <xsl:template match="svrl:active-pattern">
    <xsl:variable name="posLowerExclusive" select="count(preceding-sibling::*) + 1"/>
    <xsl:variable name="posUpperInclusive">
      <xsl:choose>
        <xsl:when test="following-sibling::svrl:active-pattern">
          <xsl:value-of
            select="count(following-sibling::svrl:active-pattern[1]/preceding-sibling::*)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="count(../*)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <svrl:active-pattern>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="copy-no-namespaces"
        select="../*[(position() > $posLowerExclusive) and ($posUpperInclusive >= position())]"
      > </xsl:apply-templates>
    </svrl:active-pattern>
  </xsl:template>

  <!-- generate a new element in the same namespace as the matched element,
     copying its attributes, but without copying its unused namespace nodes,
     then continue processing content in the "copy-no-namepaces" mode -->
  <xsl:template match="*" mode="copy-no-namespaces">
    <xsl:element name="{name()}" namespace="{namespace-uri()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="copy-no-namespaces" select="node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="comment() | processing-instruction()" mode="copy-no-namespaces">
    <xsl:copy/>
  </xsl:template>

</xsl:stylesheet>
