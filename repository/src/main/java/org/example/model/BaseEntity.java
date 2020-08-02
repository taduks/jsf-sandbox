package org.example.model;

public abstract class BaseEntity {
        
    private final Class<? extends BaseEntity> domainClass;
    private final String defaultSelector;
    
    private Integer id = null;
    
    
    public BaseEntity(Class<? extends BaseEntity> domainClass, String defaultSelector) {
    	this.domainClass = domainClass;
    	
    	if (defaultSelector == null) {
    		defaultSelector = "Id";
    	}
    	
    	this.defaultSelector = defaultSelector;
    	
    	initValues();
    }

    public void initValues() {
    }            


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }


	public Class<? extends BaseEntity> getDomainClass() {
		return domainClass;
	}

	public String getDefaultSelector() {
		return defaultSelector;
	}

}
