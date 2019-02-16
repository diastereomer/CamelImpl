<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xpath-default-namespace="http://claimcenter.msigusa.com/">
	<xsl:output method="xml" indent="yes"/>
	<xsl:param name="operationName"/>
	<xsl:template match="/*" >
		<cwr:cwrappiws1 xmlns:cwr="http://wins_policy_search.wsbeans.iseries/">
			<arg0>
				<POLICY_RETRIVAL_LINKAGE>
					<REQUEST_DETAILS>
						<ACCDTE>
								<xsl:value-of select="AccidentDate"/>
						</ACCDTE>
						<BLDNUM><xsl:value-of select="BuildingNumber"/></BLDNUM>
						<CO><xsl:value-of select="UnderwritingCompany"/></CO>
						<COVERG><xsl:value-of select="CoverageGroup"/></COVERG>
						<EDSDTE><xsl:value-of select="EndorsementDate"/></EDSDTE>
						<EDSNO><xsl:value-of select="EndorsementNumber"/></EDSNO>
						<EFFDTE><xsl:value-of select="PolicyEffDate"/></EFFDTE>
						<INSNAM><xsl:value-of select="Insured"/></INSNAM>
						<LOCNUM><xsl:value-of select="LocationNumber"/></LOCNUM>
						<POLICY><xsl:value-of select="PolicyNumber"/></POLICY>
						<PRMSTE><xsl:value-of select="PremiumState"/></PRMSTE>
						<PROD><xsl:value-of select="ProductCode"/></PROD>
						<xsl:choose>
							<xsl:when test="$operationName = 'searchPoliciesbyInsured'">
								<REQTYP>IN</REQTYP>
							</xsl:when>
							<xsl:when test="$operationName = 'getPolicyLocations'">
								<REQTYP>LN</REQTYP>
							</xsl:when>
							<xsl:when test="$operationName = 'getPolicyLimits'">
								<REQTYP>LI</REQTYP>
							</xsl:when>
							<xsl:when test="$operationName = 'getPolicyCoverages'">
								<REQTYP>CO</REQTYP>
							</xsl:when>
							<xsl:otherwise>
								<REQTYP></REQTYP>
							</xsl:otherwise>
						</xsl:choose>
								<!--<xsl:value-of select="$operationName"/>-->
					</REQUEST_DETAILS>
				</POLICY_RETRIVAL_LINKAGE>
			</arg0>
		</cwr:cwrappiws1>
	</xsl:template>
</xsl:stylesheet>