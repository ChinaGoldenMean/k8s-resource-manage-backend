package com.pubinfo.resource.model.constant;

public class K8sParam {
  public final static String NAMESPACE_ALL = "all,";
  
  private K8sParam() {
  
  }
  
  public static class Search {
    private Search() {
    
    }
    
    public final static String createTimeStamp = "createTimeStamp";
    public final static String totalItems = "totalItems";
  }
  
  public final static String WARNING = "Warning";
  
  public static class ListParam {
    private ListParam() {
    
    }
    
    public final static String pretty = "true";
    public final static Boolean allowWatchBookmarks = null;
    public final static String _continue = null;
    public final static String fieldSelector = null;
    public final static String labelSelector = null;
    public final static Integer limit = null;
    public final static String resourceVersion = null;
    public final static Integer timeoutSeconds = null;
    public final static Boolean watch = false;
  }
  
  public static class ReadParam {
    private ReadParam() {
    
    }
    
    public final static String pretty = "true";
    public final static Boolean exact = true;
    public final static Boolean export = null;
  }
  
  public static class PatchParam {
    private PatchParam() {
    
    }
    
    public final static String pretty = "true";
    public final static String dryRun = null;
    public final static String fieldManager = null;
    public final static Boolean force = false;
  }
  
  public static class CreateParam {
    private CreateParam() {
    
    }
    
    public final static String pretty = "true";
    public final static String dryRun = null;
    public final static String fieldManager = null;
  }
  
  public static class ReplaceParam {
    private ReplaceParam() {
    
    }
    
    public final static String pretty = "true";
    public final static String dryRun = null;
    public final static String fieldManager = null;
  }
  
  public static class DeleteParam {
    private DeleteParam() {
    
    }
    
    public final static String pretty = "false";
    public final static String dryRun = null;
    public final static Integer gracePeriodSeconds = 0;
    public final static Boolean orphanDependents = false;
    public final static String propagationPolicy = null;
  }
}
