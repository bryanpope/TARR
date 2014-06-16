package com.framework.impl;

import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import com.totalannihilationroadrage.TiledMap;

public class TMXParse
{
	AssetManager assetManager;
	SAXParserFactory spf;
	SAXParser sp;
	XMLReader xmlr;
    DefaultHandler2 handler;
    
    public TMXParse (AssetManager am)
    {
    	assetManager = am;
    }
    
    public void setHandler (DefaultHandler2 hr)
    {
    	handler = hr;
    }
    
    public void load(String filenameTMX) 
    {
		try
		{
			//assetManager = getBaseContext().getAssets();
			InputStream is = assetManager.open(filenameTMX);
			spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();
			xmlr = sp.getXMLReader();
			//xmlr.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
			//handler = new Handler();
			xmlr.setContentHandler(handler);
			xmlr.setDTDHandler(handler);
			//xmlr.setEntityResolver(handler);
			xmlr.setErrorHandler(handler);
			xmlr.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
			xmlr.parse(new InputSource(is));    
		}
		catch (IOException ioe)
		{
			System.err.println("IOE: " + ioe);
			//OutputView.outputText("IOE: " + ioe);
		}
		catch (SAXException saxe)
		{
			System.err.println("SAXE: " + saxe);
			//OutputView.outputText("SAXE: " + saxe);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//OutputView.outputText("PCE: " + e);
		}
	}
}
