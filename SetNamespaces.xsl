<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
      <xsl:template match="*:Documents">
        <xsl:element name="{local-name(.)}">
            <xsl:apply-templates select="@* | node()" />
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
