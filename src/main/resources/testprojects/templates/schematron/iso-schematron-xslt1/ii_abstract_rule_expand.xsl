<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:sch="http://www.ascc.net/xml/schematron"
    xmlns:iso="http://purl.oclc.org/dsdl/schematron">  

  <!-- Suppress declarations of abstract rules -->
  <xsl:template match="iso:rule[@abstract='true']">
    <xsl:comment>Suppressed abstract rule <xsl:value-of select="@id"/></xsl:comment>	
  </xsl:template> 
    
  <!-- Suppress uses of abstract rules -->
  <xsl:template match="iso:extends">  
    <xsl:comment>Replacing extends</xsl:comment>
    <xsl:variable name="id" select="./@rule"/>  
    <xsl:copy-of select="//iso:rule[@id=$id]/*" />
    <xsl:comment>done replacing extends</xsl:comment>
  </xsl:template>
  
  <!-- output everything else unchanged -->
  <xsl:template match="*" priority="-1">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates/> 
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>



