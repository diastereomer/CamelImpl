<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://claimcenter.msigusa.com/" xmlns:c="http://wins_policy_search.wsbeans.iseries/">
	<xsl:output method="xml" indent="yes"/>
	<xsl:template match="/" >
		<xsl:choose>
			<xsl:when test="c:*/return/POLICY_RETRIVAL_LINKAGE/REQUEST_DETAILS/REQTYP='IN'">
				<tns:searchPoliciesbyInsuredResponse>
					<xsl:call-template name="policies"/>
					<xsl:call-template name="message"/> 					
				</tns:searchPoliciesbyInsuredResponse>
			</xsl:when>
			<xsl:when test="c:*/return/POLICY_RETRIVAL_LINKAGE/REQUEST_DETAILS/REQTYP='LN'">
				<tns:getPolicyLocationsResponse>
					<xsl:call-template name="policyHeader"/> 
					<xsl:call-template name="locations"/> 
					<xsl:call-template name="message"/>
				</tns:getPolicyLocationsResponse>
			</xsl:when>
			<xsl:when test="c:*/return/POLICY_RETRIVAL_LINKAGE/REQUEST_DETAILS/REQTYP='CO'">
				<tns:getPolicyCoveragesResponse>
					<xsl:call-template name="locations"/> 
					<xsl:call-template name="coverages"/>
					<xsl:call-template name="message"/>
				</tns:getPolicyCoveragesResponse>
			</xsl:when>
			<xsl:when test="c:*/return/POLICY_RETRIVAL_LINKAGE/REQUEST_DETAILS/REQTYP='LI'">
				<tns:getPolicyCoveragesResponse>
					<xsl:call-template name="locations"/> 
					<xsl:call-template name="limits"/>
					<xsl:call-template name="message"/>
				</tns:getPolicyCoveragesResponse>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="policies">
		<tns:Policies>
			<xsl:for-each select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/INSURED_NAME">
				<xsl:if test="INSNAM!=''">
					<tns:Policy>
						<tns:AccountNumber>
							<xsl:value-of select="ACCTNO"/>
						</tns:AccountNumber>
						<tns:Agent>
							<xsl:value-of select="AGENT"/>
						</tns:Agent>
						<tns:AgentType>
							<xsl:value-of select="AGYTYP"/>
						</tns:AgentType>
						<tns:CLMFLD>
							<xsl:value-of select="CLMFLD"/>
						</tns:CLMFLD>
						<tns:UnderwritingCompany>
							<xsl:value-of select="CO"/>
						</tns:UnderwritingCompany>
						<tns:EndorsementDate>
							<xsl:value-of select="EDSDTE"/>
						</tns:EndorsementDate>
						<tns:EndorsementNumber>
							<xsl:value-of select="EDSNO"/>
						</tns:EndorsementNumber>
						<tns:PolicyEffDate>
							<xsl:value-of select="EFFDTE"/>
						</tns:PolicyEffDate>
						<tns:InsuredName>
							<xsl:value-of select="INSNAM"/>
						</tns:InsuredName>
						<tns:PolicyNumber>
							<xsl:value-of select="POLICY"/>
						</tns:PolicyNumber>
						<tns:ProductCode>
							<xsl:value-of select="PROD"/>
						</tns:ProductCode>
						<tns:Status>
							<xsl:value-of select="STATAS"/>
						</tns:Status>
					</tns:Policy>
				</xsl:if>
			</xsl:for-each>
		</tns:Policies>
	</xsl:template>
	<xsl:template name="message">
		<tns:Message>
			<tns:Descriptions>
				<xsl:for-each select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/MSG_DETAILS/MSG_DES[.!='']">
					<tns:Description>
						<xsl:value-of select="."/>
					</tns:Description>	
				</xsl:for-each>
			</tns:Descriptions>
			<tns:SuccessFlag>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/MSG_DETAILS/SUCFLG"/>
			</tns:SuccessFlag>
		</tns:Message>
	</xsl:template>
	<xsl:template name="policyHeader">
		<tns:PolicyHeader>
			<tns:AccountNumber>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/ACCTNO"/>
			</tns:AccountNumber>
			<tns:Agent>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/AGENT"/>
			</tns:Agent>
			<tns:UnderwritingCompany>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/CO"/>
			</tns:UnderwritingCompany>
			<tns:DirectOrAssumed>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/DIRASS"/>
			</tns:DirectOrAssumed>
			<tns:EndorsementNumber>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/EDSNO"/>
			</tns:EndorsementNumber>
			<tns:PolicyEffDate>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/EFFDTE"/>
			</tns:PolicyEffDate>
			<tns:PolicyExpDate>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/EXPDTE"/>
			</tns:PolicyExpDate>
			<tns:SpecialHandling>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/FIELDE"/>
			</tns:SpecialHandling>
			<tns:FlatCommission>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/FIELDF"/>
			</tns:FlatCommission>
			<tns:InsuredAddress1>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSAD1"/>
			</tns:InsuredAddress1>
			<tns:InsuredAddress2>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSAD2"/>
			</tns:InsuredAddress2>
			<tns:InsuredCity>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSCTY"/>
			</tns:InsuredCity>
			<tns:InsuredName>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSNAM"/>
			</tns:InsuredName>
			<tns:InsuredState>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSST"/>
			</tns:InsuredState>
			<tns:InsuredType>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSTYP"/>
			</tns:InsuredType>
			<tns:InsuredZIP>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/INSZIP"/>
			</tns:InsuredZIP>
			<tns:OriginalPolicyYear>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/ORPOYR"/>
			</tns:OriginalPolicyYear>
			<tns:PolicyNumber>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/POLICY"/>
			</tns:PolicyNumber>
			<tns:ProductCode>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/PROD"/>
			</tns:ProductCode>
			<tns:ReinsuranceFlag>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/REINFL"/>
			</tns:ReinsuranceFlag>
			<tns:UserLocation>
				<xsl:value-of select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/POLICY_LINKAGE/USELOC"/>
			</tns:UserLocation>
		</tns:PolicyHeader>
	</xsl:template>
	<xsl:template name="locations">
		<tns:Locations>
			<xsl:for-each select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/LOCATION_DETAILS[LOCNUM!='']">
				<tns:Location>
					<tns:BuildingNumber>
						<xsl:value-of select="BLDNUM"/>
					</tns:BuildingNumber>
					<tns:LocationDescription>
						<xsl:value-of select="CDDESC"/>
					</tns:LocationDescription>
					<tns:CoverageGroup>
						<xsl:value-of select="COVERG"/>
					</tns:CoverageGroup>
					<tns:LocationNumber>
						<xsl:value-of select="LOCNUM"/>
					</tns:LocationNumber>
					<tns:PrimiumState>
						<xsl:value-of select="PRMSTE"/>
					</tns:PrimiumState>
					<tns:Territory>
						<xsl:value-of select="TERR"/>
					</tns:Territory>
					<tns:VehicleID>
						<xsl:value-of select="VEHID"/>
					</tns:VehicleID>
				</tns:Location>
			</xsl:for-each>
		</tns:Locations>
	</xsl:template>
	<xsl:template name="coverages">
		<tns:Coverages>
			<xsl:for-each select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/COVERAGE_DETAILS[COVERG!='']">
				<tns:Coverage>
					<tns:ASLOB>
						<xsl:value-of select="ASLOB"/>
					</tns:ASLOB>
					<tns:BuildingNumber>
						<xsl:value-of select="BLDNUM"/>
					</tns:BuildingNumber>
					<tns:LocationDescription>
						<xsl:value-of select="CDDESC"/>
					</tns:LocationDescription>
					<tns:Class>
						<xsl:value-of select="CLASX"/>
					</tns:Class>
					<tns:ClassDescription>
						<xsl:value-of select="CLDESC"/>
					</tns:ClassDescription>
					<tns:CoverageGroup>
						<xsl:value-of select="COVERG"/>
					</tns:CoverageGroup>
					<tns:InternalCoverageCode>
						<xsl:value-of select="INTCOV"/>
					</tns:InternalCoverageCode>
					<tns:LocationNumber>
						<xsl:value-of select="LOCNUM"/>
					</tns:LocationNumber>
					<tns:Subline>
						<xsl:value-of select="SUBLN"/>
					</tns:Subline>
				</tns:Coverage>
			</xsl:for-each>
		</tns:Coverages>
	</xsl:template>
	<xsl:template name="limits">
		<tns:LimitDetails>
			<xsl:for-each select="/c:*/return/POLICY_RETRIVAL_LINKAGE/RESPONSE_DETAILS/LIMITS_DETAILS[LITERL_1!='' or LIMIT_LINK2/LITERL_2!='']">
				<tns:LimitDetail>
					<tns:Limits>
						<xsl:for-each select="LIMIT_LINK2[LITERL_2!='']">
							<tns:Limit>
								<tns:PolicyOrLocation>
									<xsl:value-of select="LEVEL"/>
								</tns:PolicyOrLocation>
								<tns:LimitValue>
									<xsl:value-of select="LIMVAL"/>
								</tns:LimitValue>
								<tns:LimitName>
									<xsl:value-of select="LITERL_2"/>
								</tns:LimitName>
							</tns:Limit>
						</xsl:for-each>
					</tns:Limits>
					<tns:LITERL_1>
						<xsl:value-of select="LITERL_1"/>
					</tns:LITERL_1>
				</tns:LimitDetail>
			</xsl:for-each>
		</tns:LimitDetails>
	</xsl:template>
</xsl:stylesheet>