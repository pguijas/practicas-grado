package e2_e3;

public enum paises{
    //No uso el valor iso, pero la profesora me dijo que lo dejase
    Austria ("AT"), Belgium ("BE"), Cyprus ("CY"), Netherlands ("NL"), Estonia ("EE"), Finland ("FI"), France ("FR"), 
    Germany ("DE"), Greece ("GR"), Ireland ("IE"), Italy ("IT"), Latvia ("LV"), Lithuania ("LT"), Luxembourg ("LU"), 
    Malta ("MT"), Monaco ("MC"), Portugal ("PT"), SanMarino ("SM"), Slovakia ("SK"), Slovenia ("SI"), Spain ("ES"), 
    VaticanCity ("VA");
        
    private String iso;
        
    private paises (String iso){
        this.iso=iso;
    }

    public String getIso(){
        return iso;
    }

    
}
