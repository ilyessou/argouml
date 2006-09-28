<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                xmlns:exsl="http://exslt.org/common"
                xmlns:set="http://exslt.org/sets"
		version="1.0"
                exclude-result-prefixes="doc exsl set">

<!-- ********************************************************************
     $Id$
     ******************************************************************** 

     This file is used by htmlhelp.xsl if you want to generate source
     files for HTML Help.  It is based on the XSL DocBook Stylesheet
     distribution (especially on JavaHelp code) from Norman Walsh.

     ******************************************************************** -->

<xsl:import href="../html/chunk.xsl"/>

<!-- ==================================================================== -->
<!-- Customizations of standard HTML stylesheet parameters -->

<xsl:param name="suppress.navigation" select="1"/>

<!-- ==================================================================== -->

<xsl:template match="/">
  <xsl:choose>
    <xsl:when test="$rootid != ''">
      <xsl:choose>
        <xsl:when test="count(key('id',$rootid)) = 0">
          <xsl:message terminate="yes">
            <xsl:text>ID '</xsl:text>
            <xsl:value-of select="$rootid"/>
            <xsl:text>' not found in document.</xsl:text>
          </xsl:message>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>Formatting from <xsl:value-of select="$rootid"/></xsl:message>
          <xsl:apply-templates select="key('id',$rootid)" mode="process.root"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="/" mode="process.root"/>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:call-template name="hhp"/>
  <xsl:call-template name="hhc"/>
  <xsl:if test="($rootid = '' and //processing-instruction('dbhh')) or
                ($rootid != '' and key('id',$rootid)//processing-instruction('dbhh'))">
    <xsl:call-template name="hh-map"/>
    <xsl:call-template name="hh-alias"/>
  </xsl:if>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="hhp">
  <xsl:call-template name="write.text.chunk">
    <xsl:with-param name="filename" select="$htmlhelp.hhp"/>
    <xsl:with-param name="method" select="'text'"/>
    <xsl:with-param name="content">
      <xsl:call-template name="hhp-main"/>
    </xsl:with-param>
    <xsl:with-param name="encoding" select="$htmlhelp.encoding"/>
  </xsl:call-template>
</xsl:template>

<!-- ==================================================================== -->
<xsl:template name="hhp-main">
<xsl:text>[OPTIONS]
</xsl:text>
<xsl:if test="//indexterm">
<xsl:text>Auto Index=Yes
</xsl:text></xsl:if>
<xsl:text>Compatibility=1.1 or later
Compiled file=</xsl:text><xsl:value-of select="$htmlhelp.chm"/><xsl:text>
Contents file=</xsl:text><xsl:value-of select="$htmlhelp.hhc"/><xsl:text>
Default topic=</xsl:text>
<xsl:choose>
  <xsl:when test="$htmlhelp.default.topic != ''">
    <xsl:value-of select="$htmlhelp.default.topic"/>
  </xsl:when>
  <xsl:otherwise>
    <xsl:call-template name="make-relative-filename">
      <xsl:with-param name="base.dir" select="$base.dir"/>
      <xsl:with-param name="base.name">
        <xsl:choose>
          <xsl:when test="$rootid != ''">
            <xsl:apply-templates select="key('id',$rootid)" mode="chunk-filename"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="/" mode="chunk-filename"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:otherwise>
</xsl:choose>
<xsl:text>
Display compile progress=No
Full-text search=Yes
Language=</xsl:text>
<xsl:if test="//@lang">
  <xsl:variable name="lang" select="//@lang[1]"/>
  <xsl:value-of select="document('langcodes.xml')//gentext[@lang=string($lang)]"/>
</xsl:if>
<xsl:if test="not(//@lang)">
  <xsl:text>0x0409 English (United States)</xsl:text>
</xsl:if>
<xsl:text>
Title=</xsl:text>
  <xsl:choose>
    <xsl:when test="$htmlhelp.title = ''">
      <xsl:choose>
        <xsl:when test="$rootid != ''">
          <xsl:apply-templates select="key('id',$rootid)" mode="title.markup"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="/*" mode="title.markup"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$htmlhelp.title"/>
    </xsl:otherwise>
  </xsl:choose>
<xsl:text>

[FILES]
</xsl:text>

<xsl:choose>
  <xsl:when test="$rootid != ''">
    <xsl:apply-templates select="key('id',$rootid)" mode="enumerate-files"/>
  </xsl:when>
  <xsl:otherwise>
    <xsl:apply-templates select="/" mode="enumerate-files"/>
  </xsl:otherwise>
</xsl:choose>

<xsl:if test="$htmlhelp.enumerate.images">
  <xsl:variable name="imagelist">
    <xsl:choose>
      <xsl:when test="$rootid != ''">
        <xsl:apply-templates select="key('id',$rootid)" mode="enumerate-images"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="/" mode="enumerate-images"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:choose>
    <xsl:when test="function-available('exsl:node-set') and function-available('set:distinct')">
      <xsl:for-each select="set:distinct(exsl:node-set($imagelist)/filename)">
        <xsl:value-of select="."/>
        <xsl:text>&#10;</xsl:text>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$imagelist"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:if>

<xsl:if test="($htmlhelp.force.map.and.alias != 0) or 
              ($rootid = '' and //processing-instruction('dbhh')) or
              ($rootid != '' and key('id',$rootid)//processing-instruction('dbhh'))">
  <xsl:text>
[ALIAS]
#include </xsl:text><xsl:value-of select="$htmlhelp.alias.file"/><xsl:text>

[MAP]
#include </xsl:text><xsl:value-of select="$htmlhelp.map.file"/><xsl:text>
</xsl:text>
</xsl:if>

<xsl:value-of select="$htmlhelp.hhp.tail"/>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="set|book|part|preface|chapter|appendix
                     |article
                     |reference|refentry
                     |sect1|sect2|sect3|sect4|sect5
                     |section
                     |book/glossary|article/glossary
                     |book/bibliography|article/bibliography
                     |book/glossary|article/glossary
                     |colophon"
              mode="enumerate-files">
  <xsl:variable name="ischunk"><xsl:call-template name="chunk"/></xsl:variable>
  <xsl:if test="$ischunk='1'">
    <xsl:call-template name="make-relative-filename">
      <xsl:with-param name="base.dir" select="$base.dir"/>
      <xsl:with-param name="base.name">
        <xsl:apply-templates mode="chunk-filename" select="."/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:text>&#10;</xsl:text>
  </xsl:if>
  <xsl:apply-templates select="*" mode="enumerate-files"/>
</xsl:template>

<xsl:template match="text()" mode="enumerate-files">
</xsl:template>

<!-- ==================================================================== -->

<xsl:template match="graphic|inlinegraphic[@format!='linespecific']" mode="enumerate-images">
  <xsl:call-template name="write.filename.enumerate-images">
    <xsl:with-param name="filename">
      <xsl:call-template name="mediaobject.filename.enumerate-images">
        <xsl:with-param name="object" select="."/>
      </xsl:call-template>  
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<xsl:template match="mediaobject|inlinemediaobject" mode="enumerate-images">
  <xsl:call-template name="select.mediaobject.enumerate-images"/>
</xsl:template>

<xsl:template name="select.mediaobject.enumerate-images" mode="enumerate-images">
  <xsl:param name="olist"
             select="imageobject|imageobjectco
                     |videoobject|audioobject|textobject"/>
  <xsl:param name="count">1</xsl:param>

  <xsl:if test="$count &lt;= count($olist)">
    <xsl:variable name="object" select="$olist[position()=$count]"/>

    <xsl:variable name="useobject">
      <xsl:choose>
	<!-- The phrase is never used -->
        <xsl:when test="name($object)='textobject' and $object/phrase">
          <xsl:text>0</xsl:text>
        </xsl:when>
	<!-- The first textobject is a reasonable fallback (but not for image in HH) -->
        <xsl:when test="name($object)='textobject'">
          <xsl:text>0</xsl:text>
        </xsl:when>
	<!-- If there's only one object, use it -->
	<xsl:when test="$count = 1 and count($olist) = 1">
	  <xsl:text>1</xsl:text>
	</xsl:when>
	<!-- Otherwise, see if this one is a useable graphic -->
        <xsl:otherwise>
          <xsl:choose>
            <!-- peek inside imageobjectco to simplify the test -->
            <xsl:when test="local-name($object) = 'imageobjectco'">
              <xsl:call-template name="is.acceptable.mediaobject">
                <xsl:with-param name="object" select="$object/imageobject"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="is.acceptable.mediaobject">
                <xsl:with-param name="object" select="$object"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$useobject='1' and $object[not(*/@format='linespecific')]">
        <xsl:call-template name="write.filename.enumerate-images">
          <xsl:with-param name="filename">
            <xsl:call-template name="mediaobject.filename.enumerate-images">
              <xsl:with-param name="object" select="$object"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="select.mediaobject.enumerate-images">
          <xsl:with-param name="olist" select="$olist"/>
          <xsl:with-param name="count" select="$count + 1"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>

<xsl:template name="mediaobject.filename.enumerate-images" mode="enumerate-images">
  <xsl:param name="object"/>

  <xsl:variable name="urifilename">
    <xsl:call-template name="mediaobject.filename">
      <xsl:with-param name="object" select="$object"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="filename">
    <xsl:choose>
      <xsl:when test="starts-with($urifilename, 'file:/')">
	<xsl:value-of select="substring-after($urifilename, 'file:/')"/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select="$urifilename"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:value-of select="translate($filename, '/', '\')"/>

</xsl:template>

<xsl:template match="text()" mode="enumerate-images">
</xsl:template>

<xsl:template name="write.filename.enumerate-images" mode="enumerate-images">
  <xsl:param name="filename"/>
  <xsl:choose>
    <xsl:when test="function-available('exsl:node-set') and function-available('set:distinct')">
      <filename><xsl:value-of select="$filename"/></filename>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$filename"/>
      <xsl:text>&#10;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ==================================================================== -->

<!-- Following templates are not nice. It is because MS help compiler is unable
     to process correct HTML files. We must generate following weird
     stuff instead. -->

<xsl:template name="hhc">
  <xsl:call-template name="write.text.chunk">
    <xsl:with-param name="filename" select="$htmlhelp.hhc"/>
    <xsl:with-param name="method" select="'text'"/>
    <xsl:with-param name="content">
      <xsl:call-template name="hhc-main"/>
    </xsl:with-param>
    <xsl:with-param name="encoding" select="$htmlhelp.encoding"/>
  </xsl:call-template>
</xsl:template>

<xsl:template name="hhc-main">
  <xsl:text>&lt;HTML&gt;
&lt;HEAD&gt;
&lt;/HEAD&gt;
  &lt;BODY&gt;
</xsl:text>
  <xsl:if test="$htmlhelp.hhc.folders.instead.books != 0">
    <xsl:text>&lt;OBJECT type="text/site properties"&gt;
	&lt;param name="ImageType" value="Folder"&gt;
&lt;/OBJECT&gt;
</xsl:text>
  </xsl:if>
  <xsl:if test="$htmlhelp.hhc.show.root != 0">
<xsl:text>&lt;UL&gt;
</xsl:text>
  </xsl:if>

  <xsl:choose>
    <xsl:when test="$rootid != ''">
      <xsl:apply-templates select="key('id',$rootid)" mode="hhc"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="/" mode="hhc"/>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:if test="$htmlhelp.hhc.show.root != 0">
  <xsl:text>&lt;/UL&gt;
</xsl:text>
  </xsl:if>
  <xsl:text>&lt;/BODY&gt;
&lt;/HTML&gt;</xsl:text>
</xsl:template>

<xsl:template match="set" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="book">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:if test="$generate.set.toc != 0 and $htmlhelp.hhc.show.root = 0">
        <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
          &lt;param name="Name" value="</xsl:text>
            <xsl:call-template name="gentext">
              <xsl:with-param name="key" select="'TableofContents'"/>
            </xsl:call-template>
          <xsl:text>"&gt;
          &lt;param name="Local" value="</xsl:text>
              <xsl:call-template name="href.target.with.base.dir"/>
          <xsl:text>"&gt;
        &lt;/OBJECT&gt;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="book" mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="book" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="part|reference|preface|chapter|appendix|bibliography|article|colophon|glossary">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:if test="$generate.book.toc != 0 and $htmlhelp.hhc.show.root = 0 and not(parent::*)">
        <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
          &lt;param name="Name" value="</xsl:text>
            <xsl:call-template name="gentext">
              <xsl:with-param name="key" select="'TableofContents'"/>
            </xsl:call-template>
          <xsl:text>"&gt;
          &lt;param name="Local" value="</xsl:text>
              <xsl:call-template name="href.target.with.base.dir"/>
          <xsl:text>"&gt;
        &lt;/OBJECT&gt;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="part|reference|preface|chapter|bibliography|appendix|article|colophon|glossary"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="part|reference|preface|chapter|bibliography|appendix|article|colophon|glossary"
              mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="reference|preface|chapter|appendix|refentry|section|sect1|bibliodiv">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates
	select="reference|preface|chapter|appendix|refentry|section|sect1|bibliodiv"
	mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="section" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="section[count(ancestor::section) &lt; $htmlhelp.hhc.section.depth]|refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="section|refentry" mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="sect1" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="sect2[$htmlhelp.hhc.section.depth > 1]|refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="sect2|refentry"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="sect2" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="sect3[$htmlhelp.hhc.section.depth > 2]|refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="sect3|refentry"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="sect3" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="sect4[$htmlhelp.hhc.section.depth > 3]|refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="sect4|refentry"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="sect4" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="sect5[$htmlhelp.hhc.section.depth > 4]|refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="sect5|refentry"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<xsl:template match="sect5|refentry|colophon|bibliodiv" mode="hhc">
  <xsl:variable name="title">
    <xsl:if test="$htmlhelp.autolabel=1">
      <xsl:variable name="label.markup">
        <xsl:apply-templates select="." mode="label.markup"/>
      </xsl:variable>
      <xsl:if test="normalize-space($label.markup)">
        <xsl:value-of select="concat($label.markup,$autotoc.label.separator)"/>
      </xsl:if>
    </xsl:if>
    <xsl:apply-templates select="." mode="title.markup"/>
  </xsl:variable>

  <xsl:if test="$htmlhelp.hhc.show.root != 0 or parent::*">
    <xsl:text>&lt;LI&gt; &lt;OBJECT type="text/sitemap"&gt;
      &lt;param name="Name" value="</xsl:text>
          <xsl:value-of select="normalize-space($title)"/>
      <xsl:text>"&gt;
      &lt;param name="Local" value="</xsl:text>
          <xsl:call-template name="href.target.with.base.dir"/>
      <xsl:text>"&gt;
    &lt;/OBJECT&gt;</xsl:text>
  </xsl:if>
  <xsl:if test="refentry">
    <xsl:text>&lt;UL&gt;</xsl:text>
      <xsl:apply-templates select="refentry"
			   mode="hhc"/>
    <xsl:text>&lt;/UL&gt;</xsl:text>
  </xsl:if>
</xsl:template>

<!-- ==================================================================== -->

<!-- no separate HTML page with index -->
<xsl:template match="index"/>   
<xsl:template match="index" mode="toc"/>

<xsl:template match="indexterm">

  <xsl:variable name="primary" select="normalize-space(primary)"/>
  <xsl:variable name="secondary" select="normalize-space(secondary)"/>
  <xsl:variable name="tertiary" select="normalize-space(tertiary)"/>

  <xsl:variable name="text">
    <xsl:value-of select="$primary"/>
    <xsl:if test="secondary">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$secondary"/>
    </xsl:if>
    <xsl:if test="tertiary">
      <xsl:text>, </xsl:text>
      <xsl:value-of select="$tertiary"/>
    </xsl:if>
  </xsl:variable>

  <xsl:if test="secondary">
    <xsl:if test="not(//indexterm[normalize-space(primary)=$primary and not(secondary)])">
      <xsl:call-template name="write.indexterm">
        <xsl:with-param name="text" select="$primary"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>

  <xsl:call-template name="write.indexterm">
    <xsl:with-param name="text" select="$text"/>
  </xsl:call-template>

</xsl:template>

<xsl:template name="write.indexterm">
  <xsl:param name="text"/>
  <OBJECT type="application/x-oleobject"
          classid="clsid:1e2a7bd0-dab9-11d0-b93a-00c04fc99f9e">
    <param name="Keyword" value="{$text}"/>
  </OBJECT>
</xsl:template>

<!-- ==================================================================== -->

<xsl:template name="hh-map">
  <xsl:call-template name="write.text.chunk">
    <xsl:with-param name="filename" select="$htmlhelp.map.file"/>
    <xsl:with-param name="method" select="'text'"/>
    <xsl:with-param name="content">
     <xsl:choose>
       <xsl:when test="$rootid != ''">
         <xsl:apply-templates select="key('id',$rootid)" mode="hh-map"/>
       </xsl:when>
       <xsl:otherwise>
         <xsl:apply-templates select="/" mode="hh-map"/>
       </xsl:otherwise>
     </xsl:choose>
    </xsl:with-param>
    <xsl:with-param name="encoding" select="$htmlhelp.encoding"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="processing-instruction('dbhh')" mode="hh-map">
  <xsl:variable name="topicname">
    <xsl:call-template name="pi-attribute">
      <xsl:with-param name="pis"
                      select="."/>
      <xsl:with-param name="attribute" select="'topicname'"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="topicid">
    <xsl:call-template name="pi-attribute">
      <xsl:with-param name="pis"
                      select="."/>
      <xsl:with-param name="attribute" select="'topicid'"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:text>#define </xsl:text>
  <xsl:value-of select="$topicname"/>
  <xsl:text>&#9;</xsl:text>
  <xsl:value-of select="$topicid"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

<xsl:template match="text()" mode="hh-map"/>

<!-- ==================================================================== -->

<xsl:template name="hh-alias">
  <xsl:call-template name="write.text.chunk">
    <xsl:with-param name="filename" select="$htmlhelp.alias.file"/>
    <xsl:with-param name="method" select="'text'"/>
    <xsl:with-param name="content">
     <xsl:choose>
       <xsl:when test="$rootid != ''">
         <xsl:apply-templates select="key('id',$rootid)" mode="hh-alias"/>
       </xsl:when>
       <xsl:otherwise>
         <xsl:apply-templates select="/" mode="hh-alias"/>
       </xsl:otherwise>
     </xsl:choose>
    </xsl:with-param>
    <xsl:with-param name="encoding" select="$htmlhelp.encoding"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="processing-instruction('dbhh')" mode="hh-alias">
  <xsl:variable name="topicname">
    <xsl:call-template name="pi-attribute">
      <xsl:with-param name="pis"
                      select="."/>
      <xsl:with-param name="attribute" select="'topicname'"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="href">
    <xsl:call-template name="href.target.with.base.dir">
      <xsl:with-param name="object" select=".."/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$topicname"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="substring-before(concat($href, '#'), '#')"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>

<xsl:template match="text()" mode="hh-alias"/>

<!-- ==================================================================== -->

<xsl:template name="href.target.with.base.dir">
  <xsl:param name="object" select="."/>
  <xsl:value-of select="$base.dir"/>
  <xsl:call-template name="href.target">
    <xsl:with-param name="object" select="$object"/>
  </xsl:call-template>
</xsl:template>

</xsl:stylesheet>
