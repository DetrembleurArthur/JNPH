package parsers;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SaxExtractor
{
    public static ArrayList<ObjectParser> extract(String xml)
    {
        SaxHandler sc = new SaxHandler(xml);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(true);
        try
        {
            SAXParser sp = spf.newSAXParser();
            sp.parse(new File(sc.getNomFichierXML()), sc);
        } catch (ParserConfigurationException e)
        {
            System.out.println("Oh oh Problème de config : " + e.getMessage());
            return null;
        } catch (SAXException e)
        {
            System.out.println("Oh oh Problème de SAX : " + e.getMessage());
            return null;
        } catch (IOException e)
        {
            System.out.println("Oh oh Problème d'IO : " + e.getMessage());
            return null;
        }
        ArrayList<ObjectParser> list = sc.getList();
        for (ObjectParser obj : list)
        {
            System.err.println(obj);
        }
        return list;
    }

    public static Serveur extractServer(ArrayList<ObjectParser> objectParsers, String nomServer)
    {
        for(ObjectParser objectParser : objectParsers)
        {
            if(objectParser instanceof Serveur)
            {
                if(((Serveur)objectParser).getNom().equals(nomServer))
                    return (Serveur)objectParser;
            }
        }
        return null;
    }

    public static Database extractDB(ArrayList<ObjectParser> objectParsers, String nomDB)
    {
        for(ObjectParser objectParser : objectParsers)
        {
            if(objectParser instanceof Database)
            {
                if(((Database)objectParser).getNom().equals(nomDB))
                    return (Database)objectParser;
            }
        }
        return null;
    }
}
