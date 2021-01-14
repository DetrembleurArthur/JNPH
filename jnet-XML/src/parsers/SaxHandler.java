package parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SaxHandler extends DefaultHandler {

    protected String nomFichierXML;
    int cptTags =0;
    public ArrayList<ObjectParser> list=new ArrayList<>();
    String tmpelement="";

    public ArrayList<ObjectParser> getList() {
        return list;
    }

    public void setList(ArrayList<ObjectParser> list) {
        this.list = list;
    }

    public SaxHandler()
    {
        setNomFichierXML(null);
    }
    public SaxHandler(String nfx)
    {
        setNomFichierXML(nfx);
    }

    public SaxHandler(String nomFichierXML, ArrayList<ObjectParser> list) {
        this.nomFichierXML = nomFichierXML;
        this.list = list;
    }

    public String getNomFichierXML() { return nomFichierXML; }
    public void setNomFichierXML(String nfx) { nomFichierXML=nfx; }

    static protected void trace (String s)
    {
        System.out.println(s);
    }
    static protected void trace (String sCte, String s)
    {
        System.out.println(sCte + " : " + s);
    }

    static protected void trace (String sCte, int i)
    {
        System.out.println(sCte + " : " + i);
    }

    // Quelques méthodes du ContentHandler
    public void characters(char[] ch, int start,int length) throws SAXException
    {
        String chaine = new String(ch, start, length).trim();
        if (chaine.length() > 0) {
            trace("@ Caractères", chaine);
            trace("@element: ",tmpelement);
            if (tmpelement.equals("noms")) {
                Serveur serveur = (Serveur) list.get(list.size() - 1);
                serveur.setNom(chaine);
            }
            else if(tmpelement.equals("database"))
            {
                Database database=(Database)list.get(list.size()-1);
                database.setNom(chaine);
            }
            else if(tmpelement.equals("infos_root"))
            {
                Serveur serveur = (Serveur) list.get(list.size() - 1);
                serveur.setInfo(chaine);
            }
            else if(tmpelement.equals("connector"))
            {
                Serveur serveur = (Serveur) list.get(list.size() - 1);
                serveur.setPort(Integer.parseInt(chaine));
            }
        }

    }

    public void startDocument()throws SAXException
    {
        trace("** Début du document **");
    }
    public void endDocument()throws SAXException
    {
        trace("** Fin du document **");
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes attr) throws SAXException
    {
        trace("* Début d'un élément");
        cptTags++;

        if(qName.equals("serveur"))
        {
            list.add(new Serveur());
            tmpelement="serveur";
        }
        else if(qName.equals("database"))
        {
            Database tmp=new Database(attr.getValue(0),attr.getValue(1));
            list.add(tmp);
            tmpelement="database";
        }
        else if(qName.equals("connector"))
        {
            tmpelement="connector";
            Serveur serveur=(Serveur)list.get(list.size()-1);

            String pool = attr.getValue("pool");
            String size = attr.getValue("size");
            String ssl = attr.getValue("ssl");
            String objQuery = attr.getValue("objQuery");
            String ip = attr.getValue("ip");

            serveur.setPool(pool != null && pool.equals("true"));
            serveur.setIp(ip != null ? ip : "");
            serveur.setSsl(ssl != null && ssl.equals("true"));
            serveur.setSize(size != null ? Integer.parseInt(size) : 5);
            serveur.setObjQuery(objQuery != null && objQuery.equals("true"));
        }
        else if(qName.equals("infos_root"))
        {
            tmpelement="infos_root";
        }
        else if(qName.equals("noms"))
        {
            tmpelement="noms";
        }
        else
            tmpelement="other";
        /*
        trace("++ compteur de tags", cptTags);
        if (uri != null && uri.length()>0) trace(" uri", uri);
        trace(" nom complet all", qName);
        if (uri != null && uri.length()>0) trace(" nom complet", qName);
        int nAttr = attr.getLength();
        trace(" nombre d'attributs", nAttr);
        if (nAttr ==0) return; // Denys like
        for (int i=0; i<nAttr; i++)
            trace(" attribut n°" + i + " = " + attr.getLocalName(i) +
                    " avec valeur : " + attr.getValue(i));*/
    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException
    {
        trace("* Fin de l'élément " + localName);
        cptTags++;
        trace("++ compteur de tags", cptTags);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        trace("!!!WARNING!!! ",e.toString());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        trace("!!!error!!! ",e.toString());
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        trace("!!!fatalError!!! ",e.toString());
    }

}
