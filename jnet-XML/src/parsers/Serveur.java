package parsers;

public class Serveur implements ObjectParser{
    public int port;
    public String nom;
    public String info;
    public int size;
    public boolean pool;
    private boolean ssl;
    private boolean objQuery;
    private String ip;

    public Serveur() {
    }

    public Serveur(int port, String nom, String info, int size, boolean pool) {
        this.port = port;
        this.nom = nom;
        this.info = info;
        this.size = size;
        this.pool = pool;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public void setSsl(boolean ssl)
    {
        this.ssl = ssl;
    }

    public boolean isObjQuery()
    {
        return objQuery;
    }

    public void setObjQuery(boolean objQuery)
    {
        this.objQuery = objQuery;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isPool() {
        return pool;
    }

    public void setPool(boolean pool) {
        this.pool = pool;
    }

    @Override
    public String toString()
    {
        return "Serveur{" +
                "port=" + port +
                ", nom='" + nom + '\'' +
                ", info='" + info + '\'' +
                ", size=" + size +
                ", pool=" + pool +
                ", ssl=" + ssl +
                ", objQuery=" + objQuery +
                ", ip='" + ip + '\'' +
                '}';
    }
}
