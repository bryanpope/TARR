package com.totalannihilationroadrage;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

public class TiledMap extends DefaultHandler2
{
	String version;
	TiledMapOrientation orientation;
	int width;  //number of tiles wide for the TiledMap
	int height;  //number of tiles high for the TiledMap
	int tileWidth;  //the number of pixels per Tile wide
	int tileHeight;  //the number of pixels per Tile high
	TiledMapTileset tileset;  //the tilesheet
	TiledMapImage image;
	List< TiledMapLayer > layers;  //the number of layers
	List< Integer > data;  //so for private use only
	
	TiledMap ()
	{
		tileset = new TiledMapTileset();
		image = new TiledMapImage();
		layers = new ArrayList< TiledMapLayer >();
	}
	
	void populateWithXMLFields (String localName, Attributes attributes)
	{
		if (localName.equals("map"))
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				if (attributes.getLocalName(i).equals("version"))
				{
					version = attributes.getValue(i);
					continue;
				}
				if (attributes.getLocalName(i).equals("orientation"))
				{
					String oValue = attributes.getValue(i);
					if (oValue.equals("orthogonal"))
					{
						orientation = TiledMapOrientation.ORTHOGONAL;
						continue;
					}
					if (oValue.equals("isometric"))
					{
						orientation = TiledMapOrientation.ISOMETRIC;
						continue;
					}
					if (oValue.equals("staggered"))
					{
						orientation = TiledMapOrientation.STAGGERED;
						continue;
					}
				}
				if (attributes.getLocalName(i).equals("width"))
				{
					width = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("height"))
				{
					height = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("tilewidth"))
				{
					tileWidth = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("tileheight"))
				{
					tileHeight = Integer.valueOf(attributes.getValue(i));
					continue;
				}
			}
			return;
		}
		if (localName.equals("tileset"))
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				if (attributes.getLocalName(i).equals("firstgid"))
				{
					tileset.firstGID = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("name"))
				{
					tileset.name = attributes.getValue(i);
					continue;
				}
				if (attributes.getLocalName(i).equals("tilewidth"))
				{
					tileset.tileWidth = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("tileheight"))
				{
					tileset.tileHeight = Integer.valueOf(attributes.getValue(i));
					continue;
				}
			}
			return;
		}
		if (localName.equals("image"))
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				if (attributes.getLocalName(i).equals("source"))
				{
					image.source = attributes.getValue(i);
					continue;
				}
				if (attributes.getLocalName(i).equals("width"))
				{
					image.width = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("height"))
				{
					image.height = Integer.valueOf(attributes.getValue(i));
					continue;
				}
			}
			return;
		}
		if (localName.equals("layer"))
		{
			TiledMapLayer aLayer = new TiledMapLayer();
			for (int i = 0; i < attributes.getLength(); i++)
			{
				if (attributes.getLocalName(i).equals("name"))
				{
					aLayer.name = attributes.getValue(i);
					continue;
				}
				if (attributes.getLocalName(i).equals("width"))
				{
					aLayer.width = Integer.valueOf(attributes.getValue(i));
					continue;
				}
				if (attributes.getLocalName(i).equals("height"))
				{
					aLayer.height = Integer.valueOf(attributes.getValue(i));
					continue;
				}
			}
			layers.add(aLayer);
			return;
		}
		if (localName.equals("data"))
		{
			data = layers.get(layers.size() - 1).data;
			return;
		}
		if (localName.equals("tile"))
		{
			if (attributes.getLocalName(0).equals("gid"))
			{
				data.add(Integer.valueOf(attributes.getValue(0)));
			}
			return;
		}
	}
	
	@Override
	public void error(SAXParseException saxpe)
	{
		System.out.println("error() " + saxpe);
	}
	
	@Override
	public void fatalError(SAXParseException saxpe)
	{
		System.out.println("fatalError() " + saxpe);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	{
		populateWithXMLFields (localName, attributes);
	}
	
	@Override
	public void warning(SAXParseException saxpe)
	{
		System.out.println("warning() " + saxpe);
	}	
}
