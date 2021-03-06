/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Jan 25, 2010
 *
 */
package org.biojava.nbio.structure.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.biojava.nbio.core.util.StringManipulationHelper;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.align.StructureAlignment;
import org.biojava.nbio.structure.align.StructureAlignmentFactory;
import org.biojava.nbio.structure.align.ce.CeMain;
import org.biojava.nbio.structure.align.model.AFPChain;
import org.biojava.nbio.structure.align.util.AtomCache;
import org.biojava.nbio.structure.align.xml.AFPChainXMLConverter;
import org.biojava.nbio.structure.align.xml.AFPChainXMLParser;
import org.biojava.nbio.structure.test.align.fatcat.FlipAFPChainTest;
import org.biojava.nbio.structure.test.util.StringManipulationTestsHelper;
import org.junit.Test;

/** This test makes sure that the new representation of selenocysteins as SEC amino acids does not
 * affect the structure alignment results.
 *
 * @author andreas
 *
 */
public class TestSECalignment {

	@Test
	public void testOldSecOutput() throws Exception {

		String fileName = "/ce_1fdo.A_2iv2.X.out";
		InputStream inStream = this.getClass().getResourceAsStream(fileName);
		assertNotNull("Could not find file " + fileName +" in resource path. Config error?", inStream);
		String xml = StringManipulationHelper.convertStreamToString(inStream);

		AtomCache cache = new AtomCache();
		String name1="1FDO.A";
		String name2="2IV2.X";
		Atom[] ca1 = cache.getAtoms(name1);
		Atom[] ca2 = cache.getAtoms(name2);

		assertEquals(715, ca1.length);
		assertEquals(697, ca2.length);

		AFPChain afpChainOrig = AFPChainXMLParser.fromXML(xml, ca1, ca2);

		assertNotNull("Could not get AfpChain object from flat file!", afpChainOrig);

		assertEquals("Could not find alignment string for prot 1","MKKVVTVCPYCASGCKINLVVDNGKIVRAEAAQGKTNQGTLCLKGYYGWDFINDTQILTPRLKTPMIRRQRGGKLEPVSWDEALNYVAERLSAIKEKYGPDAIQTTGSSRGTGNETNYVMQKFARAVIGTNNVDCCARVUHGPSVA-----GLHQSVGNGAMSNAINEIDNTDLVFVFGYNPADSHPIVANHVINAKRNGAKIIVCDPRKIETARIADMHIALKNGSNIALLNAMGHVIIEENLYDKAFVASRTEGFEEYRKIVEGYTPESVEDITGVSASEIRQAARMYAQAKSAAILWGMGVTQFYQGVETVRSLTSLAMLTGNLGKPHAGVNPVRGQNNVQGACDMGALPDTYPGYQYVKDPANREKFAKAWGVESLPAHTGYRISELPHRAAHGEVRAAYIMGEDPLQTDAELSAVRKAFEDLELVIVQDIFMTKTASAADVILPSTSWGEHEGVFTAADRGFQRFFKAVEPKWDLKTDWQIISEIATRMGYPMHYNNTQEIWDELRHLCPDFYGATYEKMGELGFIQWPCRDTSDADQGTSYLFKEKFDTPNGLAQFFTCDWVAPIDKLTDEYPMVLSTVREVGHYSCRSMTGNCAALAALADEPGYAQINTEDAKRLGIEDEALVWVHSRKGKIITRAQVSDRPNKGAIYMTYQWWIGACNELVTENLSPITKTPEYKYCAVRVEPIADQRAAEQYVIDEYNKLKTRLREAALA", new String(afpChainOrig.getAlnseq1(),0,afpChainOrig.getAlnLength()));
		assertEquals("Could not find alignment string for prot 2","MKKVVTVCPYCASGCKINLVVDNGKIVRAEAAQGKTNQGTLCLKGYYGWDFINDTQILTPRLKTPMIRRQRGGKLEPVSWDEALNYVAERLSAIKEKYGPDAIQTTGSSRGTGNETNYVMQKFARAVIGTNNVDCCAR-----VUHGPSVAGLHQSVGNGAMSNAINEIDNTDLVFVFGYNPADSHPIVANHVINAKRNGAKIIVCDPRKIETARIADMHIALKNGSNIALLNAMGHVIIEENLYDKAFVASRTEGFEEYRKIVEGYTPESVEDITGVSASEIRQAARMYAQAKSAAILWGMGVTQFYQGVETVRSLTSLAMLTGNLGKPHAGVNPVRGQNNVQGACDMGALPDTYPGYQYVKDPANREKFAKAWGVESLPAHTGYRISELPHRAAHGEVRAAYIMGEDPLQTDAELSAVRKAFEDLELVIVQDIFMTKTASAADVILPSTSWGEHEGVFTAADRGFQRFFKAVEPKWDLKTDWQIISEIATRMGYPMHYNNTQEIWDELRHLCPDFYGATYEKMGELGFIQWPCRDTSDADQGTSYLFKEKFDTPNGLAQFFTCDWVAPIDKLTDEYPMVLSTVREVGHYSCRSMTGNCAALAALADEPGYAQINTEDAKRLGIEDEALVWVHSRKGKIITRAQVSDRPNKGAIYMTYQWW------------------PEYKYCAVRVEPIADQRAAEQYVIDEYNKLKTRLREAALA", new String(afpChainOrig.getAlnseq2(),0,afpChainOrig.getAlnLength()));

		// calc time is hardware dependent.... overwrite...
		afpChainOrig.setCalculationTime(-1);

		assertEquals("alnLength is wrong! (" + afpChainOrig.getAfpChainLen()+")" ,
				720,afpChainOrig.getAlnLength());
		assertEquals("gapLength is wrong! ("+ afpChainOrig.getGapLen() + ")",
				28, afpChainOrig.getGapLen());

		//identity should be 0.9569
		assertTrue("alignment ID is < 0.95 ! (" + afpChainOrig.getIdentity()+")" , afpChainOrig.getIdentity() > 0.95);
		assertTrue("alignment ID is > 0.999 ! (" + afpChainOrig.getIdentity()+")" , afpChainOrig.getIdentity() < 0.999);

		String xmlComp =  AFPChainXMLConverter.toXML(afpChainOrig, ca1, ca2);

		FlipAFPChainTest t = new FlipAFPChainTest();
		t.printFirstMismatch(xml, xmlComp);
		StringManipulationTestsHelper.assertEqualsIgnoreEndline(xml, xmlComp);
		StructureAlignment ce = StructureAlignmentFactory.getAlgorithm(CeMain.algorithmName);


		AFPChain afpChainNew = ce.align(ca1,ca2);
		afpChainNew.setCalculationTime(-1);
		afpChainNew.setName1(name1);
		afpChainNew.setName2(name2);



		String xmlNew = AFPChainXMLConverter.toXML(afpChainNew,ca1,ca2);

		StringManipulationTestsHelper.assertEqualsIgnoreEndline(xml,xmlNew);


	}
}
