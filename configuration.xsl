<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:hs="http://www.esei.uvigo.es/dai/hybridserver">
	<xsl:output method="html" indent="yes" encoding="utf-8" />
	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE HTML&gt;</xsl:text>
		<html>
			<head>
				<title>Configuration</title>
			</head>
			<body>
				<h1>Configuration</h1>
				<h2>Connections</h2>
				<xsl:apply-templates select="hs:configuration/hs:connections" />
				<h2>Database</h2>
				<xsl:apply-templates select="hs:configuration/hs:database" />
				<h2>Servers</h2>
				<xsl:apply-templates select="hs:configuration/hs:servers" />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="hs:connections">
		<div>
			<ul>
				<li><b>HTTP: </b><xsl:value-of select="hs:http" /></li>
				<li><b>WebService: </b><xsl:value-of select="hs:webservice" /></li>
				<li><b>Maximun number of clients: </b><xsl:value-of select="hs:numClients" /></li>
			</ul>
		</div>
	</xsl:template>
	<xsl:template match="hs:database">
		<div>
			<ul>
				<li><b>User: </b><xsl:value-of select="hs:user" /></li>
				<li><b>Password: </b><xsl:value-of select="hs:password" /></li>
				<li><b>URL: </b><xsl:value-of select="hs:url" /></li>
			</ul>
		</div>
	</xsl:template>
	<xsl:template match="hs:servers">
		<div>
			<ul>
				<xsl:for-each select="hs:server">
					<h2><xsl:value-of select="@name"/></h2>
					<ul>
					<li><b>WSDL: </b><xsl:value-of select="@wsdl" /></li>
					<li><b>Namespace: </b><xsl:value-of select="@namespace" /></li>
					<li><b>Service: </b><xsl:value-of select="@service" /></li>
					<li><b>HTTPAddress: </b><xsl:value-of select="@httpAddress" /></li>
					</ul>
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>

</xsl:stylesheet>