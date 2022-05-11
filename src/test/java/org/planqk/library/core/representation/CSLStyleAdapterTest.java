package org.planqk.library.core.representation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import org.jabref.logic.citationstyle.CitationStyle;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.planqk.library.core.repository.LibraryServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CSLStyleAdapterTest {
    @TempDir
    Path citationFilePath;

    @BeforeEach
    public void setupFile() throws URISyntaxException, IOException {
        Path styleSource = Paths.get(LibraryServiceTest.class.getClassLoader().getResource("org/planqk/library/core/acm-sig-proceedings.csl").toURI());
        Files.copy(styleSource, citationFilePath.resolve("acm-sig-proceedings.csl"), StandardCopyOption.REPLACE_EXISTING);
        CSLStyleAdapter.resetStyles();
    }

    @Test
    public void registerCitationStyleFromFile() throws IOException, URISyntaxException {
        CitationStyle style = CSLStyleAdapter.getInstance().registerCitationStyleFromFile(citationFilePath.resolve("acm-sig-proceedings.csl").toString());
        assertEquals("ACM SIG Proceedings (\"et al.\" for 3+ authors)", style.getTitle());
        assertEquals(getSource(), style.getSource().replaceAll("\r\n", "\n"));
    }

    @Test
    public void styleEntryPlain() throws IOException, URISyntaxException {
        String renderedEntry  = CSLStyleAdapter.getInstance().generatePlainCitation(getEntry(), "ACM SIGGRAPH");
        System.out.println(renderedEntry);
    }

    @Test
    public void registerAvailableCitationStyles() throws IOException, URISyntaxException {
        CSLStyleAdapter adapter = CSLStyleAdapter.getInstance();
        assertEquals(Set.of("ACM SIGGRAPH", "American Psychological Association 6th edition", "IEEE", "Turabian 9th edition (author-date)"), new HashSet<>(CSLStyleAdapter.getInstance().getRegisteredStyles()));
    }

    @Test
    public void getStyledEntry1() throws IOException, URISyntaxException {
        CSLStyleAdapter adapter = CSLStyleAdapter.getInstance();
        // In default use IEEE style
        assertEquals("  <div class=\"csl-entry\">\n" +
                "    <div class=\"csl-left-margin\">[1]</div><div class=\"csl-right-inline\">P. Saha and U. Kanewala, &ldquo;Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing,&rdquo; 2018-02-20.</div>\n" +
                "  </div>\n", adapter.generateCitation(getEntry(), "non-existing"));
    }

    @Test
    public void getStyledEntry2() throws IOException, URISyntaxException {
        CSLStyleAdapter adapter = CSLStyleAdapter.getInstance();
        // In default use IEEE style
        assertEquals("  <div class=\"csl-entry\"><span style=\"font-variant: small-caps\">Saha, P. and Kanewala, U.</span> 2018-02-20. Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing. .</div>\n", adapter.generateCitation(getEntry(), "ACM SIGGRAPH"));
        assertNotEquals(adapter.generateCitation(getEntry(), "IEEE"), adapter.generateCitation(getEntry(), "ACM SIGGRAPH"));
    }

    private BibEntry getEntry() {
        return new BibEntry(StandardEntryType.Article)
                .withCitationKey("Saha2018")
                .withField(StandardField.AUTHOR, "Prashanta Saha and Upulee Kanewala")
                .withField(StandardField.DATE, "2018-02-20")
                .withField(StandardField.TITLE, "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing");
    }

    private String getSource() {
        return """
                <?xml version="1.0" encoding="utf-8"?>
                <style xmlns="http://purl.org/net/xbiblio/csl" class="in-text" version="1.0" demote-non-dropping-particle="sort-only" default-locale="en-US">
                  <info>
                    <title>ACM SIG Proceedings ("et al." for 3+ authors)</title>
                    <id>http://www.zotero.org/styles/acm-sig-proceedings</id>
                    <link href="http://www.zotero.org/styles/acm-sig-proceedings" rel="self"/>
                    <link href="http://www.acm.org/sigs/publications/proceedings-templates" rel="documentation"/>
                    <author>
                      <name>Naeem Esfahani</name>
                      <email>nesfaha2@gmu.edu</email>
                      <uri>http://mason.gmu.edu/~nesfaha2/</uri>
                    </author>
                    <contributor>
                      <name>Chris Horn</name>
                      <email>chris.horn@securedecisions.com</email>
                    </contributor>
                    <contributor>
                      <name>Patrick O'Brien</name>
                    </contributor>
                    <category citation-format="numeric"/>
                    <category field="science"/>
                    <category field="engineering"/>
                    <updated>2017-07-15T11:28:14+00:00</updated>
                    <rights license="http://creativecommons.org/licenses/by-sa/3.0/">This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 License</rights>
                  </info>
                  <macro name="author">
                    <choose>
                      <if type="webpage">
                        <text variable="title" suffix=":"/>
                      </if>
                      <else>
                        <names variable="author">
                          <name name-as-sort-order="all" and="text" sort-separator=", " initialize-with="." delimiter-precedes-last="never" delimiter=", "/>
                          <label form="short" prefix=" "/>
                          <substitute>
                            <names variable="editor"/>
                            <names variable="translator"/>
                          </substitute>
                        </names>
                      </else>
                    </choose>
                  </macro>
                  <macro name="editor">
                    <names variable="editor">
                      <name initialize-with="." delimiter=", " and="text"/>
                      <label form="short" prefix=", "/>
                    </names>
                  </macro>
                  <macro name="access">
                    <choose>
                      <if type="article-journal" match="any">
                        <text variable="DOI" prefix=". DOI:https://doi.org/"/>
                      </if>
                    </choose>
                  </macro>
                  <citation collapse="citation-number">
                    <sort>
                      <key variable="citation-number"/>
                    </sort>
                    <layout prefix="[" suffix="]" delimiter=", ">
                      <text variable="citation-number"/>
                    </layout>
                  </citation>
                  <bibliography entry-spacing="0" second-field-align="flush" et-al-min="3" et-al-use-first="1">
                    <sort>
                      <key macro="author"/>
                      <key variable="title"/>
                    </sort>
                    <layout suffix=".">
                      <text variable="citation-number" prefix="[" suffix="]"/>
                      <text macro="author" suffix=" "/>
                      <date variable="issued" suffix=". ">
                        <date-part name="year"/>
                      </date>
                      <choose>
                        <if type="paper-conference">
                          <group delimiter=". ">
                            <text variable="title"/>
                            <group delimiter=" ">
                              <text variable="container-title" font-style="italic"/>
                              <group delimiter=", ">
                                <group delimiter=", " prefix="(" suffix=")">
                                  <text variable="publisher-place"/>
                                  <date variable="issued">
                                    <date-part name="month" form="short" suffix=" "/>
                                    <date-part name="year"/>
                                  </date>
                                </group>
                                <text variable="page"/>
                              </group>
                            </group>
                          </group>
                        </if>
                        <else-if type="article-journal">
                          <group delimiter=". ">
                            <text variable="title"/>
                            <text variable="container-title" font-style="italic"/>
                            <group delimiter=", ">
                              <text variable="volume"/>
                              <group delimiter=" ">
                                <text variable="issue"/>
                                <date variable="issued" prefix="(" suffix=")">
                                  <date-part name="month" form="short" suffix=" "/>
                                  <date-part name="year"/>
                                </date>
                              </group>
                              <text variable="page"/>
                            </group>
                          </group>
                        </else-if>
                        <else-if type="patent">
                          <group delimiter=". ">
                            <text variable="title"/>
                            <text variable="number"/>
                            <date variable="issued">
                              <date-part name="month" form="short" suffix=" "/>
                              <date-part name="day" suffix=", "/>
                              <date-part name="year"/>
                            </date>
                          </group>
                        </else-if>
                        <else-if type="thesis">
                          <group delimiter=". ">
                            <text variable="title" font-style="italic"/>
                            <text variable="archive_location" prefix="Doctoral Thesis #"/>
                            <text variable="publisher"/>
                          </group>
                        </else-if>
                        <else-if type="report">
                          <group delimiter=". ">
                            <text variable="title" font-style="italic"/>
                            <text variable="number" prefix="Technical Report #"/>
                            <text variable="publisher"/>
                          </group>
                        </else-if>
                        <else-if type="webpage">
                          <group delimiter=". ">
                            <text variable="URL" font-style="italic"/>
                            <date variable="accessed" prefix="Accessed: ">
                              <date-part name="year" suffix="-"/>
                              <date-part name="month" form="numeric-leading-zeros" suffix="-"/>
                              <date-part name="day" form="numeric-leading-zeros"/>
                            </date>
                          </group>
                        </else-if>
                        <else-if type="chapter paper-conference" match="any">
                          <group delimiter=". ">
                            <text variable="title"/>
                            <text variable="container-title" font-style="italic"/>
                            <text macro="editor"/>
                            <text variable="publisher"/>
                            <text variable="page"/>
                          </group>
                        </else-if>
                        <else-if type="bill book graphic legal_case legislation motion_picture report song" match="any">
                          <group delimiter=". ">
                            <text variable="title" font-style="italic"/>
                            <text variable="publisher"/>
                          </group>
                        </else-if>
                        <else>
                          <group delimiter=". ">
                            <text variable="title"/>
                            <text variable="container-title" font-style="italic"/>
                            <text variable="publisher"/>
                          </group>
                        </else>
                      </choose>
                      <text macro="access"/>
                    </layout>
                  </bibliography>
                </style>
                """;
    }
}
