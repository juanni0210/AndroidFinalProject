/**
 * This is the class of model for CarDatabase in this app
 * @author Sophie Sun
 * @since 1.0
 */

package com.cst2335.finalproject;

public class Model {
    private String model;
    private String make;
    private String modelID;
    private String makeID;
    private long id;

    public Model(String make, String model, long id){
        this.make = make;
        this.model = model;
        this.id = id;
    }

    public Model(String make, String model, String makeID, String modelID){
        this.make = make;
        this.model = model;
        this.makeID = makeID;
        this.modelID = modelID;
    }

    public String getModel(){ return model;}
    public String getMake(){return make;}

    public String getModelID() {
        return modelID;
    }

    public String getMakeID() {
        return makeID;
    }

    public long getId(){
        return id;
    }

}
