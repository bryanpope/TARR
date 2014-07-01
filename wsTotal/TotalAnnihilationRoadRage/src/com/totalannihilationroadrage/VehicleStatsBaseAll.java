package com.totalannihilationroadrage;

import com.framework.Pixmap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import java.util.ArrayList;
import java.util.List;

public class VehicleStatsBaseAll extends DefaultHandler2
{
	List< VehicleStatsBase > vehicles;
	private List< Integer > data;
    private VehicleStatsBase aVehicle;
    public Pixmap tileSheetVehicles;
    final public int INDEX_START_CAR_TILES = 33;
    final public int INDEX_DESTROYED_CAR_TILES = 54;
    private String test, testA;

    VehicleStatsBaseAll()
	{
        vehicles = new ArrayList< VehicleStatsBase >();
	}
	
	void populateWithXMLFields (String localName, Attributes attributes)
	{
		if (localName.equals("vehicle"))
		{
            aVehicle = new VehicleStatsBase();
			for (int i = 0; i < attributes.getLength(); i++)
			{
                test = attributes.getLocalName(i);
				if (attributes.getLocalName(i).equals("type"))
				{
					aVehicle.type = VehicleType.getVehicleType(attributes.getValue(i));
					continue;
				}
                if (attributes.getLocalName(i).equals("size"))
                {
                    aVehicle.size = VehicleSize.getVehicleSize(attributes.getValue(i));
                    continue;
                }
				if (attributes.getLocalName(i).equals("mass"))
				{
					aVehicle.mass = Integer.valueOf(attributes.getValue(i));
					continue;
				}
                if (attributes.getLocalName(i).equals("structure"))
                {
                    aVehicle.structure = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("maxspeed"))
                {
                    aVehicle.maxSpeed = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("maneuverability"))
                {
                    aVehicle.maneuverability = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("braking"))
                {
                    aVehicle.braking = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("acceleration"))
                {
                    aVehicle.acceleration = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missilefactorleft"))
                {
                    testA = attributes.getValue(i);
                    aVehicle.missileFactor.left = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missilefactorright"))
                {
                    testA = attributes.getValue(i);
                    aVehicle.missileFactor.right = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missilefactorfront"))
                {
                    testA = attributes.getValue(i);
                    aVehicle.missileFactor.front = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missilefactorback"))
                {
                    aVehicle.missileFactor.back = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missileprotectionfactorleft"))
                {
                    aVehicle.missileProtectionFactor.left = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missileprotectionfactorright"))
                {
                    aVehicle.missileProtectionFactor.right = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missileprotectionfactorfront"))
                {
                    aVehicle.missileProtectionFactor.front = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("missileprotectionfactorback"))
                {
                    aVehicle.missileProtectionFactor.back = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("tires"))
                {
                    aVehicle.tires = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("boardingfactorleft"))
                {
                    aVehicle.boardingFactor.left = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("boardingfactorright"))
                {
                    aVehicle.boardingFactor.right = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("boardingfactorfront"))
                {
                    aVehicle.boardingFactor.front = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("boardingfactorback"))
                {
                    aVehicle.boardingFactor.back = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("crewcapacityinterior"))
                {
                    aVehicle.crewCapacityInterior = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("crewcapacityexterior"))
                {
                    aVehicle.crewCapacityExterior = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("fuel"))
                {
                    aVehicle.fuel = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("carryingcapacity"))
                {
                    aVehicle.carryingCapacity = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
                if (attributes.getLocalName(i).equals("basecost"))
                {
                    aVehicle.baseCost = Integer.valueOf(attributes.getValue(i));
                    continue;
                }
			}
			vehicles.add(aVehicle);
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
